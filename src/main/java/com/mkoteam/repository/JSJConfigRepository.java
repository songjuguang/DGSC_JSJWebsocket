package com.mkoteam.repository;

import com.mkoteam.entity.AlarmData;
import com.mkoteam.entity.JSJConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface JSJConfigRepository extends JpaRepository<JSJConfig,Integer> {

    @Query(value = "SELECT * FROM SC_JSJ_Config WHERE groupId=?1 ", nativeQuery = true)
    JSJConfig findByGroupId(String groupId);

}
