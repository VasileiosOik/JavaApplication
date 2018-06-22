package com.connection.application;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBootMyBatisCompatabilityConfiguration {

    @Autowired
    private SqlSessionFactory companySqlSessionFactory;

    @Bean
    public SqlSessionFactory sqlSessionFactory() {
        return  companySqlSessionFactory;
    }
}
