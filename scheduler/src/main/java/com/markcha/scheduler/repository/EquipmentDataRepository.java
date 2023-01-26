package com.markcha.scheduler.repository;


import com.markcha.scheduler.domain.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentDataRepository extends JpaRepository<Equipment, Long> {
//    @Query("SELECT eq from Equipment eq where " +
//            "eq.type = ?1 AND " +
//            "eq.model = ?2")
    Equipment getOneByTypeAndModel(String tpye, String model);
}
