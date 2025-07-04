package com.chanakya.doc2chat.controller;

import com.chanakya.doc2chat.dto.ChatRequest;
import com.chanakya.doc2chat.dto.ChatResponse;
import com.chanakya.doc2chat.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/ingest")
    public ResponseEntity<ChatResponse> ingestDocuments() {
        documentService.ingestDocuments();
        return ResponseEntity.ok().body(new ChatResponse("Documents ingested successfully"));
    }

    @GetMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest) {
        ChatResponse chatResponse = documentService.chatWithDocument(chatRequest);
        return ResponseEntity.ok().body(chatResponse);
    }
}
