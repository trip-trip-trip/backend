
package yeohaenggasijo.tripshot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "webpush.vapid")
public class VapidKeys {
    private String publicKey;
    private String privateKey;
    private String subject;

    // Getter와 Setter는 필수! (생략)
    public String getPublicKey() {
        return publicKey != null ? publicKey.trim() : null;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    public String getPrivateKey() {
        return privateKey != null ? privateKey.trim() : null;
    }
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

