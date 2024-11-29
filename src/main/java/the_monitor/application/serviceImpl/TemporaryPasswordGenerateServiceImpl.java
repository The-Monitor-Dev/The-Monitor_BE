package the_monitor.application.serviceImpl;

import org.springframework.stereotype.Service;
import the_monitor.application.service.TemporaryPasswordGenerateService;

import java.security.SecureRandom;

@Service
public class TemporaryPasswordGenerateServiceImpl implements TemporaryPasswordGenerateService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%";
    private static final int PASSWORD_LENGTH = 12;

    public String generateTemporaryPassword() {

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 특수문자 하나 무작위 선택
        int specialCharIndex = random.nextInt(SPECIAL_CHARACTERS.length());
        char specialChar = SPECIAL_CHARACTERS.charAt(specialCharIndex);

        // 나머지 문자들을 먼저 채워줌
        for (int i = 0; i < PASSWORD_LENGTH - 1; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index)); // 일반 문자들을 먼저 추가
        }

        // 특수문자 삽입 위치 무작위 선택
        int randomPosition = random.nextInt(PASSWORD_LENGTH);
        password.insert(randomPosition, specialChar); // 특수문자 삽입

        return password.toString();

    }

}