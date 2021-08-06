package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/upload")
@CrossOrigin
public class FileUploadController {

    /**
     * 文件上传
     */
    @PostMapping
    public Result upload(@RequestParam(value = "file")MultipartFile file) throws Exception{
        FastDFSFile fastDFSFile = new FastDFSFile(
                file.getOriginalFilename(),//文件名1.jpg
                file.getBytes(),//文件字节数组
                StringUtils.getFilenameExtension(file.getOriginalFilename()));//文件扩展名

        //调用FastDFSUtil工具类将文件传入到FastDFS中
        String[] uploads = FastDFSUtil.upload(fastDFSFile);
        //拼接访问地址 url = http://192.168.21.132:8080/group1/M00/01/44/itheima.jpg
        //String url = "http://192.168.21.132:8080/" + uploads[0] + "/" + uploads[1];
        String url = FastDFSUtil.getTrackerInfo() + "/" + uploads[0] + "/" + uploads[1];

        return new Result(true, StatusCode.OK,"上传成功！",url);
    }
}
