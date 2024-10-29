package com.fincords.dto.respone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseMaster {
    private Integer status;
    private String message;
    private Object data;
}
