package com.wemingle.core.global.responseform;

import lombok.Builder;

@Builder
public record ResponseHandler<T>(String responseMessage,T responseData) {
}
