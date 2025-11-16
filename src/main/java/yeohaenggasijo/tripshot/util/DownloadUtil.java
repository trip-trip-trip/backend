package yeohaenggasijo.tripshot.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DownloadUtil {
    private final HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    public Path downloadToTemp(String url, Path workDir) throws IOException, InterruptedException {
        Path out = Files.createTempFile(workDir, "src_", ".bin");
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<Path> res = http.send(req, HttpResponse.BodyHandlers.ofFile(out));
        if (res.statusCode() / 100 != 2) throw new IOException("Download failed: " + res.statusCode());
        return out;
    }
}
