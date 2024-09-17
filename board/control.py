# -*- coding: utf-8 -*-
import sys
import os
import time
import requests
import torch
import numpy as np
from math import floor
from tqdm import tqdm
from PIL import Image
from torchvision import transforms
import matplotlib.pyplot as plt
import datetime
import argparse
import logging

import json
from paho.mqtt import client as mqtt

from pathlib import Path
import torch.backends.cudnn as cudnn
from numpy import random

from models.experimental import attempt_load
from utils.datasets import LoadStreams, LoadImages
from utils.datasets import LoadSingleImage
from utils.general import check_img_size, check_requirements, check_imshow, non_max_suppression, apply_classifier, \
    scale_coords, xyxy2xywh, strip_optimizer, set_logging, increment_path
from utils.plots import plot_one_box
from utils.torch_utils import select_device, load_classifier, time_synchronized

import subprocess
import cv2
from PyQt5 import QtCore
from PyQt5.QtCore import QThread, Qt
from PyQt5.QtGui import QImage, QPixmap
from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QPushButton, QStyle, QHBoxLayout, QVBoxLayout


image = None


class FaceDetectThread(QThread):
    """
    人脸检测算法sdk调用线程
    """
    updatedImage = QtCore.pyqtSignal(int)

    def __init__(self, mw):
        self.mw = mw
        self.working = True
        QThread.__init__(self)

    def __del__(self):
        self.wait()

    def run(self):
        while self.working:
            if self.mw.AlgIsbasy == False and not (self.mw.limg is None):
                self.mw.AlgIsbasy = True
                limg = self.mw.limg
                # ret = self.mw.nlFaceDetect.NL_FD_InitVarIn(limg)
                ret = 0
                if ret == 0:
                    # ret = self.mw.nlFaceDetect.NL_FD_Process_C()  # 返回值是目标个数
                    ret = 0
                    height, width, bytesPerComponent = limg.shape
                    bytesPerLine = bytesPerComponent * width
                    rgb = cv2.cvtColor(limg, cv2.COLOR_BGR2RGB)
                    if ret > 0:
                        # 人脸检测结果输出
                        for i in range(self.mw.nlFaceDetect.djEDVarOut.num):
                            outObject = self.mw.nlFaceDetect.djEDVarOut.faceInfos[i].bbox
                            print("Total face:", self.mw.nlFaceDetect.djEDVarOut.num, " ID: ", i)
                            print('face box :%0.2f,%0.2f,%0.2f,%0.2f' % (outObject.x1, outObject.y1, 
                                                                         outObject.x2, outObject.y2))
                            print('Scores: %f' % outObject.score)

                            font = cv2.FONT_HERSHEY_SIMPLEX  # 定义字体
                            imgzi = cv2.putText(rgb, str('Face'), (int(outObject.x1), int(outObject.y1)), font, 0.8,
                                                (255, 0, 0), 2)
                            cv2.rectangle(rgb, (int(outObject.x1), int(outObject.y1)),
                                          (int(outObject.x2), int(outObject.y2)), (0, 0, 255), 2)
                    showImage = QImage(rgb.data, width, height, bytesPerLine, QImage.Format_RGB888)
                    self.mw.showImage = QPixmap.fromImage(showImage)
                    self.updatedImage.emit(self.mw.frameID)
                else:
                    print('Var Init Error code:', ret)
                    time.sleep(0.001)
                self.mw.AlgIsbasy = False
            else:
                time.sleep(0.001)
                
    def stop(self):
        if self.working:
            self.working = False
            print('FaceDetectThread Exit!')


class RealTimeThread(QThread):
    def __init__(self, mw):
        self.mw = mw
        QThread.__init__(self)

    def run(self):
        global image
        while True:
            ret, image = self.mw.mw.cap.read()
            time.sleep(1.0 / 50)


