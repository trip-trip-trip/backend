package yeohaenggasijo.tripshot.util;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {
    public static String generateAuthCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자
        return String.valueOf(code);
    }

    public String makeAuthMessage(String authCode) {
        return "[Tripshot 인증번호] " + authCode + "\n본인 확인을 위해 인증번호를 입력해주세요.";
    }
}