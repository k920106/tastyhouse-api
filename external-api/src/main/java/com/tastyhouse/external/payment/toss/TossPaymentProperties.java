package com.tastyhouse.external.payment.toss;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties {

    private String secretKey;
    private String baseUrl = "https://api.tosspayments.com";
    private String confirmPath = "/v1/payments/confirm";
}
