package com.markcha.scheduler.dto.order;

import com.markcha.scheduler.domain.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class OrderDto {
    private Long groupId;
    private Integer order;
    public OrderDto(Order order) {
        this.order = order.getOrder();
        if(!isNull(order.getGroup())) {
            groupId = order.getGroup().getId();
        }
    }
}
