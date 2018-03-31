/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.iot.client.aws.config;

import com.amazonaws.services.iot.client.AWSIotConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ixortalk.iot.client.aws")
public class AwsIotClientProperties {

    private String endpoint;
    private String defaultTopic;
    private String clientId;
    private String certificateFile;
    private String privateKeyFile;
    private Integer maxConnectionRetries = AWSIotConfig.MAX_CONNECTION_RETRIES;
    private Integer baseRetryDelay = AWSIotConfig.CONNECTION_BASE_RETRY_DELAY;
    private Integer connectionTimeout = AWSIotConfig.CONNECTION_TIMEOUT;
    private Integer keepAliveInterval = AWSIotConfig.KEEP_ALIVE_INTERVAL;
    private Integer maxOfflineQueueSize = AWSIotConfig.MAX_OFFLINE_QUEUE_SIZE;
    private Integer maxRetryDelay = AWSIotConfig.CONNECTION_MAX_RETRY_DELAY;
    private Integer numOfClientThreads  = AWSIotConfig.NUM_OF_CLIENT_THREADS;
    private Integer serverAckTimeout  = AWSIotConfig.SERVER_ACK_TIMEOUT;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(String certificateFile) {
        this.certificateFile = certificateFile;
    }

    public String getPrivateKeyFile() {
        return privateKeyFile;
    }

    public void setPrivateKeyFile(String privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }

    public Integer getMaxConnectionRetries() {
        return maxConnectionRetries;
    }

    public void setMaxConnectionRetries(Integer maxConnectionRetries) {
        this.maxConnectionRetries = maxConnectionRetries;
    }

    public Integer getBaseRetryDelay() {
        return baseRetryDelay;
    }

    public void setBaseRetryDelay(Integer baseRetryDelay) {
        this.baseRetryDelay = baseRetryDelay;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(Integer keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public Integer getMaxOfflineQueueSize() {
        return maxOfflineQueueSize;
    }

    public void setMaxOfflineQueueSize(Integer maxOfflineQueueSize) {
        this.maxOfflineQueueSize = maxOfflineQueueSize;
    }

    public Integer getMaxRetryDelay() {
        return maxRetryDelay;
    }

    public void setMaxRetryDelay(Integer maxRetryDelay) {
        this.maxRetryDelay = maxRetryDelay;
    }

    public Integer getNumOfClientThreads() {
        return numOfClientThreads;
    }

    public void setNumOfClientThreads(Integer numOfClientThreads) {
        this.numOfClientThreads = numOfClientThreads;
    }

    public Integer getServerAckTimeout() {
        return serverAckTimeout;
    }

    public void setServerAckTimeout(Integer serverAckTimeout) {
        this.serverAckTimeout = serverAckTimeout;
    }
}
