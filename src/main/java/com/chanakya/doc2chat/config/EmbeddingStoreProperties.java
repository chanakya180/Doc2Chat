package com.chanakya.langchain4jimpl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public boolean isUseIndex() {
        return useIndex;
    }

    public void setUseIndex(boolean useIndex) {
        this.useIndex = useIndex;
    }

    public int getIndexListSize() {
        return indexListSize;
    }

    public void setIndexListSize(int indexListSize) {
        this.indexListSize = indexListSize;
    }

    public boolean isCreateTable() {
        return createTable;
    }

    public void setCreateTable(boolean createTable) {
        this.createTable = createTable;
    }

    public boolean isDropTableFirst() {
        return dropTableFirst;
    }

    public void setDropTableFirst(boolean dropTableFirst) {
        this.dropTableFirst = dropTableFirst;
    }
}
