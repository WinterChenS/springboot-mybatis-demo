package com.winter.service.project.impl;

import com.winter.mapper.ProjectMapper;
import com.winter.model.Project;
import com.winter.service.project.ProjectService;
import com.winter.utils.StringUtils;
import net.sf.mpxj.*;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.writer.ProjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created By Donghua.Chen on  2018/1/9
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    public static Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectMapper projectMapper;


    @Override
    public Integer addProjectInfo(Project project) {
        return projectMapper.addProjectSelective(project);
    }

    @Transactional
    @Override
    public void readMmpFileToDB(File file) {
        try{
            MPPReader mppRead = new MPPReader();
            ProjectFile pf = mppRead.read(file);
            System.out.println(file.getName());
            List<Task> tasks = pf.getChildTasks();
            System.out.println("tasks.size() : " + tasks.size());
            List<Project> proList = new LinkedList<>();
            Project pro = new Project();
            pro.setBatchNum(StringUtils.UUID());//生成批次号UUID

            getChildrenTask(tasks.get(0), pro ,proList, 0);
        }catch (MPXJException e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }
    }



    @Override
    public void getChildrenTask(Task task, Project project, List<Project> list, int levelNum){
        if(task.getResourceAssignments().size() == 0){
            levelNum ++;//层级号需要增加
            List<Task> tasks = task.getChildTasks();
            for (int i = 0; i < tasks.size(); i++) {
                if(tasks.get(i).getResourceAssignments().size() == 0){//说明还是在父任务层
                    System.out.println("+++++" + tasks.get(i));
                    Project pro = new Project();
                    if (project.getProjId() != null){//说明不是第一次读取了
                        pro.setParentId(project.getProjId());//将上一级目录的Id赋值给下一级
                    }
                    pro.setBatchNum(project.getBatchNum());
                    pro.setImportTime(new Date());
                    pro.setLevel(levelNum);
                    pro.setTaskName(tasks.get(i).getName());
                    pro.setDurationDate(tasks.get(i).getDuration().toString());
                    pro.setStartDate(tasks.get(i).getStart());
                    pro.setEndDate(tasks.get(i).getFinish());
                    pro.setResource(tasks.get(i).getResourceGroup());
                    this.addProjectInfo(pro);
                    pro.setProjId(pro.getProjId());
                    //getResourceAssignment(tasks.get(i),pro,list,levelNum);
                    getChildrenTask(tasks.get(i), pro,list,levelNum);
                }else{
                    getChildrenTask(tasks.get(i), project, list, levelNum);
                }
            }
        }else{
            if (project.getProjId() != null){

                getResourceAssignment(task, project, list, levelNum);
            }
        }
    }

    public void getResourceAssignment(Task task, Project project, List<Project> proList, int levelNum){
        List<ResourceAssignment> list = task.getResourceAssignments();
        ResourceAssignment rs = list.get(0);
        System.out.println("task = [" + task.getName());
        Project pro = new Project();
        pro.setTaskName(task.getName());
        pro.setParentId(project.getProjId());
        pro.setLevel(levelNum);
        pro.setImportTime(new Date());
        pro.setBatchNum(project.getBatchNum());
        pro.setDurationDate(task.getDuration().toString());
        pro.setStartDate(rs.getStart());
        pro.setEndDate(rs.getFinish());
        String resource = "";
        if(list.size() > 1){
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getResource() != null){
                    if(i < list.size() - 1){
                        resource += list.get(i).getResource().getName() + ",";
                    }else{
                        resource += list.get(i).getResource().getName();
                    }
                }
            }
        }else{

            if(list.size() > 0 && list.get(0).getResource() != null){
                resource = list.get(0).getResource().getName();
            }
        }
        if(!StringUtils.isEmpty(resource)){
            pro.setResource(resource);
        }
        this.addProjectInfo(pro);
        pro.setProjId(pro.getProjId());
        proList.add(pro);

    }


    @Override
    public void writeMppFileToDB(String fileLocation, String batchNum, File file){
        try{
            MPPReader mppRead = new MPPReader();
            ProjectFile pf = mppRead.read(file);
            List<Project> projects =  projectMapper.getProjectsByBatchNum(batchNum);
            writeChildrenTaskToObj(projects,1,pf.addTask(), null);

            //生成文件
            ProjectWriter writer = new MSPDIWriter();

            try{
                writer.write(pf, "d:\\test.xml");
            }catch(IOException ioe){
                throw ioe;
            }
        }catch (MPXJException e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void writeChildrenTaskToObj(List<Project> proList, int levelNum1, Task parentTask, Integer parentId){
        //首先从第一层开始读取
        List<Project> pList = getSubProjectList(proList,parentId,levelNum1);

        for (int i = 0; i < pList.size(); i++) {
            int levelNum = levelNum1;
            Project pro = pList.get(i);
            //然后利用parentId进行进一步的读取,并且这个可以进行判断是否是最底层
            List<Project> childrenList = getSubProjectList(proList,pro.getProjId(),levelNum + 1);
            //这个判断很重要，如果size为0，说明当前的层级是最底层
            if(childrenList.size() > 0){
                //说明是父任务，进行父任务的写入，然后进行下一次递归
                Task task = parentTask.addTask();
                task.setName(pro.getTaskName());
                task.setDuration(Duration.getInstance(5, TimeUnit.DAYS));
                task.setStart(pro.getStartDate());
                task.setFinish(pro.getEndDate());
                if (levelNum ==  1){//如果是读取第一层
                    task.setOutlineLevel(1);
                    task.setUniqueID(1);
                    task.setID(1);
                }else{
                    task.setOutlineLevel(parentTask.getOutlineLevel() + 1);
                    task.setUniqueID(parentTask.getUniqueID() + 1);
                    task.setID(parentTask.getID() + 1);
                }
                levelNum ++;
                //进行递归写入
                writeChildrenTaskToObj(proList, levelNum,task, pro.getProjId());
            }else{//说明当前层级为最底层
                writeResourceAssignmentToObj(pro, pro.getParentId(), parentTask);
            }

        }

    }

    public void writeResourceAssignmentToObj(Project pro, int parentId, Task parentTask){

        Task task = parentTask.addTask();
        task.setName(pro.getTaskName());
        task.setDuration(Duration.getInstance(5, TimeUnit.DAYS));
        task.setStart(pro.getStartDate());
        task.setFinish(pro.getEndDate());
        task.setResourceGroup(pro.getResource());
        task.setOutlineLevel(parentTask.getOutlineLevel() + 1);
        task.setUniqueID(parentTask.getUniqueID() + 1);
        task.setID(parentTask.getID() + 1);
    }

    /**
     * 获取子任务列表
     * @Author: Donghua.Chen
     * @Description:
     * @Date: 2018/1/12
     * @param proList
     * @param parentId
     */
    private List<Project> getSubProjectList(List<Project> proList, Integer parentId, Integer levelNum){

        List<Project> subList = new LinkedList<>();
        List<Project> rsList = new LinkedList<>();
        if(levelNum != null){
            for (int i = 0; i < proList.size(); i++) {
                Project pro = proList.get(i);

                    if(pro.getLevel().equals(levelNum)){
                        subList.add(pro);
                    }
            }

        }else{
            subList = proList;
        }

        if(parentId != null){
            for (int i = 0; i < subList.size(); i++) {
                Project pro = subList.get(i);
                if(pro.getParentId().equals(parentId)){
                    rsList.add(pro);
                }
            }

        }
        if (parentId != null){
            return rsList;
        }else {
            return subList;
        }

    }

}
