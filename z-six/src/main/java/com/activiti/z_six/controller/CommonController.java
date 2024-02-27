package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.service.IFilService;
import com.activiti.z_six.util.ResultRes;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Map;

/**
 * 通用请求处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/common")
public class CommonController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);


    private static final String FILE_DELIMETER = ",";

    @Autowired
    private IFilService iFilService;


    /**
     * 文件上传SFTP
     *
     * @return
     * @throws Exception
     * @throws Exception
     */
    @OperLog(operModul = "文件相关" , operType = LogConst.OTHER , operDesc = "附件上传")
    @PostMapping("/uploadSftp")
    public ResultRes uploadFileSftp(@RequestParam("file") MultipartFile file) throws Exception {
        // 获取源文件
        boolean tag = true;
        // 获取文件流/
        InputStream ins = file.getInputStream();
        // 获取文件名称 (包括后缀)
        String fileNameOld = file.getOriginalFilename();
        // 调用上传
        Map<String, Object> result = iFilService.uploadFile(fileNameOld, ins, tag);
       return  ResultRes.success(result);
    }
    @OperLog(operModul = "文件相关" , operType = LogConst.OTHER , operDesc = "附件下载")
    @PostMapping("/downFilesftp")
    public void downFilesftp(@RequestBody JSONObject param, HttpServletResponse response){
        String fileName=param.getString("fileName");
        String filePath=param.getString("filePath");
        iFilService.download(filePath,fileName,response);
    }
}
