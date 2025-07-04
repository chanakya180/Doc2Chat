package com.chanakya.langchain4jimpl.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingStoreConfig {

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(
            EmbeddingModel embeddingModel,
            EmbeddingStoreProperties properties
    ) {
        return PgVectorEmbeddingStore.builder()
                .host(properties.getHost())
                .port(properties.getPort())
                .database(properties.getDatabase())
                .user(properties.getUser())
                .password(properties.getPassword())
                .table(properties.getTable())
                .useIndex(properties.isUseIndex())
                .indexListSize(properties.getIndexListSize())
                .createTable(properties.isCreateTable())
                .dropTableFirst(properties.isDropTableFirst())
                .dimension(embeddingModel.dimension())
                .build();
    }
}
