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

import com.ixortalk.iot.client.core.IotClient;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureIotDeviceClient implements IotClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureIotDeviceClient.class);

    private DeviceClient deviceClient;

    public AzureIotDeviceClient(DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    @Override
    public void publish(String topic, String payload) throws RuntimeException {
        throw new IllegalArgumentException("Error publishing to Azure IoT: No custom topics supported in the Azure Device SDK.");
    }

    @Override
    public void publish(String payload) throws RuntimeException {
        try {
            Object lockobj = new Object();
            EventCallback callback = new EventCallback();
            deviceClient.sendEventAsync(new Message(payload), callback, lockobj);

            synchronized (lockobj) {
                lockobj.wait();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error publishing to Azure IoT: " + e.getMessage(), e);
        }
    }

    private static class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            LOGGER.info("IoT Hub responded to message with status: " + status.name());

            if (context != null) {
                synchronized (context) {
                    context.notify();
                }
            }
        }
    }

}
