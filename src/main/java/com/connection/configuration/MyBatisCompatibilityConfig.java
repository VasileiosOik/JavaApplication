package com.connection.configuration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisCompatibilityConfig {

    private final SqlSessionFactory companySqlSessionFactory;

    @Autowired
    public MyBatisCompatibilityConfig(SqlSessionFactory companySqlSessionFactory) {
        this.companySqlSessionFactory = companySqlSessionFactory;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() {
        return  companySqlSessionFactory;
    }
}
