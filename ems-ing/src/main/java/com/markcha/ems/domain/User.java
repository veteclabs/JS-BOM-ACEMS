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
    @Column(name="timestamp", columnDefinition = "DATETIME")
    private LocalDateTime lastPasswordChanged;
    @Column(length = 15)
    private String lastUpdateIp;
}
