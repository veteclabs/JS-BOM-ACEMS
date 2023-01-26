package com.markcha.ems.repository;


import com.markcha.ems.domain.Device;
import com.markcha.ems.domain.Equipment;
import com.markcha.ems.repository.equipment.EquipmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EquipmentDataRepository extends JpaRepository<Equipment, Long>, EquipmentRepository {
//    @Query("SELECT eq from Equipment eq where " +
//            "eq.type = ?1 AND " +
//            "eq.model = ?2")
    Equipment getOneByTypeAndModel(String tpye, String model);
}
