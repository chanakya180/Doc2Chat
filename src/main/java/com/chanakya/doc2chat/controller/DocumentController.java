package com.chanakya.doc2chat.controller;

import com.chanakya.doc2chat.dto.ChatRequest;
import com.chanakya.doc2chat.dto.ChatResponse;
import com.chanakya.doc2chat.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/ingest")
    public ResponseEntity<ChatResponse> ingestDocuments(Principal principal) {
      String userId = principal.getName();
      documentService.ingestDocuments(userId);
      log.debug("Ingest requested by user: {}", userId);
      return ResponseEntity.ok().body(new ChatResponse("Documents ingested successfully for user: " + userId));
    }

    @GetMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest, Principal principal) {
      String userId = principal.getName();
      ChatResponse chatResponse = documentService.chatWithDocument(chatRequest, userId);
      log.debug("Chat requested by user: {}", userId);
      return ResponseEntity.ok().body(chatResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, String>>> listDocuments(Principal principal) {
      String userId = principal.getName();
      System.out.println("List requested by user: " + userId);
      List<Map<String, String>> docs = List.of(
              Map.of("id", "doc1", "name", "Document 1"),
              Map.of("id", "doc2", "name", "Document 2")
      );
      return ResponseEntity.ok(docs);
    }
}
