package com.jhhc.conf;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author xiaojiang
 * @date 2021/1/8 15:18
 */
@Component
public class LogService {

    private final Logger log = LoggerFactory.getLogger(LogService.class);

    public void info(String s) {
        log.info(s);
    }

    public void debug(String s) {
        log.debug(s);
    }

    public void warn(String s) {
        log.warn(s);
    }

    public void error(String s) {
        log.error(s);
    }
}
