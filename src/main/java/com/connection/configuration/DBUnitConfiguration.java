package com.connection.configuration;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Configuration
public class DBUnitConfiguration {

    private final DataSource dataSource;

    @Autowired
    public DBUnitConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
        DatabaseConfigBean bean = new DatabaseConfigBean();
        bean.setDatatypeFactory(new MySqlDataTypeFactory());
        bean.setQualifiedTableNames(Boolean.TRUE);


        DatabaseDataSourceConnectionFactoryBean dbConnectionFactory = new DatabaseDataSourceConnectionFactoryBean(dataSource);
        dbConnectionFactory.setDatabaseConfig(bean);
     //   dbConnectionFactory.setSchema("testdb2");
        return dbConnectionFactory;
    }
}
