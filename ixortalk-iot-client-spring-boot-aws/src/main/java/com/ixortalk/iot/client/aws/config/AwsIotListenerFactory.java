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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ixortalk.iot.client.core.config.IotListenerEndpoint;
import com.ixortalk.iot.client.core.config.IotListenerFactory;
import org.springframework.util.StringUtils;

import javax.inject.Inject;

public class AwsIotListenerFactory implements IotListenerFactory {

    private AWSIotMqttClient awsIotMqttClient;

    public AwsIotListenerFactory(AWSIotMqttClient awsIotMqttClient) {
        this.awsIotMqttClient = awsIotMqttClient;
    }

    @Inject
    private AwsIotClientProperties awsIotClientProperties;

    @Override
    public void createIotListener(IotListenerEndpoint iotListenerEndpoint, ObjectMapper objectMapper) {

        if (StringUtils.isEmpty(iotListenerEndpoint.getTopic())) {
            if (StringUtils.isEmpty(awsIotClientProperties.getDefaultTopic())) {
                throw new RuntimeException("AWS IoT listener requires a user-defined topic");
            } else {
                iotListenerEndpoint.setTopic(awsIotClientProperties.getDefaultTopic());
            }
        }

        AwsIotListener awsIotListener = new AwsIotListener(iotListenerEndpoint, objectMapper);
        try {
            awsIotMqttClient.subscribe(awsIotListener);
        } catch (AWSIotException e) {
            throw new RuntimeException("Could not create AWS listener for iotListenerEndpoint " + iotListenerEndpoint.getId() + ": " + e.getMessage(), e);
        }
    }
}
