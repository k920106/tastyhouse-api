package com.tastyhouse.webapi.verification;

import com.tastyhouse.core.entity.verification.PhoneVerification;
import com.tastyhouse.core.entity.verification.PhoneVerificationStatus;
import com.tastyhouse.core.repository.verification.PhoneVerificationJpaRepository;
import com.tastyhouse.external.sms.solapi.SolapiSmsClient;
import com.tastyhouse.webapi.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhoneVerificationService {

    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final String SMS_TEXT_TEMPLATE = "[TastyHouse] 인증번호 [%s]를 입력해주세요.";

    private final PhoneVerificationJpaRepository phoneVerificationJpaRepository;
    private final SolapiSmsClient solapiSmsClient;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void sendVerificationCode(Long memberId, String phoneNumber) {
        // 기존 미완료 인증 건 만료 처리
        phoneVerificationJpaRepository.expireAllPendingByPhoneNumber(phoneNumber);

        String verificationCode = generateVerificationCode();

        phoneVerificationJpaRepository.save(
            PhoneVerification.builder()
                .phoneNumber(phoneNumber)
                .verificationCode(verificationCode)
                .build()
        );

        String smsText = SMS_TEXT_TEMPLATE.formatted(verificationCode);
        solapiSmsClient.sendSms(phoneNumber, smsText);

        log.info("휴대폰 인증번호 발송 완료. memberId: {}, phoneNumber: {}", memberId, phoneNumber);
    }

    @Transactional
    public String confirmVerificationCode(Long memberId, String phoneNumber, String verificationCode) {
        PhoneVerification verification = phoneVerificationJpaRepository
            .findTopByPhoneNumberAndStatusOrderByCreatedAtDesc(phoneNumber, PhoneVerificationStatus.PENDING)
            .orElseThrow(() -> new IllegalArgumentException("발송된 인증번호가 없습니다. 인증번호를 다시 요청해주세요."));

        if (verification.isExpired()) {
            verification.expire();
            throw new IllegalArgumentException("인증번호가 만료되었습니다. 인증번호를 다시 요청해주세요.");
        }

        if (!verification.getVerificationCode().equals(verificationCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        verification.verify();

        String phoneVerifyToken = jwtTokenProvider.createPhoneVerifyToken(memberId, phoneNumber);

        log.info("휴대폰 인증 완료. memberId: {}, phoneNumber: {}", memberId, phoneNumber);

        return phoneVerifyToken;
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.valueOf(code);
    }
}
