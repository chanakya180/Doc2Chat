package com.chanakya.doc2chat.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatResponse {
    private String response;
    public ChatResponse(String response) {
        this.response = response;
    }
}
