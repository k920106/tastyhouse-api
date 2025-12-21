package com.tastyhouse.webapi;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyGeneratorTest {

    @Test
    void generate_hs512_secret_key() {
        // HS512 알고리즘에 안전한 SecretKey를 생성합니다.
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // 생성된 키를 Base64 문자열로 인코딩합니다.
        String base64Key = Encoders.BASE64.encode(key.getEncoded());

        // 생성된 키를 콘솔에 출력합니다.
        System.out.println("Generated HS512 Key: " + base64Key);

        // (검증) 인코딩된 키를 다시 디코딩했을 때 64바이트(512비트)인지 확인합니다.
        byte[] decodedKey = Decoders.BASE64.decode(base64Key);
        assertThat(decodedKey.length).isEqualTo(64);
    }
}
