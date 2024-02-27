package com.activiti.z_six;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.activiti.z_six.mapper")
@MapperScan("com.activiti.z_six.excLog.dao")
@MapperScan("com.activiti.z_six.operLog.dao")
@SpringBootApplication
public class ZSixApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZSixApplication.class, args);
    }
}
