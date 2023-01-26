package com.markcha.scheduler.repository;

import com.markcha.scheduler.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDataRepository extends JpaRepository<Order, Long> {

}