class CameraThread(QThread):
    updatedM = QtCore.pyqtSignal(int)

    def __init__(self, mw):
        self.mw = mw
        self.working = True
        QThread.__init__(self)

    def __del__(self):
        self.wait()

    def run(self):
        global image
        # ret, image = self.mw.cap.read()  # 获取第一帧图像
        # self.latest_th = RealTimeThread(self)
        # self.latest_th.start()
        while self.working:
            QApplication.processEvents()
            if not self.mw.CapIsbasy:
                # 图像采用线程采集，确保最新
                # 上传至服务器
                ret, image = self.mw.cap.read()  # 获取当前图像
                with torch.no_grad():
                    path, img, im0s, vid_cap = LoadSingleImage(image)
                    image_res = detect(path, img, im0s, vid_cap)
                # img_bgr = cv2.cvtColor(image_res, cv2.COLOR_RGB2BGR)
                _, img_encoded = cv2.imencode(".jpg", image_res)  # 将处理后的帧转换为JPEG格式
                # _, img_encoded = cv2.imencode(".jpg", image)  # 将原帧转换为JPEG格式
                try:
                    # 发送到服务器, 设置超时时间防止阻塞, 如果服务器性能一般般尽量不要发太快
                    response = requests.post("http://***.***.***.***:8000/upload", data=img_encoded.tostring(),
                                             timeout=0.5)  # 发送图像数据
                    if response.status_code != 200:
                        print("接口名字是不是写错了")
                except:
                    pass
                
            else:
                time.sleep(1.0 / 50)
                
    def stop(self):
        if self.working:
            self.working = False
            print('')


# 设置qt显示窗口
class VideoBox(QWidget):
    """
    显示界面
    """
    def __init__(self, libNamePath, configPath, capWidth, capHeight):
        QWidget.__init__(self)
        self.setWindowFlags(Qt.CustomizeWindowHint)
        self.move(0, 0)
        self.label_show_camera = QLabel()
        self.label_show_camera.setObjectName("Picture")
        self.label_show_camera.setScaledContents(True)  # 将显示框进行填充，画面会进行缩放

        # 设置停止按钮组件
        self.stop_btn = QPushButton("Stop")
        self.stop_btn.setMaximumSize(QtCore.QSize(150, 60))
        self.stop_btn.setEnabled(True)
        self.stop_btn.setIcon(self.style().standardIcon(QStyle.SP_MediaPlay))
        self.stop_btn.clicked.connect(self.stop_btn_func)

        # 设置开始按钮组件 QPushButton
        self.start_btn = QPushButton("start")
        self.start_btn.setMaximumSize(QtCore.QSize(150, 60))
        self.start_btn.setEnabled(True)
        self.start_btn.setIcon(self.style().standardIcon(QStyle.SP_MediaPlay))
        self.start_btn.clicked.connect(self.start_btn_func)

        # 设置按键大小边框 QHBoxLayout
        control_box = QHBoxLayout()
        control_box.setContentsMargins(0, 0, 0, 0)
        control_box.addWidget(self.start_btn)
        control_box.addWidget(self.stop_btn)

        layout = QVBoxLayout()
        layout.addWidget(self.label_show_camera)
        layout.addLayout(control_box)
        self.setLayout(layout)

        # 设置双线程
        self.frameID = 0
        self.CapIsbasy = False
        self.AlgIsbasy = False

        # 设计视频采集参数
        self.cap = cv2.VideoCapture(0)  # 打开摄像头
        self.cap.set(cv2.CAP_PROP_FRAME_WIDTH, capWidth)  # 璁剧疆鎽勫儚澶村垎杈ㄧ巼瀹藉害
        self.cap.set(cv2.CAP_PROP_FRAME_HEIGHT, capHeight)  # 璁剧疆鎽勫儚澶村垎杈ㄧ巼楂樺害
        self.cap.set(cv2.CAP_PROP_BUFFERSIZE, 1)
        self.showImage = None
        self.limg = None

        # 调用算法类
        self.configPath = configPath  # 配置文件路径
        self.libNamePath = libNamePath  # 动态库
        # self.nlFaceDetect = NLFaceDetect(self.libNamePath)  # 实例化算法类
        # self.nlFaceDetect.NL_FD_ComInit(self.configPath)  # 初始化

    def showframe(self):
        # 显示视频流
        self.label_show_camera.setPixmap(self.showImage)

    def start_btn_func(self):
       
        self.start_btn.setEnabled(False)
        self.stop_btn.setEnabled(True)
        # 线程1相机采集
        self.camera_th = CameraThread(self)
        # self.camera_th.updatedM.connect(self.showframe)
        self.camera_th.start()

        # 线程2算法处理
        # self.face_detect_th = FaceDetectThread(self)
        # self.face_detect_th.updatedImage.connect(self.showframe)
        # self.face_detect_th.start()

    def stop_btn_func(self):
        """
        停止按钮
        :return:
        """
        self.stop_btn.setEnabled(False)
        self.start_btn.setEnabled(True)
        try:
            self.camera_th.stop()
            self.camera_th.quit()
            self.face_detect_th.stop()
            self.face_detect_th.quit()
            del self.camera_th
            del self.face_detect_th
        except Exception as e:
            pass


