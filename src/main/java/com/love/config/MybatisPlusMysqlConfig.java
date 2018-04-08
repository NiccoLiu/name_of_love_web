package com.love.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;

/**
 * MybatisPlus 配置
 * 
 * @author liuxinq
 */
@Configuration
@MapperScan(basePackages = {"mapper"})
public class MybatisPlusMysqlConfig {

    @Autowired
    MysqlDruidProperties mysqlDruidProperties;

    /**
     * mybatis-plus分页插件
     */
    @Bean
    @Primary
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setDialectType(DBType.MYSQL.getDb());
        return paginationInterceptor;
    }

    /**
     * druid数据库连接池
     */
    @Bean(initMethod = "init")
    @Primary
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        mysqlDruidProperties.coinfig(dataSource);
        return dataSource;
    }



}
