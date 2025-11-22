package yeohaenggasijo.tripshot.service;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;
import yeohaenggasijo.tripshot.util.SmsUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OtpService {
    private final Map<String, String> verificationStore = new ConcurrentHashMap<>();

    private final SmsUtil smsUtil;
    private final DefaultMessageService messageService;

    public OtpService(SmsUtil smsUtil, DefaultMessageService messageService) {
        this.smsUtil = smsUtil;
        this.messageService = messageService;
    }
//    @PostConstruct
//    public void init() {
//        Twilio.init(accountSid, authToken);
//    }

    public void sendVerificationCode(String phoneNumber) {
        String authCode = SmsUtil.generateAuthCode(); // 인증번호 생성
        String messageText = smsUtil.makeAuthMessage(authCode); // 메세지 포맷팅

        Message message = new Message();
        message.setFrom("010-2707-9564");
        message.setTo(phoneNumber);
        message.setText(messageText);

        // 요청 래핑
        SingleMessageSendingRequest request = new SingleMessageSendingRequest(message);

        // CoolSMS 발송 및 로그
        try {
            SingleMessageSentResponse response = messageService.sendOne(request);
            log.info("[SMS] 인증번호 발송 - to: {}, code: {}, response: {}", phoneNumber, authCode, response);
        } catch (Exception e) {
            log.error("[SMS] 인증번호 발송 실패 - to: {}, 에러: {}", phoneNumber, e.getMessage(), e);
            throw new RuntimeException("SMS 발송에 실패했습니다.");
        }

        verificationStore.put(phoneNumber, authCode);
        // Redis에 인증번호 저장 (3분 유효)
//        String redisKey = "SMS:AUTH:" + phoneNumber;
//        redisTemplate.opsForValue().set(redisKey, authCode, 3, TimeUnit.MINUTES);
//        log.info("[SMS] 인증번호 Redis 저장 - key: {}, code: {}", redisKey, authCode);
//        if (phoneNumber.equals("+821012345678")) {
//
//        } else {
////            String code = String.valueOf(new Random().nextInt(899999) + 100000);
////            verificationStore.put(phoneNumber, code);
////            Message.creator(new PhoneNumber(phoneNumber), (PhoneNumber) null, "Your verification code is: " + code)
////                    .setMessagingServiceSid(messagingServiceSid)
////                    .create();
//            verificationStore.put(phoneNumber, "222222");
//        }
    }

    public boolean verifyCode(String phoneNumber, String code) {
        return code.equals(verificationStore.get(phoneNumber));
    }
}
