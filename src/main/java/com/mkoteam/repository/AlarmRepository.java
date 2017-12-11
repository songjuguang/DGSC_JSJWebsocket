package com.mkoteam.repository;

import com.mkoteam.entity.AlarmData;
import com.mkoteam.entity.ResultData;
import com.mkoteam.until.DateUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


public interface AlarmRepository extends JpaRepository<AlarmData, String> {

    List<AlarmData> findByCid(String cid);


    @Query(value = "SELECT COUNT(DISTINCT cid) as total FROM SC_JSJ_AlarmData WHERE groupId = ?1 AND status<>3", nativeQuery = true)
    Integer findAlarmDataCount(String groupId);

    @Query(value = "SELECT status FROM SC_JSJ_AlarmData WHERE cid = ?1 AND status = 2 LIMIT 1", nativeQuery = true)
    String findAlarmStatus(String cid);


    @Query(value = "SELECT DISTINCT x.cid,COUNT(1) AS total,y.jzName,x.jzLevel,x.jzPosition,z.unitName,q.status " +
            "FROM SC_WLW_SSSB x, SC_QY_JZXX y,SC_WLW_Unit z,SC_JSJ_AlarmData q WHERE q.groupId=?1 AND x.jzID=y.jzID AND x.SSSBCode=z.unitId AND q.cid=x.cid AND q.STATUS<>3 " +
            "GROUP BY cid", nativeQuery = true)
    List<Object> findAlarmData(String groupId);

    @Query(value = "SELECT x.cid,y.jzName,x.jzLevel,x.jzPosition,z.unitName, x.alarmType, q.datetime,q.status,q.pic1 " +
            "FROM SC_WLW_SSSB x, SC_QY_JZXX y,SC_WLW_Unit z,SC_JSJ_AlarmData q WHERE q.groupId=?1 AND q.cid=?2 AND q.status<>3 AND q.cid=x.cid AND x.jzID=y.jzID AND x.SSSBCode=z.unitId " +
            "GROUP BY q.status ORDER BY q.datetime", nativeQuery = true)
    List<Object> findAlarmInfo(String groupId, String cid);


    @Query(value = "SELECT count(*) FROM SC_JSJ_AlarmData WHERE groupId = ?1 AND cid=?2 AND status<>3 ", nativeQuery = true)
    Integer findAlarmInfoCount(String groupId, String cid);

    @Query(value = "SELECT a.datetime FROM SC_JSJ_AlarmData a WHERE a.groupId=?1 AND a.cid=?2 AND a.status<>3 ORDER BY a.datetime LIMIT 1", nativeQuery = true)
    Date findAlarmFirstTime(String groupId, String cid);

    @Query(value = "SELECT a.datetime FROM SC_JSJ_AlarmData a WHERE a.groupId=?1 AND a.cid=?2 AND a.status<>3 ORDER BY a.datetime DESC LIMIT 1", nativeQuery = true)
    Date findAlarmRecentTime(String groupId, String cid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SC_JSJ_AlarmData SET datetime = ?1, status=2 WHERE cid =?2 ORDER BY datetime DESC LIMIT 1 ", nativeQuery = true)
    void updateStatusNoticeBycid(Date da, String cid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SC_JSJ_AlarmData SET datetime = ?1,status = 3 WHERE cid = ?2 ", nativeQuery = true)
    void updateStatusNormalBycid(Date da, String cid);


    @Query(value = "SELECT x.cid,y.jzName,x.jzLevel,x.jzPosition,x.sbStatus,x.pic_addr FROM SC_WLW_SSSB x,SC_QY_JZXX y WHERE x.groupId=?1 AND x.jzID=y.jzID", nativeQuery = true)
    List<Object> findVideoSurveillanceList(String groupId);

    @Query(value = "SELECT count(*) FROM SC_WLW_SSSB WHERE groupId = ?1 GROUP by groupId", nativeQuery = true)
    Integer findVideoSurveillanceCount(String groupId);

    @Query(value = "SELECT x.cid,z.systemId,x.SSSBName,q.description,x.installDate,x.alarmType,x.brand,x.model,y.jzName,x.jzLevel,x.jzPosition,x.sbStatus,x.video_addr " +
            "FROM SC_WLW_SSSB x, SC_QY_JZXX y,SC_WLW_Unit z,SC_System_Group q WHERE x.cid=?1 AND x.jzID=y.jzID AND x.SSSBCode=z.unitId AND x.groupId=q.groupId", nativeQuery = true)
    List<Object> findVideoSurveillanceByCid(String cid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SC_WLW_SSSB SET status = 4 WHERE cid = ?1 ", nativeQuery = true)
    Integer webcamIsClose(String cid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SC_WLW_SSSB SET status = 1 WHERE cid = ?1 ", nativeQuery = true)
    Integer webcamIsOpen(String cid);

    @Query(value = "SELECT sbStatus FROM SC_WLW_SSSB WHERE cid=?1", nativeQuery = true)
    Integer findSbstatusByCid(String cid);


    @Query(value = "SELECT x.cid,COUNT(1),y.jzName,x.jzLevel,x.jzPosition,z.unitName, q.datetime,q.status,x.video_addr " +
            "FROM SC_WLW_SSSB x, SC_QY_JZXX y,SC_WLW_Unit z,SC_JSJ_AlarmData q " +
            "WHERE q.groupId='QYWX000010' AND q.cid=?1 AND q.cid=x.cid AND x.jzID=y.jzID AND x.SSSBCode=z.unitId " +
            "GROUP BY q.status ORDER BY q.datetime", nativeQuery = true)
    List<Object> findDeviceAlarmInfoList(String cid);


    @Query(value = "SELECT COUNT(DISTINCT status) AS total FROM SC_JSJ_AlarmData WHERE cid=?1", nativeQuery = true)
    Integer findDeviceAlarmInfoCount(String cid);


}

