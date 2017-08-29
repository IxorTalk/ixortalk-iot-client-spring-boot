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
import com.ixortalk.iot.client.core.IotClient;

import javax.inject.Inject;

public class AwsIotClient implements IotClient {

    private AWSIotMqttClient awsIotMqttClient;

    @Inject
    private AwsIotClientProperties awsIotClientProperties;


    public AwsIotClient(AWSIotMqttClient awsIotMqttClient) {
        this.awsIotMqttClient = awsIotMqttClient;
    }

    @Override
    public void publish(String topic, String payload) throws RuntimeException {
        try {
            awsIotMqttClient.publish(topic, payload);
        } catch (AWSIotException e) {
            throw new RuntimeException("Error publishing to AWS IoT: " + e.getMessage(), e);
        }
    }

    @Override
    public void publish(String payload) throws RuntimeException {
        if (awsIotClientProperties.getDefaultTopic()==null) {
            throw new RuntimeException("Unable to publish to AWS IoT, no default topic provided.");
        }
        publish(awsIotClientProperties.getDefaultTopic(),payload);

    }
}
