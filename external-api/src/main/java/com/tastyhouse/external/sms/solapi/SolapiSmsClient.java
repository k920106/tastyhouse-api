package com.tastyhouse.external.sms.solapi;

import com.tastyhouse.external.sms.solapi.dto.SolapiMessageRequest;
import com.tastyhouse.external.sms.solapi.dto.SolapiMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolapiSmsClient {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String AUTH_SCHEME = "HMAC-SHA256";

    private final WebClient.Builder webClientBuilder;
    private final SolapiProperties solapiProperties;

    public SolapiMessageResponse sendSms(String to, String text) {
        SolapiMessageRequest request = SolapiMessageRequest.builder()
            .messages(List.of(
                SolapiMessageRequest.SolapiMessage.builder()
                    .to(to)
                    .from(solapiProperties.getSenderNumber())
                    .text(text)
                    .type("SMS")
                    .build()
            ))
            .build();

        log.info("Solapi SMS 발송 요청. to: {}", to);

        try {
            String authorizationHeader = createAuthorizationHeader();

            SolapiMessageResponse response = webClientBuilder.build()
                .post()
                .uri(solapiProperties.getBaseUrl() + solapiProperties.getSendManyPath())
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SolapiMessageResponse.class)
                .block();

            if (response == null) {
                log.warn("Solapi SMS 발송 응답 없음. to: {}", to);
                throw new RuntimeException("SMS 발송 응답이 없습니다.");
            }

            if (!response.isSuccess()) {
                log.error("Solapi SMS 발송 실패. to: {}, failedMessages: {}", to, response.getFailedMessageList());
                throw new RuntimeException("SMS 발송에 실패했습니다.");
            }

            log.info("Solapi SMS 발송 성공. to: {}", to);
            return response;

        } catch (WebClientResponseException e) {
            log.error("Solapi SMS 발송 API 오류. status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("SMS 발송 중 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Solapi SMS 발송 중 예외 발생. to: {}", to, e);
            throw new RuntimeException("SMS 발송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String createAuthorizationHeader() throws Exception {
        String dateTime = Instant.now().toString();
        String salt = UUID.randomUUID().toString().replace("-", "");
        String signature = generateHmacSignature(solapiProperties.getApiSecret(), dateTime, salt);

        return "%s apiKey=%s, date=%s, salt=%s, signature=%s".formatted(
            AUTH_SCHEME,
            solapiProperties.getApiKey(),
            dateTime,
            salt,
            signature
        );
    }

    private String generateHmacSignature(String apiSecret, String dateTime, String salt) throws Exception {
        String message = dateTime + salt;
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(apiSecret.getBytes(), HMAC_ALGORITHM));
        byte[] rawHmac = mac.doFinal(message.getBytes());
        return bytesToHex(rawHmac);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
