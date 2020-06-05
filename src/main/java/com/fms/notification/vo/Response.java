package com.fms.notification.vo;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private String message;
    private T data;
}
