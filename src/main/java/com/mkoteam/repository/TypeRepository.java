package com.mkoteam.repository;

import com.mkoteam.entity.TypeConversion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<TypeConversion,String> {
}
