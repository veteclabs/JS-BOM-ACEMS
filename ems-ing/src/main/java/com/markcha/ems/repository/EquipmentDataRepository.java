package com.markcha.ems.repository;


import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EquipmentDataRepository extends JpaRepository<Equipment, Long> {
//    @Query("SELECT eq from Equipment eq where " +
//            "eq.type = ?1 AND " +
//            "eq.model = ?2")
    Equipment getOneByTypeAndModel(String tpye, String model);
}
