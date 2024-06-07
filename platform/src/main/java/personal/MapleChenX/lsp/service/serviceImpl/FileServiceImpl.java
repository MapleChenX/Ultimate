package personal.MapleChenX.lsp.service.serviceImpl;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import personal.MapleChenX.lsp.service.FileService;
import personal.MapleChenX.lsp.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Resource
    UserService userService;

    @Resource
    MinioClient minioClient;

    @Value("${spring.minio.bucket}")
    String bucket;

    @Value("${spring.minio.bucket4file}")
    String bucket4file;

    @Value("${const.default.fileUploadPath.temp}")
    String tempPath;

    @Value("${const.default.fileUploadPath.fullFile}")
    String fullFilePath;

    @Value("${const.default.fileUploadPath.video}")
    String videoPath;

    @Override
    public String avatarUpload(MultipartFile file, String uid) {
        // 以两个空格作为uid和文件名的分隔符
        String fileName = "/avatar/"+ uid + "/" + file.getOriginalFilename();
        System.out.println(fileName);
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucket)

                    .stream(file.getInputStream(), file.getSize(), -1)
                    .object(fileName)
                    .build();
            minioClient.putObject(args);
        }catch (Exception e){
            throw new RuntimeException("图片上传失败");
        }
        System.out.println("minio upload ok");
        userService.updateAvatar(fileName, uid);
        return fileName;
    }

    public void doAvatarGet(HttpServletRequest request, HttpServletResponse response) {
        String imagePath = request.getServletPath().substring(18);
        try {
            ServletOutputStream stream = response.getOutputStream();
            System.out.println("开始获取图片");
            getFileFromMinio(imagePath, stream);
        }catch (Exception e){
            throw new RuntimeException("图片获取失败");
        }
    }

    public void getFileFromMinio(String filePath, OutputStream outputStream){
        GetObjectArgs get = GetObjectArgs.builder()
                .bucket(bucket)
                .object(filePath)
                .build();
        // 返回数据是流，直接写入到输出流中；返回图片的方式都是给输出流赋值
        try {
            GetObjectResponse fileStream = minioClient.getObject(get);
            IOUtils.copy(fileStream, outputStream);
        }catch (Exception e){
            throw new RuntimeException("从minio获取图片失败");
        }
    }

    @Override
    public void uploadChunk(MultipartFile file, int chunkNumber, int totalChunks, String md5, String filename) throws IOException {
        Path chunkDir = Paths.get(tempPath, md5);
        if(!Files.exists(chunkDir))
            Files.createDirectories(chunkDir);
        Path chunkPath = chunkDir.resolve(String.valueOf(chunkNumber));
        // 写入文件片段，将文件块的内容写入由chunkPath指定的路径。如果指定路径已经存在文件，则会被覆盖。
        Files.write(chunkPath, file.getBytes());
        // 如果所有的文件片段都已经上传完毕，那么合并文件片段
        if (Files.list(chunkDir).count() == totalChunks) {
            mergeChunks(chunkDir, totalChunks, md5, filename);
        }
    }

    @Override
    public Path videom3u8(String md5, HttpServletResponse response) {
        Path path = Paths.get(videoPath, md5);
        System.out.println("path: " + path);
        // 找到文件夹下的m3u8文件
        try {
            List<Path> m3u8Path = Files.walk(path)
                    .filter(f -> f.toString().endsWith(".m3u8"))
                    .collect(Collectors.toList());
            System.out.println("找到了");
            return m3u8Path.get(0);
        }catch (Exception e){
            throw new RuntimeException("视频文件不存在");
        }
    }

    @Override
    public Path watch(String filename) {
//        String filename = "Assassin's Creed® Unity 2023-08-10 12-55-29_ea5bcb63cd0687b6dd0da9ce6702f863_(000)";
        int lastUnderscore = filename.lastIndexOf("_");
        int secondLastUnderscore = filename.lastIndexOf("_", lastUnderscore - 1);
        String tsFolder = filename.substring(secondLastUnderscore + 1, lastUnderscore);
        System.out.println(tsFolder); // 输出：ea5bcb63cd0687b6dd0da9ce6702f863
        Path path = Paths.get(videoPath, tsFolder, filename);
        return path;
    }

    private void mergeChunks(Path chunkDir, int totalChunks, String md5, String filename) throws IOException {
        Path fileDir = Paths.get(fullFilePath);
        if (!Files.exists(fileDir))
            Files.createDirectories(fileDir);
        Path fillFilePath = fileDir.resolve(filename); // 合并后的文件路径

        try (OutputStream out = Files.newOutputStream(fillFilePath)) {
            for (int i = 0; i < totalChunks; i++) {
                System.out.println("合并第" + i + "个文件片段");
                Path chunkPath = chunkDir.resolve(String.valueOf(i));
                Files.copy(chunkPath, out);
                Files.delete(chunkPath);      // todo 有待优化，不应该每次都删除，还要留着防止重传
            }
            System.out.println("------本地合并完成------");
            System.out.println("---上传到minio中---");
            String minioPath = "/file/" + md5 + "/" + filename; // todo 路径后续需要和用户相关联
            System.out.println(minioPath);

            file2minio(fillFilePath, minioPath);
            System.out.println("------minio上传完成------");
            System.out.println("full path is : " + fillFilePath);
            // 判断是不是视频文件
            String mimeType = Files.probeContentType(fillFilePath);
            if(mimeType != null && mimeType.startsWith("video")){
                System.out.println("------视频转换中------");
                // todo 扔进异步池
                video2m3u8(fullFilePath + "/" + filename, md5);
                System.out.println("------视频转换完成------");
            }
        } catch (Exception e) {
            System.out.println("合并文件失败");
            throw new RuntimeException("合并文件失败");
        }
        Files.delete(chunkDir);
    }

    private void file2minio(Path fillFilePath, String minioPath) throws Exception{
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucket4file)
                .stream(Files.newInputStream(fillFilePath), Files.size(fillFilePath), -1)
                .object(minioPath)
                .build();
        System.out.println("------minio上传中------");
        minioClient.putObject(args);
    }

    private void video2m3u8(String videoFilePath, String md5){
        // 判断路径是否存在，不存在则创建
        String folderPath = videoPath + "/" + md5 + "/"; // 转码文件夹，文件夹名是md5值，一个文件夹对应一个视频
        Path path = Paths.get(folderPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String outputFilePath = folderPath + new File(videoFilePath).getName().substring(0, new File(videoFilePath).getName().lastIndexOf(".")) + ".m3u8";
        System.out.println("Video file path: " + videoFilePath);
        System.out.println("outputFilePath: " + outputFilePath);

        // 一条ffmpeg命令直接生成M3U8目录文件和ts视频文件
        String cmd = "ffmpeg -i \"" + videoFilePath + "\" -codec copy -map 0 -f segment -segment_list \"" + outputFilePath + "\" -segment_time 10 \"" + outputFilePath.substring(0, outputFilePath.lastIndexOf(".")) + "_" + md5 + "_" + "(%03d).ts\"";
        try {
            // 执行命令并打印细节
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            int exitCode = process.waitFor();
            System.out.println("Exit code: " + exitCode);
            System.out.println("------命令成功------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
