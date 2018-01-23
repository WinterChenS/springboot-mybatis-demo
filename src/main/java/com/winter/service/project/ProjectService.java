package com.winter.service.project;

import com.winter.model.Project;
import net.sf.mpxj.Task;

import java.io.File;
import java.util.List;

/**
 * Created By Donghua.Chen on  2018/1/9
 */
public interface ProjectService {

    /**
     * 读取Project文件中的数据
     * @param file
     */
    void readMmpFileToDB(File file);

    /**
     * 将Project数据存入数据库
     * @param project
     */
    Integer addProjectInfo(Project project);

    /**
     * 读取Project中的目录级别数据
     * @param task
     * @param project
     * @param list
     * @param levelNum
     */
    void getChildrenTask(Task task, Project project, List<Project> list, int levelNum);

    /**
     * 读取Project最底层的文件
     * @param task
     * @param project
     * @param proList
     * @param levelNum
     */
    void getResourceAssignment(Task task, Project project, List<Project> proList, int levelNum);
    /**
     * 从数据库读取数据写入xml中
     * @Author: Donghua.Chen
     * @Description:
     * @Date: 2018/1/12
     * @param fileLocation
     * @param batchNum
     * @param file
     */
    void writeMppFileToDB(String fileLocation, String batchNum, File file);


}
