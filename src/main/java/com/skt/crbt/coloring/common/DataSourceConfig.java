package com.skt.crbt.coloring.common;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource crbtStsDataSource() {
        return dataSource(DbConstants.DB_CRBT_STS);
    }

    @Bean
    public DataSource crbtDataSource() {
        return dataSource(DbConstants.DB_CRBT);
    }

    @Bean
    public DataSource copystsDataSource() {
        return dataSource(DbConstants.DB_COPYSTS);
    }

    @Bean
    public DataSource couplringDataSource() {
        return dataSource(DbConstants.DB_COUPLRING);
    }

    @Bean
    @Primary
    public JdbcTemplate crbtStsJdbcTemplate(@Qualifier("crbtStsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate crbtJdbcTemplate(@Qualifier("crbtDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate copystsJdbcTemplate(@Qualifier("copystsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate couplringJdbcTemplate(@Qualifier("couplringDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    private DataSource dataSource(String database) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(DbConstants.jdbcUrl(database));
        ds.setUsername(DbConstants.USER);
        ds.setPassword(DbConstants.PASSWORD);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setPoolName("coloring-" + database);
        ds.setMaximumPoolSize(5);
        ds.setInitializationFailTimeout(-1);
        ds.setConnectionTimeout(5000);
        return ds;
    }
}
