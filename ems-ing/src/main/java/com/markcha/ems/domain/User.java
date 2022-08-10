package com.markcha.ems.domain;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name ="users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;
    @Column(length = 80)
    private String username;
    @Column(length = 80)
    private String password;
    private Boolean verified;
    @Column(length = 15)
    private String create_ip;
    @Column(length = 50)
    private String email;
    @Column(length = 50)
    private String phone;
    private Boolean isAlarm;
    @Column(name="last_password_changed", columnDefinition = "DATETIME")
    private LocalDateTime lastPasswordChanged;
    @Column(name="last_update_ip", length = 15)
    private String lastUpdateIp;
    @Column(name="withdraw_dtm", columnDefinition = "DATETIME")
    private LocalDateTime withdrawDtm;
    @Column(name="enable_marketing_At", columnDefinition = "DATETIME")
    private LocalDateTime enableMarketingAt;
    @Column(name="disable_marketing_At", columnDefinition = "DATETIME")
    private LocalDateTime disbleMarketingAt;
    @Column(name="updatedAt", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;
    @Column(name="createdAt", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
}
