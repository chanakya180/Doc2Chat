package com.chanakya.doc2chat.controller;

import com.chanakya.doc2chat.dto.ChatRequest;
import com.chanakya.doc2chat.dto.ChatResponse;
import com.chanakya.doc2chat.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    @PostMapping("/ingest")
    public ResponseEntity<ChatResponse> ingestDocuments(@RequestParam(name = "file", required = false) MultipartFile file, Principal principal) {
      String userId = principal.getName();
      log.debug("Ingest requested by user: {}", userId);
      documentService.ingestDocuments(file, userId);
      return ResponseEntity.ok().body(new ChatResponse("Documents ingested successfully for user: " + userId));
    }

    @GetMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest, Principal principal) {
      String userId = principal.getName();
      log.debug("Chat requested by user: {}", userId);
      ChatResponse chatResponse = documentService.chatWithDocument(chatRequest, userId);
      return ResponseEntity.ok().body(chatResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listDocuments(Principal principal) {
      String userId = principal.getName();
      log.debug("List of documents requested by user: {}", userId);
      List<String> strings = documentService.listDocuments(userId);
      return ResponseEntity.ok().body(strings);
    }
}
