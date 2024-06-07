package personal.MapleChenX.lsp.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import personal.MapleChenX.lsp.common.model.ret.ResponseTemplate;
import personal.MapleChenX.lsp.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/v1/file")
public class FileController {
    @Resource
    FileService fileService;

    @Value("${const.default.fileUploadPath.video}")
    String videoPath;

    @RequestMapping("/upload")
    public ResponseTemplate<?> upload(@RequestParam("file") MultipartFile file) {
//        String sid = id.replaceAll("\"", ""); todo 过滤器加上之后放开，并且要从HttpRequest中获取id属性，然后把下一行注释掉
        String sid = "646f4eef25ac4a38a1db03bf8b196639";
        String avatarPath = fileService.avatarUpload(file, sid);
        System.out.println("file size:"+file.getSize()/1024.0/1024 + "MB");
        return ResponseTemplate.success(avatarPath);
    }

    /**
     * 关键点，就是把从minio获取的流写入到response的输出流中
     */
    @RequestMapping("/avatarGet/**")
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "image/jpg");
        fileService.doAvatarGet(request, response);
        // 设置图片缓存，约30天
        response.setHeader("Cache-Control", "max-age=2592000");
    }

    /**
     * 大文件分片上传
     * @param file 上传的文件
     * @param fileName 文件名
     * @param chunkIndex 当前分片编号
     * @param totalChunks 总分片数
     * @param md5 文件标识
     */
    @RequestMapping("/uploadChunk")
    public ResponseEntity<?> uploadChunk(@RequestParam("file") MultipartFile file,
                                         @RequestParam("filename") String fileName,
                                         @RequestParam("chunkIndex") int chunkIndex,
                                         @RequestParam("totalChunks") int totalChunks,
                                         @RequestParam("md5") String md5) {

        try {
            // todo 秒传
            fileService.uploadChunk(file, chunkIndex, totalChunks, md5, fileName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 视频观看，就是把ts文件流拷贝到response的输出流
     * @param fileId 文件id，其实是文件md5值
     */
    @RequestMapping("/video/m3u8")
    public ResponseEntity<?> videoWatchReq(@RequestParam("fileId") String fileId, HttpServletResponse response) throws IOException {
        System.out.println("videoWatch fileId：" + fileId);
        Path m3u8Path = fileService.videom3u8(fileId, response);
        if (Files.exists(m3u8Path)){
            System.out.println("m3u8Path存在 路径: " + m3u8Path);
            InputStreamResource m3u8 = new InputStreamResource(Files.newInputStream(m3u8Path));
            // 返回m3u8文件
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/x-mpegURL"))
                    .body(m3u8);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/video/{filename}")
    public ResponseEntity<InputStreamResource> watch(@PathVariable String filename) throws IOException {
        Path path = fileService.watch(filename); // ts文件路径
        if (Files.exists(path)) {
            InputStreamResource inputStreamResource = new InputStreamResource(Files.newInputStream(path));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp2t"))
                    .body(inputStreamResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping("/downloadTest")
    public void download(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        //获取文件路径
        String filePath = "X:\\minio.bat";
        //获取文件名
        String fileName = "minio.bat";

        //告诉浏览器返回的内容是一个需要下载的文件
        response.setContentType("application/x-msdownload; charset=UTF-8");
        //如果是IE浏览器，则对文件名进行编码
        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0) {//IE浏览器
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            //对文件名进行编码
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        }
        //设置响应头；指示浏览器将响应的内容作为文件下载
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");

        // 从filePath获取file流
        Path path = Paths.get(filePath);

        try (InputStream fileStream = Files.newInputStream(path)) {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copy(fileStream, outputStream);
        } catch (IOException e) {
            // 处理异常
            System.out.println("下载文件失败");
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFolder() throws IOException {
        // 文件夹路径
        Path folderPath = Paths.get("X:\\Tomcat9\\apache-tomcat-9.0.80");
        // 创建临时文件存储压缩文件
        Path zipPath = Files.createTempFile("folder", ".zip");
        // 压缩文件夹
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(folderPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(folderPath.relativize(path).toString());
                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
        // 创建响应
        FileSystemResource resource = new FileSystemResource(zipPath.toFile());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipPath.getFileName())
                .body(resource);
    }

}