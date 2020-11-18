package com.lyl.service.controller;

import com.lyl.service.entity.User;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class testController {

    private static final Logger logger = LoggerFactory.getLogger(testController.class);
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @RequestMapping(value = "/testredis")
    public Map<String,Object> test(){
        Map<String,Object> result = new HashMap<>();
        result.put("code",200);
        result.put("msg","success");
        String key = "zszxz";
        String value = "知识追寻者";
        redisTemplate.opsForValue().set(key, value);
        return result;
    }

    //测试redis
    @RequestMapping(value = "/app/testredis")
    public Map<String,Object> testredis(){
        String aa = (String)redisTemplate.opsForValue().get("zszxz");
        Map<String,Object> result = new HashMap<>();
        result.put("aa",aa);
        return result;
    }

    @RequestMapping("/test")
    private Map<String,Object> testredis2(){
        Map<String,Object> result = new HashMap<>();
        User user = new User("1","吃橘子的张三丰",null);
        result.put("hellow","lyl");
        logger.info(user.getName());
        result.put("user",user);
        return result;
    }

    @RequestMapping(value = "/download2", method = RequestMethod.POST)
    public ResponseEntity<byte[]> download(HttpServletRequest request, @RequestBody Map<String,String> map)throws Exception{
        String fileName = map.get("fileName");
//            String filePath = map.get("fileUrl");
        String filePath = "C:\\Users\\20996\\Desktop\\Graduationdesign\\a.pdf";
//            String filePath = "C:\\Users\\20996\\Desktop\\20200701\\a.docx";
        File file = new File(filePath);
        HttpHeaders headers = new HttpHeaders();
//            String dowmloadFileName = new String(fileName.get)
        headers.setContentDispositionFormData("attachment",fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ResponseEntity<byte[]> b = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
        return b;
    }

    @RequestMapping(value = {"/download"}, method = RequestMethod.POST, produces = "application/octet-stream;charset=utf-8")
    public String download(HttpServletRequest req, HttpServletResponse response) {
//        String fileName = req.getParameter("fileName");// 设置文件名，根据业务需要替换成要下载的文件名
//        String fileUrl = req.getParameter("fileUrl");
        String fileName = "文件";// 设置文件名，根据业务需要替换成要下载的文件名
        String fileUrl = "C:\\Users\\20996\\Desktop\\Graduationdesign\\a.pdf";
        File file = new File(fileUrl);//下载目录加文件名拼接成realpath
        if(file.exists()){ //判断文件父目录是否存在
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            int rl = 1024*1024*1024;
            byte[] buffer = new byte[rl];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer,0,i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally {
                try {
                    bis.close();
                    fis.close();
                    os.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            System.out.println("----------file download" + fileName);

        }
        return "status:succ";
    }

}
