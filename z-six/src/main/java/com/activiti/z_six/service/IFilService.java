package com.activiti.z_six.service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by zzz on 2022/8/18.
 */
public interface IFilService {

    /**
     * 文件上传
     *
     * @param fileNameOld   文件名
     * @param ins   文件流
     * @param nameTag 是否保留文件名
     * @return
     * @throws Exception
     */
    Map<String, Object> uploadFile(String fileNameOld, InputStream ins, boolean nameTag) throws Exception;


    void download (String path , String fileName , HttpServletResponse response);
}
