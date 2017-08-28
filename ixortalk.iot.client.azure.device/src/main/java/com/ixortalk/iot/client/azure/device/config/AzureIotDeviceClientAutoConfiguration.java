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
import com.ixortalk.iot.client.core.config.IotClientConfiguration;
import com.ixortalk.iot.client.core.config.IotListenerFactory;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@EnableConfigurationProperties(AzureIotDeviceClientProperties.class)
@ConditionalOnProperty(prefix = "ixortalk.iot.client.azure.device", value = "device-connection-string")
@Import(IotClientConfiguration.class)
public class AzureIotDeviceClientAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureIotDeviceClientAutoConfiguration.class);

    @Inject
    private AzureIotDeviceClientProperties azureIotDeviceClientProperties;

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
            client.open();
            return client;
        } catch (Exception e) {
            throw new IllegalStateException("Could not start Aure IoT Mqtt Client: " + e.getMessage(), e);
        }


    }

}