def detect(path, img, im0s, vid_cap):

    # Run inference
    img = torch.from_numpy(img).to(device)
    img = img.float()  # uint8 to fp16/32
    img /= 255.0  # 0 - 255 to 0.0 - 1.0
    if img.ndimension() == 3:
        img = img.unsqueeze(0)

    # Inference
    t1 = time_synchronized()
    pred = model(img, augment=False)[0]

    # Apply NMS
    pred = non_max_suppression(pred, 0.45, 0.5, classes=None, agnostic=False)
    t2 = time_synchronized()

    # Process detections
    for i, det in enumerate(pred):  # detections per image
        p, s, im0, frame = path, '', im0s, None

        s += '%gx%g ' % img.shape[2:]  # print string
        gn = torch.tensor(im0.shape)[[1, 0, 1, 0]]  # normalization gain whwh
        mqtt_message = {}
        if len(det):
            # Rescale boxes from img_size to im0 size
            det[:, :4] = scale_coords(img.shape[2:], det[:, :4], im0.shape).round()

            # Print results
            for c in det[:, -1].unique():
                n = (det[:, -1] == c).sum()  # detections per class
                s += f"{n} {names[int(c)]}{'s' * (n > 1)}, "  # add to string

            # Write results
            for *xyxy, conf, cls in reversed(det):
                # label = f'{names[int(cls)]} {conf:.2f}'
                label = f'{names[int(cls)]}'
                if names[int(cls)] in mqtt_message:
                    mqtt_message[names[int(cls)]] += 1
                else:
                    mqtt_message[names[int(cls)]] = 1
                plot_one_box(xyxy, im0, label=label, color=colors[int(cls)], line_thickness=3)

        if len(mqtt_message) > 0:
            mqtt_client.send_message(json.dumps(mqtt_message), MQTT['TOPIC'])

        # Print time (inference + NMS)
        print(f'{s}Done. ({t2 - t1:.3f}s)')

        return im0


# MQTT 配置
MQTT = {
    'HOST': '***.***.***.***',
    'PORT': 1883,
    'TOPIC': '/send'
}


class MQTTClientWrapper:
    def __init__(self, host, port, topic):
        self.client = mqtt.Client()
        self.host = host
        self.port = port
        self.topic = topic
        self.is_connected = False
        self.client.on_connect = self.on_connect
        self.client.on_disconnect = self.on_disconnect

    def connect(self):
        try:
            self.client.connect(self.host, self.port, keepalive=60)
            self.client.loop_start()
        except Exception as e:
            print("连接失败 ", str(e))

    def on_connect(self, client, userdata, flags, rc):
        print("已连接，结果代码为 " + str(rc))
        self.is_connected = True

    def on_disconnect(self, client, userdata, rc):
        print("已断开连接，结果代码为 " + str(rc))
        self.is_connected = False

    def send_message(self, msg, topic=None):
        publish_topic = topic if topic else self.topic
        if self.is_connected:
            self.client.publish(publish_topic, msg, qos=0)
        else:
            print("未连接或连接异常")


if __name__ == "__main__":
    mqtt_client = MQTTClientWrapper(MQTT['HOST'], MQTT['PORT'], MQTT['TOPIC'])
    mqtt_client.connect()
    source, weights, view_img, save_txt, imgsz = 'sample', 'weights/best.pt', False, False, 640
    # Initialize
    set_logging()
    device = select_device('')
    half = device.type != 'cpu'  # half precision only supported on CUDA

    # Load model
    with torch.no_grad():
        model = attempt_load(weights, map_location=device)  # load FP32 model
        stride = int(model.stride.max())  # model stride
        imgsz = check_img_size(imgsz, s=stride)  # check img_size
        if half:
            model.half()  # to FP16

        # Get names and colors
        names = model.module.names if hasattr(model, 'module') else model.names
        print(str(names))
        colors = [[random.randint(0, 255) for _ in range(3)] for _ in names]

    app = QApplication(sys.argv)
    configPath = b"rk3399_AI_model"  # 指定模型以及配置文件路径
    libNamePath = 'C:\\Users\DarkQuantum\\Desktop\\pydemolibNL_faceEnc.4.6.so'  # 指定库文件路径
    box = VideoBox(libNamePath, configPath, 640, 480)
    box.show()
    sys.exit(app.exec_())