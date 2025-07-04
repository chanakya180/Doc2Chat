package com.chanakya.doc2chat.service;


import com.chanakya.doc2chat.dto.ChatRequest;
import com.chanakya.doc2chat.dto.ChatResponse;

public interface DocumentService {
    void ingestDocuments();
    ChatResponse chatWithDocument(ChatRequest chatRequest);
}
