package game_forum_api.googlecloudstroage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import game_forum_api.googlecloudstroage.GoogleCloudStorageService;
import game_forum_api.googlecloudstroage.dto.DeleteImageRequest;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

	@Autowired
    private final GoogleCloudStorageService storageService;

    public FileUploadController(GoogleCloudStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = storageService.uploadFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("上傳失敗");
        }
    }
    
    // 刪除圖片
    @DeleteMapping
    public ResponseEntity<String> deleteImage(@RequestBody DeleteImageRequest request) {
        String url = request.getUrl();
        if (url == null || url.isEmpty()) {
            return ResponseEntity.badRequest().body("缺少圖片網址");
        }

        try {
            storageService.deleteImageByUrl(url); // 從 URL 取得檔名並刪除
            return ResponseEntity.ok("刪除成功");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("刪除失敗: " + e.getMessage());
        }
    }
    
    @GetMapping("/images")
    public ResponseEntity<List<String>> listImages() {
        try {
            List<String> urls = storageService.listAllImageUrls();
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
}

