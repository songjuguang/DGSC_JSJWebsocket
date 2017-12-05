package com.mkoteam.repository;

import com.mkoteam.entity.WarningData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


public interface WarningRepository extends JpaRepository<WarningData,String> {

    WarningData findByCid(String cid);
}

