package com.rosetta.video.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "hbase")
@Component
public class HbaseProperties {

    private String quorum;

    private String port;

    private String rootDir;

    private String nodeParent;

    private Integer poolsize;

    public String getQuorum() {
        return quorum;
    }

    public void setQuorum(String quorum) {
        this.quorum = quorum;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getNodeParent() {
        return nodeParent;
    }

    public void setNodeParent(String nodeParent) {
        this.nodeParent = nodeParent;
    }

    public Integer getPoolsize() {
        return poolsize;
    }

    public void setPoolsize(Integer poolsize) {
        this.poolsize = poolsize;
    }
}
