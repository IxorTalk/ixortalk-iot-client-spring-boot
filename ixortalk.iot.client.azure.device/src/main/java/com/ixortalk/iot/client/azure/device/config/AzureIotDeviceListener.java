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
package com.ixortalk.iot.client.azure.device.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ixortalk.iot.client.core.config.IotListenerEndpoint;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureIotDeviceListener implements com.microsoft.azure.sdk.iot.device.MessageCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureIotDeviceListener.class);

    private IotListenerEndpoint iotListenerEndpoint;

    private ObjectMapper objectMapper;

    public AzureIotDeviceListener(IotListenerEndpoint iotListenerEndpoint, ObjectMapper objectMapper) {
        this.iotListenerEndpoint = iotListenerEndpoint;
        this.objectMapper = objectMapper;
    }

    public IotHubMessageResult execute(Message message, Object context) {
        try {
            this.iotListenerEndpoint.getMethod().invoke(iotListenerEndpoint.getBean(), objectMapper.readValue(message.getBytes(), iotListenerEndpoint.getMethod().getParameterTypes()[0]));
        } catch (Throwable throwable) {
            LOGGER.error("Could not consume message: " + throwable.getMessage() + " content: " + new String(message.getBytes()), throwable);
        }

        return IotHubMessageResult.COMPLETE;
    }
}



