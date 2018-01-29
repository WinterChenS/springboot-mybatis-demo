package com.winter.Controller;

import com.winter.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * Created By Donghua.Chen on  2018/1/9
 */
@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @ResponseBody
    @RequestMapping(value = "/add/project-file", method = RequestMethod.POST)
    public ResponseEntity addProjectFile(){

        File file = new File("C:\\Users\\donghua.chen\\Desktop\\项目计划导入模板-2018011201 - 副本.mpp");
        projectService.readMmpFileToDB(file);
        return ResponseEntity.ok("导入成功!!");
    }

    @ResponseBody
    @RequestMapping(value = "/project-file/{batchNum}", method = RequestMethod.GET)
    public ResponseEntity writeProjectFile(@PathVariable("batchNum") String batchNum){
        File file = new File("C:\\Users\\donghua.chen\\Desktop\\开办新公司 - 导出模板.mpp");
        projectService.writeMppFileToDB("C:\\Users\\donghua.chen\\Desktop",batchNum,file);
        return ResponseEntity.ok("导出成功");
    }
}
