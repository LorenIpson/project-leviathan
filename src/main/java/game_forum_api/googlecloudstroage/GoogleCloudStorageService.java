package game_forum_api.googlecloudstroage;

import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GoogleCloudStorageService {
    private final String bucketName = "leviathan_images";
    private final Storage storage;

    public GoogleCloudStorageService(Storage storage) {
        this.storage = storage;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
    
    public boolean deleteFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName); // fileName 要傳入 "資料夾/檔名.副檔名"
        return storage.delete(blobId);
    }
    
    public List<String> listAllImageUrls() {
        List<String> imageUrls = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);

        for (Blob blob : bucket.list().iterateAll()) {
            if (blob.getContentType() != null && blob.getContentType().startsWith("image/")) {
                String url = String.format("https://storage.googleapis.com/%s/%s", bucketName, blob.getName());
                imageUrls.add(url);
            }
        }
        return imageUrls;
    }
    
    public void deleteImageByUrl(String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        storage.delete(BlobId.of(bucketName, fileName));
    }
}

