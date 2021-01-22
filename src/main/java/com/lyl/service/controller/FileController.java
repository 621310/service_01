package com.lyl.service.controller;

import com.alibaba.druid.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${remote.uploadFilesUrl}")
    String uploadFilesUrl;

    @RequestMapping(value={"/fileupload"},method= {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> getInfo(@RequestBody MultipartFile file) {

        Map<String, Object> result = new HashMap<String, Object>();

        if (file.isEmpty()) {
            result.put("code", 501);
            result.put("message", "文件名为空");
            return result;
        }
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            // 设置文件存储路径
//            String filePath =new File(this.getClass().getResource(File.separator).getPath())
//                    .getParentFile().getParentFile().getParentFile().getParentFile().getAbsoluteFile() +File.separator+"file"+File.separator;
            String filePath = uploadFilesUrl+"/FileManagerFile/";
            File picFile = new File(filePath);
            if(!picFile.exists()){
                picFile.mkdirs();
            }
            filePath=java.net.URLDecoder.decode(filePath,"UTF-8");
            UUID uuid  =  UUID.randomUUID();
            String path = filePath + uuid+suffixName;
            File dest = new File(path);
            // 检测是否存在目录
            dest.deleteOnExit();
            // 文件写入
            file.transferTo(dest);
            result.put("code", 200);
            result.put("message", "succ");
            result.put("data", path);
            return result;

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e);
            return result;
        }
    }

    //base64图片上传
    @RequestMapping(value={"/Base64FileUpload"},method= {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> Base64FileUpload(@RequestBody String imgStr) {

        Map<String, Object> result = new HashMap<String, Object>();

        imgStr=imgStr.replace("data:image/png;base64,","");
        imgStr=imgStr.replace(" ","+");



        if (imgStr==null || imgStr=="") {
            result.put("code", 501);
            result.put("message", "文件名为空");
            return result;
        }
        try {


            BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(imgStr);

            String filePath =new File(this.getClass().getResource("/").getPath())
                    .getParentFile().getParentFile().getParentFile().getParentFile().getAbsoluteFile() +"/file/";




            filePath=java.net.URLDecoder.decode(filePath,"UTF-8");
            UUID uuid  =  UUID.randomUUID();
            String path = filePath + uuid+".png";



            ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);


            ImageIO.setUseCache(false);

            BufferedImage image = ImageIO.read(bais);


            File w2 = new File(path);// 可以是jpg,png,gif格式

            ImageIO.write(image, "jpg", w2);// 不管输出什么格式图片，此处不需改动


            bais.close();


            result.put("code", 200);
            result.put("message", "succ");
            result.put("data", path);

            return result;

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e);
            return result;
        }
    }

    @RequestMapping(value={"/downloadfile"},method={RequestMethod.POST})
    public Map<String, Object>download(@RequestBody(required = false) String path, HttpServletResponse resp) throws IOException{
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isEmpty(path)) {
            result.put("code", 200);
            result.put("message", "succ");
            result.put("data", "");
        } else {
            File file1 = new File(path);
            if(!file1.exists()){
                result.put("code", 200);
                result.put("message", "succ");
                result.put("data", "");
            } else {
                byte[] b = Files.readAllBytes(Paths.get(path));
                final BASE64Encoder encoder = new BASE64Encoder();
                String base = encoder.encode(b);

                result.put("code", 200);
                result.put("message", "succ");
                result.put("data", base);
            }
        }

        return result;
    }


    @RequestMapping(value = {"/download"}, method = RequestMethod.POST, produces = "application/octet-stream;charset=utf-8")
    public String downloadOne(HttpServletRequest req, HttpServletResponse response) throws IOException {
        String fileName = req.getParameter("fileName");// 设置文件名，根据业务需要替换成要下载的文件名
        String fileUrl = req.getParameter("fileUrl");
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
            }
            System.out.println("----------file download" + fileName);
            try {
                bis.close();
                fis.close();
                os.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "status:succ";
    }

    @RequestMapping(value = "/download2", method = RequestMethod.POST)
    public ResponseEntity<byte[]> download(HttpServletRequest request)throws Exception{
        String fileName = request.getParameter("fileName");// 设置文件名，根据业务需要替换成要下载的文件名
        String filePath = request.getParameter("fileUrl");
//        String fileName = map.get("fileName");
//            String filePath = map.get("fileUrl");
//        String filePath = "C:\\Users\\20996\\Desktop\\Graduationdesign\\a.pdf";
        File file = new File(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment",fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ResponseEntity<byte[]> b = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
        return b;
    }



}


