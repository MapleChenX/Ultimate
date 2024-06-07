package personal.MapleChenX.lsp.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;


public interface FileService {
    public String avatarUpload(MultipartFile file, String uid);
    public void doAvatarGet(HttpServletRequest request, HttpServletResponse response);
    public void getFileFromMinio(String filePath, OutputStream outputStream);
    public void uploadChunk(MultipartFile file, int chunkNumber, int totalChunks, String md5, String filename) throws IOException;
    public Path videom3u8(String md5, HttpServletResponse response);
    public Path watch(String filename);

}
