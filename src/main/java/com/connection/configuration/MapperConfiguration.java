package com.connection.configuration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@MapperScan(value = "com.connection.mapper", sqlSessionFactoryRef = "companySqlSessionFactory")
public class MapperConfiguration {

    private final SqlSessionConfigurer sqlSessionConfigurer;

    @Autowired
    public MapperConfiguration(SqlSessionConfigurer sqlSessionConfigurer) {
        this.sqlSessionConfigurer = sqlSessionConfigurer;
    }

    @Value(value = "classpath:/myBatis-config.xml")
    private Resource myBatisConfig;

    @Bean
    public SqlSessionFactory companySqlSessionFactory() {
        return sqlSessionConfigurer.sqlSessionFactory(myBatisConfig);
    }
}
