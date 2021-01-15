package com.jhhc.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;

/**
 * @author xiaojiang
 * @date 2021/1/7 16:51
 */
@Configuration
@PropertySource(value = {"file:/opt/mcb/ircn/conf/make_volte.properties", "classpath:conf.properties"}, ignoreResourceNotFound = true)
public class Setting {

    public enum FaultContext {

        BLANK_FILE("0000", "空文件");

        private String code;
        private String desc;
        FaultContext(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String toString() {
            return this.code;
        }
    }

    @Value("${file.output.path}")
    private String fileOutPath;

    @Value("${create.thread.sleep}")
    private int createThreadSleep;

    @Value("${wait.io.close.sleep}")
    private int waitIoCloseSleep;

    @Value("${is.test:false}")
    private boolean isTest;

    @Value("${error.rate:5}")
    private int errorRate;

    @Value("${oracle.driver.class.name}")
    private String driverClassName;

    @Value("${oracle.url}")
    private String url;

    @Value("${oracle.username}")
    private String username;

    @Value("${oracle.password}")
    private String password;

    @Value("${oracle.maxActive}")
    private int maxActive;
    @Value("${oracle.maxWait}")
    private int maxWait;

    public int getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(int errorRate) {
        this.errorRate = errorRate;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public int getCreateThreadSleep() {
        return createThreadSleep;
    }

    public void setCreateThreadSleep(int createThreadSleep) {
        this.createThreadSleep = createThreadSleep;
    }

    public int getWaitIoCloseSleep() {
        return waitIoCloseSleep;
    }

    public void setWaitIoCloseSleep(int waitIoCloseSleep) {
        this.waitIoCloseSleep = waitIoCloseSleep;
    }

    public String getFileOutPath() {
        return fileOutPath;
    }

    public void setFileOutPath(String fileOutPath) {
        this.fileOutPath = fileOutPath;
    }

}
