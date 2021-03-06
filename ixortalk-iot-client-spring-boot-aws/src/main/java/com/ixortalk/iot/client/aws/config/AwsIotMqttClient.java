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

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.ixortalk.iot.client.core.ConnectionEventHandler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.security.KeyStore;
import java.util.List;

public class AwsIotMqttClient extends AWSIotMqttClient {

    private List<ConnectionEventHandler> connectionEventHandlers;

    public AwsIotMqttClient(String clientEndpoint, String clientId, KeyStore keyStore, String keyPassword, List<ConnectionEventHandler> connectionEventHandlers) {
        super(clientEndpoint, clientId, keyStore, keyPassword);

        this.connectionEventHandlers = connectionEventHandlers;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connectOnStartUp() {
        try {
            connect();
        } catch (AWSIotException e) {
            throw new IllegalStateException("AWS IoT Mqtt Client could not connect: " + e.getMessage(), e);
        }
    }

    @Override
    public void onConnectionSuccess() {
        super.onConnectionSuccess();
        connectionEventHandlers.forEach(ConnectionEventHandler::onConnectionSuccess);
    }

    @Override
    public void onConnectionFailure() {
        super.onConnectionFailure();
        connectionEventHandlers.forEach(ConnectionEventHandler::onConnectionFailure);
    }
}
