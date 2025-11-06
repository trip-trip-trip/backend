package yeohaenggasijo.tripshot.helper.converter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import yeohaenggasijo.tripshot.helper.constants.SocialLoginType;

@Configuration
public class SocialLoginTypeConverter implements Converter<String, SocialLoginType> {
    @Override
    public SocialLoginType convert(String s) {
        return SocialLoginType.valueOf(s.toUpperCase());
        // 소문자와 SocialLoginType enum을 매핑하기 위함. 구분 없이 사용 가능
    }
}
