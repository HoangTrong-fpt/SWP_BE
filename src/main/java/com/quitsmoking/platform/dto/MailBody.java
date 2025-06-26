package com.quitsmoking.platform.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MailBody {
    private String to;
    private String subject;
    private String text;
}
