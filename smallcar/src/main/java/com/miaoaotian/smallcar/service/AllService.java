package com.miaoaotian.smallcar.service;

import com.miaoaotian.smallcar.mapper.AllMapper;
import com.miaoaotian.smallcar.pojo.AllActionVO;
import com.miaoaotian.smallcar.pojo.AllSignalVO;
import com.miaoaotian.smallcar.pojo.DriveTypeActionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllService {
    @Autowired
    private AllMapper allMapper;
    public List<DriveTypeActionVO> getDriveTypeActions() {
        return allMapper.getDriveTypeActions();
    }
    public List<AllActionVO> getAllActions() {
        return allMapper.getAllActions();
    }
    public List<AllSignalVO> getAllSignals() {
        return allMapper.getAllSignals();
    }

}
