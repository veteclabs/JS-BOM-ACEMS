package com.markcha.ems.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name ="users")
public class User {
    @Id @Column(name="user_id")
    private Long id;
    private String username;
    private String password;
    private Boolean verified;
    private String create_ip;
    private String email;
    private String phone;
    private Boolean isAlarm;
    private LocalDateTime lastPasswordChanged;
    private String lastUpdateIp;
    private String withDrawDtm;
    private String enableMarketingAt;
    private String disableMarketingAt;
    private String updateAt;
    private String createAt;
}
