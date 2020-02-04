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
package com.ixortalk.iot.client.autoconfigure;

import com.ixortalk.iot.client.azure.device.config.AzureIotDeviceClient;
import com.ixortalk.iot.client.azure.device.config.AzureIotDeviceClientProperties;
import com.ixortalk.iot.client.azure.device.config.AzureIotDeviceListenerFactory;
import com.ixortalk.iot.client.core.ConnectionEventHandler;
import com.ixortalk.iot.client.core.IotClient;
import com.ixortalk.iot.client.core.config.IotClientConfiguration;
import com.ixortalk.iot.client.core.config.IotListenerFactory;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.List;

import static com.microsoft.azure.sdk.iot.device.IotHubConnectionState.CONNECTION_DROP;
import static com.microsoft.azure.sdk.iot.device.IotHubConnectionState.CONNECTION_SUCCESS;

@Configuration
@EnableConfigurationProperties(AzureIotDeviceClientProperties.class)
@ConditionalOnClass(DeviceClient.class)
@Import(IotClientConfiguration.class)
public class AzureIotDeviceClientAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureIotDeviceClientAutoConfiguration.class);

    @Inject
    private AzureIotDeviceClientProperties azureIotDeviceClientProperties;

    @Autowired(required = false)
    private List<ConnectionEventHandler> connectionEventHandlers;

    @Bean
    public IotClient iotClient() {
        return new AzureIotDeviceClient(deviceClient());
    }

    @Bean
    public IotListenerFactory iotListenerFactory() {
        return new AzureIotDeviceListenerFactory(deviceClient());
    }

    @Bean
    public DeviceClient deviceClient() {

        try {
            DeviceClient client = new DeviceClient(azureIotDeviceClientProperties.getDeviceConnectionString(), IotHubClientProtocol.MQTT);
            client.registerConnectionStateCallback(
                    (state, callbackContext) -> {
                        if (state == CONNECTION_SUCCESS) {
                            connectionEventHandlers.forEach(ConnectionEventHandler::onConnectionSuccess);
                        } else if (state == CONNECTION_DROP) {
                            connectionEventHandlers.forEach(ConnectionEventHandler::onConnectionFailure);
                        }
                    },
                    null);
            client.open();
            return client;
        } catch (Exception e) {
            throw new IllegalStateException("Could not start Aure IoT Mqtt Client: " + e.getMessage(), e);
        }


    }

}
