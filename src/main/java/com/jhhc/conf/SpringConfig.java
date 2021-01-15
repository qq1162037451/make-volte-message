package com.jhhc.conf;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.InputStream;

/**
 * @author xiaojiang
 * @date 2021/1/7 16:58
 */
@Configuration
@ComponentScan(basePackages = {"com.jhhc"})
public class SpringConfig {

}
