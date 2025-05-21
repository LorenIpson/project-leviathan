package game_forum_api.avatar.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/avatar/upload")
public class UploadController {

    @Value("${frontend.project.path}")
    private String frontendProjectPath;

    @PostMapping
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {
        try {
            // 構建目標目錄路徑
            Path uploadPath = Paths.get(frontendProjectPath, "src", "assets", "img", "avatar", type);

            // 如果目錄不存在，則創建
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 檢查是否已存在相同檔名的檔案
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            if (Files.exists(filePath)) {
                return ResponseEntity.badRequest().body("已有相同檔名的檔案，請確認檔案");
            }

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 打印文件保存路徑
            System.out.println("文件保存路徑: " + filePath.toAbsolutePath());

            return ResponseEntity.ok("文件上傳成功: " + filePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("文件上傳失敗");
        }
    }
}