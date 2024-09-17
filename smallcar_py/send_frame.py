import uvicorn
from fastapi import FastAPI, Request, WebSocket
from fastapi.responses import HTMLResponse, StreamingResponse
from fastapi.middleware.cors import CORSMiddleware
import cv2
import numpy as np

app = FastAPI()
# 允许所有来源
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
# 用于存储客户端WebSocket连接的列表
client_websockets = []

# 客户端WebSocket连接的路由
@app.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()
    client_websockets.append(websocket)
    try:
        while True:
            # 接收并忽略来自客户端的所有消息
            await websocket.receive()
    finally:
        # 从列表中删除已断开的WebSocket连接
        client_websockets.remove(websocket)

# 用于接收客户端上传的视频流并推送到所有WebSocket连接
@app.post("/upload")
async def upload_video(request: Request):
    # 从请求正文中读取视频流
    content = await request.body()
    nparr = np.frombuffer(content, np.uint8)
    frame = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    # 将视频帧转换为JPEG格式
    ret, buffer = cv2.imencode('.jpg', frame)
    jpg_as_text = buffer.tobytes()

    # 推送视频帧到所有WebSocket连接
    for websocket in client_websockets:
        await websocket.send_bytes(jpg_as_text)

    # 返回成功响应
    return {"status": "ok"}
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)