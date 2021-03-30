package com.jhhc.bean;

import java.sql.Date;

/**
 * @author xiaojiang
 * @date 2021/3/1 16:54
 */
public class IoSubchg4AFileLog{
    private String dataCode;
    private String fileName;
    private Long fileSize;
    private Date makeTm;
    private Date lastModTm;

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Date getMakeTm() {
        return makeTm;
    }

    public void setMakeTm(Date makeTm) {
        this.makeTm = makeTm;
    }

    public Date getLastModTm() {
        return lastModTm;
    }

    public void setLastModTm(Date lastModTm) {
        this.lastModTm = lastModTm;
    }

    @Override
    public String toString() {
        return "IoSubchg4AFileLog{" +
                "dataCode='" + dataCode + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", makeTm=" + makeTm +
                ", lastModTm=" + lastModTm +
                '}';
    }
}