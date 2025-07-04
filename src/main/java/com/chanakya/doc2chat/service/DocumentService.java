package com.chanakya.langchain4jimpl.service;

import com.chanakya.langchain4jimpl.dto.ChatRequest;
import com.chanakya.langchain4jimpl.dto.ChatResponse;

public interface DocumentService {
    void ingestDocuments();
    ChatResponse chatWithDocument(ChatRequest chatRequest);
}
