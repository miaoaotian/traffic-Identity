package com.miaoaotian.smallcar.mapper;

import com.miaoaotian.smallcar.pojo.AllActionVO;
import com.miaoaotian.smallcar.pojo.AllSignalVO;
import com.miaoaotian.smallcar.pojo.DriveTypeActionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface AllMapper {
    @Select("""
        SELECT
          CASE
            WHEN action_type = 0 THEN '停车'
            WHEN action_type = 1 THEN '行驶'
          END as action_description,
          create_time
        FROM drive_type
        ORDER BY create_time DESC
    """)
    List<DriveTypeActionVO> getDriveTypeActions();
    @Select("""
        SELECT
            CASE obj_type
                WHEN 0 THEN '直行'
                WHEN 1 THEN '左转'
                WHEN 2 THEN '右转'
                WHEN 3 THEN '减速'
                WHEN 4 THEN '停车'
                ELSE '未知'
            END AS obj_type_cn,
            COUNT(*) AS count
        FROM
            objects
        GROUP BY
            obj_type;
    """)
    List<AllActionVO> getAllActions();

    @Select("SELECT signal_type,count(*) AS count FROM traffic_signal GROUP BY signal_type")
    List<AllSignalVO> getAllSignals();

    @Insert("INSERT INTO objects (obj_type) VALUES (#{objType})")
    void insertObject(@Param("objType") int objType);

    @Insert("INSERT INTO drive_type (action_type) VALUES (#{actionType})")
    void insertDriveType(@Param("actionType") int actionType);

    @Insert("INSERT INTO traffic_signal (signal_type) VALUES (#{signal})")
    void insertTrafficSignal(@Param("signal") String signal);
}
