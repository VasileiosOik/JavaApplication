package com.connection.configuration;

import org.apache.ibatis.session.SqlSessionException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@Component
public class SqlSessionConfigurer {

    @Autowired
    private DataSource dataSource;

    public SqlSessionConfigurer(){}

    public SqlSessionFactory sqlSessionFactory(Resource myBatisConfigFile) {
        return this.sqlSessionFactory(this.dataSource, myBatisConfigFile);
    }

    private SqlSessionFactory sqlSessionFactory(DataSource dataSource, Resource myBatisConfigFile) {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(dataSource);
        factoryBean.setVfs(SpringBootVFS.class);
        factoryBean.setConfigLocation(myBatisConfigFile);

        try {
            return factoryBean.getObject();
        } catch (Exception e) {
            throw new SqlSessionException(e.getMessage());
        }
    }
}
