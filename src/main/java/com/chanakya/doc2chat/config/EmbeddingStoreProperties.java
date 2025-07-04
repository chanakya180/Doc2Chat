package com.chanakya.doc2chat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "embedding-store")
public class EmbeddingStoreProperties {
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private String table;
    private boolean useIndex;
    private int indexListSize;
    private boolean createTable;
    private boolean dropTableFirst;
}
