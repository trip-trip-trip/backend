package yeohaenggasijo.tripshot.service.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class R2StorageUploader implements StorageUploader {

    private final R2StorageService r2;

    @Override
    public UploadResult upload(Path file, String contentType, String objectKey) {
        return r2.upload(file, contentType, objectKey);
    }
}