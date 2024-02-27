package com.activiti.z_six.service.impl;

import com.activiti.z_six.service.IFilService;
import com.activiti.z_six.util.DateUtils;
import com.activiti.z_six.util.SFTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class IFileServiceImpl implements IFilService {


    @Value("${sftp.sftpUrl}")
    private String sftpUrl;
    @Value("${sftp.sftpPort}")
    private int port;

    @Value("${sftp.sftpUsername}")
    private String userName;

    @Value("${sftp.sftpPassword}")
    private String password;

    @Value("${sftp.sftpBasePath}")
    private String sftpBasePath;

    @Value("${sftp.sftpImagePath}")
    private String sftpImagePath;

    @Value("${sftp.sftpFilePath}")
    private String sftpFilePath;

    @Override
    public Map<String, Object> uploadFile(String fileNameOld, InputStream ins, boolean nameTag) throws Exception {
        SFTPUtil sftp = null;
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // 获取ftp配置
            String[] strings = fileNameOld.split("\\.");
            String suffix = strings[strings.length - 1];
            String filePath;

            String[] images = {"jpg", "jpeg", "png", "gif"};

            if (ArrayUtils.contains(images, suffix.toLowerCase())) {
                // 图片路径

                filePath = sftpImagePath;
            } else {
                // 文件路径
                filePath = sftpFilePath;
            }
            // 年月日路径
            String fileNamePath = filePath + "/" + DateUtils.getDate("yyyy") + "/" + DateUtils.getDate("MM") + "/"
                    + DateUtils.getDate("dd") + "/";
            // 生成新的文件名
            String fileName = "";
            if (nameTag) {
                fileName += fileNameOld.replace("." + suffix, "") + "_";
            }
            fileName += System.currentTimeMillis() + "." + suffix;
//           // sftp数据
            sftp = new SFTPUtil(userName, password, sftpUrl, port);
            // 连接sftp服务器
            sftp.login();
            //上传
            sftp.upload(sftpBasePath, fileNamePath, fileName, ins);
            // 关闭连接
            sftp.logout();
            /**
             * 封装输出结果
             */
            result.put("suffix", suffix);
            result.put("filePath", "/"+fileNamePath + fileName);
            result.put("fileName", fileNameOld);
        } catch (Exception e) {
            throw new Exception("上传附件异常!");
        } finally {
            sftp.logout();
        }
        return result;
    }

    @Override
    public void download(String path, String fileName, HttpServletResponse response) {

        SFTPUtil sftp = null;
        ServletOutputStream out = null;

        try {
            // sftp数据
            sftp = new SFTPUtil(userName, password, sftpUrl, port);
            // 连接sftp服务器
            sftp.login();
            fileName=path.split("/")[path.split("/").length-1];
            //上传
            byte[] bytes = sftp.download(path.replace(fileName,""), fileName);
            // 设置response的Header
            response.setContentType("application/octet-stream");
            response.setHeader("content-type", "application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            sftp.logout();
        }
    }
}
