package com.markcha.ems.repository;

import com.markcha.ems.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDataRepository extends JpaRepository<Order, Long> {

}
