package com.chanakya.langchain4jimpl.service.impl;

import com.chanakya.langchain4jimpl.ai.Assistant;
import com.chanakya.langchain4jimpl.dto.ChatRequest;
import com.chanakya.langchain4jimpl.dto.ChatResponse;
import com.chanakya.langchain4jimpl.service.DocumentService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Value("${docs.path}")
    private String docsPath;

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final Assistant assistant;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public DocumentServiceImpl(Assistant assistant, EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.assistant = assistant;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    @Override
    public void ingestDocuments() {

        List<File> files;
        try (Stream<Path> walk = Files.walk(Paths.get(docsPath))) {
            files = walk
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                    .map(Path::toFile)
                    .toList();
        } catch (IOException e) {
            log.error("Error while walking the path: {}", docsPath, e);
            return;
        }

        log.info("Found {} PDF files to ingest", files.size());

        if (files.isEmpty()) {
            return;
        }

        DocumentSplitter splitter = DocumentSplitters.recursive(2048, 0);

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        files.parallelStream().forEach(file -> {
            Document document = FileSystemDocumentLoader.loadDocument(file.toPath(), new ApachePdfBoxDocumentParser());
            ingestor.ingest(document);
            log.info("Ingested document: {}", file.getName());
        });
    }

    @Override
    public ChatResponse chatWithDocument(ChatRequest chatRequest) {
        Embedding content = embeddingModel.embed(chatRequest.getMessage()).content();
        EmbeddingSearchResult<TextSegment> search = embeddingStore.search(
                EmbeddingSearchRequest.builder()
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
}
