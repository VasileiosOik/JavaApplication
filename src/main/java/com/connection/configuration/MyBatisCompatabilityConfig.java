package com.connection.configuration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisCompatabilityConfig {

    private final SqlSessionFactory companySqlSessionFactory;

    @Autowired
    public MyBatisCompatabilityConfig(SqlSessionFactory companySqlSessionFactory) {
        this.companySqlSessionFactory = companySqlSessionFactory;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() {
        return  companySqlSessionFactory;
    }
}
