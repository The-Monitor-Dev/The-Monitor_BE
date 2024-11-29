package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import the_monitor.application.service.CertifiedKeyService;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertifiedKeyServiceImpl implements CertifiedKeyService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String generateCertifiedKey() {

        Random random = new SecureRandom();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }

        return key.toString();

    }

    @Override
    public boolean existsCertifiedKey(String email) {

        return Boolean.TRUE.equals(redisTemplate.hasKey(email));

    }

    @Override
    public void saveCertifiedKey(String email, String key) {

        redisTemplate.opsForValue().set(email, key, 10, TimeUnit.MINUTES);

    }

    @Override
    public String getCertifiedKey(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }

    @Override
    public void deleteCertifiedKey(String email) {
        redisTemplate.delete(email);
    }

    @Override
    public boolean isCertifiedKeyExpired(String email) {
        Long ttl = redisTemplate.getExpire(email, TimeUnit.SECONDS);

        if (ttl == null || ttl == -2) {
            return true;
        }

        return ttl < 420;

    }

}
