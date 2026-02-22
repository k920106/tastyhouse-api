package com.tastyhouse.external.sms.solapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SolapiMessageRequest {

    private List<SolapiMessage> messages;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SolapiMessage {
        private String to;
        private String from;
        private String text;
        private String type;
        private String subject;
    }
}
