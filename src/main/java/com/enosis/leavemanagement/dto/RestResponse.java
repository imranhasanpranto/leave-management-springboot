package com.enosis.leavemanagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RestResponse {
    private boolean status;
    private String message;
}
