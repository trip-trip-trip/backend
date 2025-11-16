package yeohaenggasijo.tripshot.service.storage;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageUploader {

    record UploadResult(String key, String url, String etag, long size) {}

    UploadResult upload(Path file, String contentType, String objectKey);
}