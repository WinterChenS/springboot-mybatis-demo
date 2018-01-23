package com.winter.model;

import java.util.Date;

/**
 * Project文件封装类
 * Created By Donghua.Chen on  2018/1/9
 */
public class Project {
    /* 自增主键Id */
    private Integer projId;
    /* 上级Id */
    private Integer parentId;
    /* 结构层级 */
    private Integer level;
    /* 任务名称 */
    private String taskName;
    /* 工期 */
    private String durationDate;
    /* 开始时间 */
    private Date startDate;
    /* 结束时间 */
    private Date endDate;
    /* 前置任务ID */
    private Integer preTask;
    /* 资源名称 */
    private String resource;
    /* 导入时间 */
    private Date importTime;
    /* 批次号 */
    private String batchNum;

    public Integer getProjId() {
        return projId;
    }

    public void setProjId(Integer projId) {
        this.projId = projId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDurationDate() {
        return durationDate;
    }

    public void setDurationDate(String durationDate) {
        this.durationDate = durationDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getPreTask() {
        return preTask;
    }

    public void setPreTask(Integer preTask) {
        this.preTask = preTask;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Date getImportTime() {
        return importTime;
    }

    public void setImportTime(Date importTime) {
        this.importTime = importTime;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projId=" + projId +
                ", parentId=" + parentId +
                ", level=" + level +
                ", taskName='" + taskName + '\'' +
                ", durationDate='" + durationDate + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", preTask=" + preTask +
                ", resource='" + resource + '\'' +
                ", importTime=" + importTime +
                ", batchNum='" + batchNum + '\'' +
                '}';
    }
}
