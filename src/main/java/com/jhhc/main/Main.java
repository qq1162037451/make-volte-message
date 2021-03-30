package com.jhhc.main;

import com.jhhc.conf.DataSourceService;
import com.jhhc.conf.LogService;
import com.jhhc.conf.Setting;
import com.jhhc.conf.SpringConfig;
import com.jhhc.utils.DateUtils;
import com.jhhc.work.MakeData;
import com.jhhc.work.NewFileTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaojiang
 * @date 2021/1/7 16:50
 */

public class Main {

    private static final String ARGS_TIP = "java -jar make-volte-test-data.jar YYYYMMDD fileCount dataCount";

    /**
     * java -jar make-volte-test-data.jar instx YYYYMMDD fileCount dataCount
     * @param args
     */
    public static void main(String[] args) {
        // 加载spring环境
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        LogService log = context.getBean("logService", LogService.class);
        Setting setting = context.getBean("setting", Setting.class);
        DataSourceService dataSourceService = context.getBean("dataSourceService", DataSourceService.class);
        dataSourceService.loadDataSource();

        NewFileTest newFileTest = context.getBean("newFileTest", NewFileTest.class);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            list.add("测试测试测试测试测试测试测试测试测试测试测试测试测试测试" + i);
        }
        newFileTest.newFileTestMethod(list);
        System.exit(1);
        log.info("造数据程序启动...");
        if (!setting.isTest()) { // 测试阶段不验证参数
            if (args == null) {
                log.warn("启动参数为空，正确格式为：" + ARGS_TIP);
                System.exit(1);
            }
            if (args.length != 3) {
                log.warn("启动参数数量不为3,正确格式为：" + ARGS_TIP);
                System.exit(1);
            }
        }
        MakeData makeData = context.getBean("makeData", MakeData.class);
        try {
//            if (setting.isTest())
//                Thread.sleep(20000);
            if (!setting.isTest()) {
                try {
                    Integer fileCount = Integer.valueOf(args[1]);
                    Integer dataCount = Integer.valueOf(args[2]);
                    DateUtils.parseYYYYMMDD(args[0]);
                    makeData.makeData(fileCount, dataCount, args[0]);
                } catch (Exception e) {
                    log.warn("启动参数输入不正确,正确格式为：" + ARGS_TIP);
                }
            } else {
                makeData.makeData(10, 400, DateUtils.formatYYYYMMDD(new Date()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
