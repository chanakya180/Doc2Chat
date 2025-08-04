package com.chanakya.doc2chat.service;


import com.chanakya.doc2chat.dto.ChatRequest;
import com.chanakya.doc2chat.dto.ChatResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    List<String> listDocuments(String userId);
    void ingestDocuments(MultipartFile file, String userId);
    ChatResponse chatWithDocument(ChatRequest chatRequest, String userId);
}
