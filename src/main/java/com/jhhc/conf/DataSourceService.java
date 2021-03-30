package com.jhhc.conf;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * @author xiaojiang
 * @date 2021/1/12 13:54
 */
@Repository
public class DataSourceService extends Setting{

    @Autowired
    private Setting setting;

    private DruidDataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 初始化template
     */
    public void loadDataSource() {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName(setting.getDriverClassName());
        dataSource.setUrl(setting.getUrl());
        dataSource.setUsername(setting.getUsername());
        dataSource.setPassword(setting.getPassword());
        dataSource.setMaxActive(setting.getMaxActive());
        dataSource.setMaxWait(setting.getMaxWait());
        dataSource.setDefaultAutoCommit(true);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
