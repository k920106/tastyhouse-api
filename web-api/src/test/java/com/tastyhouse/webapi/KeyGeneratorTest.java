package com.tastyhouse.webapi;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyGeneratorTest {

    @Test
    void generate_hs512_secret_key() {
        // HS512 = 512bit = 64bytes
        byte[] keyBytes = new byte[64];
        new SecureRandom().nextBytes(keyBytes);

        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        String base64Key = Encoders.BASE64.encode(key.getEncoded());

        byte[] decodedKey = Decoders.BASE64.decode(base64Key);
        assertThat(decodedKey.length).isEqualTo(64);
    }
}
