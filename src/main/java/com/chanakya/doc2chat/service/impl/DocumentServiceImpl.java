package com.chanakya.doc2chat.service.impl;

import com.chanakya.doc2chat.ai.Assistant;
import com.chanakya.doc2chat.dto.ChatRequest;
import com.chanakya.doc2chat.dto.ChatResponse;
import com.chanakya.doc2chat.service.DocumentService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    private final Assistant assistant;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public DocumentServiceImpl(Assistant assistant, EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.assistant = assistant;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    @Override
    public List<String> listDocuments(String userId) {
        EmbeddingSearchResult<TextSegment> search = embeddingStore.search(
                EmbeddingSearchRequest.builder()
                        .filter(new IsEqualTo("userId", userId))
                        .queryEmbedding(embeddingModel.embed("a").content())
                        .maxResults(100)
                        .build()
        );

        Set<String> fileNames = new HashSet<>();
        for (EmbeddingMatch<TextSegment> match : search.matches()) {
            String fileName = match.embedded().metadata().getString("fileName");
            if (fileName != null) {
                fileNames.add(fileName);
            }
        }

        return new ArrayList<>(fileNames);
    }

    @Override
    public void ingestDocuments(MultipartFile file, String userId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String safeFilename = sanitizeFilename(originalFilename);
            // Use a fixed prefix and a safe suffix (e.g., the extension from the sanitized filename)
            String suffix = "";
            int dotIdx = safeFilename.lastIndexOf('.');
            if (dotIdx != -1 && dotIdx < safeFilename.length() - 1) {
                suffix = safeFilename.substring(dotIdx);
            } else {
                suffix = ".tmp";
            }
            File tempFile = File.createTempFile("uploaded-", suffix);
            file.transferTo(tempFile);

            DocumentSplitter splitter = DocumentSplitters.recursive(2048, 0);

            DocumentTransformer userIdTransformer = document -> {
                Metadata metadata = document.metadata().copy();
                metadata.put("userId", userId);
                metadata.put("fileName", safeFilename);
                return Document.from(document.text(), metadata);
            };

            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentTransformer(userIdTransformer)
                    .documentSplitter(splitter)
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();

            // Now use the temp file
            Document document = FileSystemDocumentLoader.loadDocument(tempFile.toPath(), new ApachePdfBoxDocumentParser());
            ingestor.ingest(document);
            log.info("Ingested document: {}", safeFilename);
            tempFile.deleteOnExit();
        } catch (IOException e) {
            log.error("Failed to ingest document", e);
            throw new RuntimeException("Document ingestion failed", e);
        }
    }


    @Override
    public ChatResponse chatWithDocument(ChatRequest chatRequest, String userId) {
        Embedding content = embeddingModel.embed(chatRequest.getMessage()).content();
        EmbeddingSearchResult<TextSegment> search = embeddingStore.search(
          EmbeddingSearchRequest.builder()
            .filter(new IsEqualTo("userId", userId))
            .queryEmbedding(content)
            .maxResults(3)
            .build()
        );

        StringJoiner context = new StringJoiner("\n---\n");
        for (EmbeddingMatch<TextSegment> match : search.matches()) {
            context.add(match.embedded().text());
        }

        String prompt = String.format(
                "Context:\n%s\n\nQuestion:\n%s",
                context,
                chatRequest.getMessage()
        );

        return new ChatResponse(assistant.chat(prompt));
    }
    /**
     * Sanitizes a filename by extracting only the base name and ensuring it does not contain path separators or "..".
     * If the filename is invalid or null, returns "file".
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        // Remove any path components
        String baseName;
        try {
            baseName = Paths.get(filename).getFileName().toString();
        } catch (InvalidPathException e) {
            return "file";
        }
        // Disallow suspicious patterns
        if (baseName.contains("..") || baseName.contains("/") || baseName.contains("\\")) {
            return "file";
        }
        // Optionally, restrict to a safe pattern (e.g., alphanumerics, dot, dash, underscore)
        if (!baseName.matches("[\\w.\\- ]+")) {
            return "file";
        }
        return baseName;
    }
}
