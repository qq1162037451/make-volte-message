package com.jhhc.work;

import com.jhhc.bean.IoSubchg4AFileLog;
import com.jhhc.conf.DataSourceService;
import com.jhhc.conf.LogService;
import com.jhhc.conf.Setting;
import com.jhhc.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author xiaojiang
 * @date 2021/3/1 16:44
 */
@Component
public class NewFileTest {
    @Autowired
    private Setting setting;

    @Autowired
    private LogService log;

    @Autowired
    private DataSourceService dataSourceService;

    public void newFileTestMethod(List<String> list) {
        {
            final long fileSize = 5 * 1024 * 1024;
            try {
                String path = "E:/test";
                File directory = new File(path);
                if (!directory.exists() || !directory.isDirectory()) {
                    directory.mkdirs();
                }
                StringBuffer sqlBuffer = new StringBuffer();
                sqlBuffer.append("select max(FILE_NAME) from IRCNDBA.IO_SUBCHG_4A_FILE_LOG where data_code = '00' and file_size < " + fileSize);
                String fName = dataSourceService.getJdbcTemplate().queryForList(sqlBuffer.toString(), String.class).get(0);

//                String fName = ioSubchg4AFileLogDao.getFileNameLess5M(INTF_LOG);
//                log.info("本次查到小于5M的文件名：{}", fName);
                boolean insert = StringUtils.isBlank(fName) ? true : false;
                fName = insert ? "JTNGIRCN_" + DateUtils.formatYYYYMMDDYYYYMMDD(new Date()) + ".xml" : fName;
//                log.info("开始写入4A-接口日志数据，本次写入文件名{0}", fName);
                File file = new File(path + "/" + fName);
                Writer writer = null;
                BufferedWriter bw = null;
                try {
                    writer = new FileWriter(file, true);
                    bw = new BufferedWriter(writer);
                    if (insert) {
                        bw.write("<?xml version='1.0' encoding=’UTF-8’?>");
                        bw.newLine();
                        bw.write("<ROOT><![CDATA[");
                    }
                    for (String log : list) {
                        bw.newLine();
                        bw.write(log);
                    }
                    bw.flush();
                    if (file.length() >= fileSize) {
                        bw.write("]]></ROOT>");
                    }
                    bw.flush();
                    log.info("数据写入完成，将更新4A文件生成日志表");
                    if (insert) {
                        IoSubchg4AFileLog log4a = new IoSubchg4AFileLog();
                        log4a.setDataCode("00");
                        log4a.setFileName(fName);
                        log4a.setFileSize(file.length());
                        log4a.setMakeTm(new java.sql.Date(new Date().getTime()));
                        log4a.setLastModTm(new java.sql.Date(new Date().getTime()));
                        String sql = "insert into IRCNDBA.IO_SUBCHG_4A_FILE_LOG(DATA_CODE,FILE_NAME,FILE_SIZE,MAKE_TM,LAST_MOD_TM) values (?,?,?,?,?)";
                        dataSourceService.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps) throws SQLException {
                                int j = 0;
                                ps.setString(++j, log4a.getDataCode());
                                ps.setString(++j, log4a.getFileName());
                                ps.setLong(++j, log4a.getFileSize());
                                ps.setDate(++j, log4a.getMakeTm());
                                ps.setDate(++j, log4a.getLastModTm());
                            }
                        });
                    } else {
                        String sql = "update IRCNDBA.IO_SUBCHG_4A_FILE_LOG set last_mod_tm = ? , file_size = ? where file_name = ? ";
                        String finalFName = fName;
                        dataSourceService.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps) throws SQLException {
                                int j = 0;
                                ps.setDate(++j, new java.sql.Date(new java.util.Date().getTime()));
                                ps.setLong(++j, file.length());
                                ps.setString(++j, finalFName);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bw != null)
                            bw.close();
                        if (writer != null)
                            writer.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (Throwable t) {
//                log.warn("写入4A文件时出现异常", t);
                t.printStackTrace();
            }


        }

    }
}
