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
import com.ixortalk.iot.client.core.config.IotListenerFactory;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import org.apache.commons.lang3.StringUtils;

public class AzureIotDeviceListenerFactory implements IotListenerFactory {

    private DeviceClient deviceClient;

    public AzureIotDeviceListenerFactory(DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    @Override
    public void createIotListener(IotListenerEndpoint iotListenerEndpoint, ObjectMapper objectMapper) {

        if (!StringUtils.isEmpty(iotListenerEndpoint.getTopic())) {
            throw new RuntimeException("Azure IoT device listener does not support user-defined topics");
        }

        AzureIotDeviceListener azureIotDeviceListener = new AzureIotDeviceListener(iotListenerEndpoint, objectMapper);

        try {
            deviceClient.setMessageCallback(azureIotDeviceListener,null);
        } catch (Exception e) {
            throw new RuntimeException("Could not create AWS listener for iotListenerEndpoint " + iotListenerEndpoint.getId() + ": " + e.getMessage(), e);
        }
    }
}
