package com.tastyhouse.core.entity.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PolicyType {

    TERMS_OF_SERVICE("이용약관"),
    PRIVACY_POLICY("개인정보처리방침");

    private final String description;
}
