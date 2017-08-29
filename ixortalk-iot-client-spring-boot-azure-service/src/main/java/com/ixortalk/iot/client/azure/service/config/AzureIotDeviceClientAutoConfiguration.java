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
package com.ixortalk.iot.client.azure.service.config;

import com.ixortalk.iot.client.core.IotClient;
import com.ixortalk.iot.client.core.config.IotClientConfiguration;
import com.ixortalk.iot.client.core.config.IotListenerFactory;
import com.microsoft.azure.sdk.iot.service.FeedbackReceiver;
import com.microsoft.azure.sdk.iot.service.FileUploadNotificationReceiver;
import com.microsoft.azure.sdk.iot.service.IotHubServiceClientProtocol;
import com.microsoft.azure.sdk.iot.service.ServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

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
        return new AzureIotServiceClient(serviceClient());
    }

    @Bean
    public IotListenerFactory iotListenerFactory() {
        return new AzureIotDeviceListenerFactory(serviceClient());
    }

    private static FeedbackReceiver feedbackReceiver = null;
    private static FileUploadNotificationReceiver fileUploadNotificationReceiver = null;

    private static final int MAX_COMMANDS_TO_SEND = 5; // maximum commands to send in a loop
    private static final int RECEIVER_TIMEOUT = 10000; // Timeout in ms

    @Bean
    public ServiceClient serviceClient() {

        try {
            ServiceClient serviceClient = ServiceClient.createFromConnectionString(azureIotDeviceClientProperties.getDeviceConnectionString(), IotHubServiceClientProtocol.AMQPS);

            CompletableFuture<Void> future = serviceClient.openAsync();
            future.get();

            if (serviceClient != null)
            {
                feedbackReceiver = serviceClient.getFeedbackReceiver();
                if (feedbackReceiver != null)
                {
                    CompletableFuture<Void> future2 = feedbackReceiver.openAsync();
                    future2.get();
                    System.out.println("********* Successfully opened FeedbackReceiver...");
                }
            }

            return serviceClient;
        } catch (Exception e) {
            throw new IllegalStateException("Could not start Aure IoT Mqtt Client: " + e.getMessage(), e);
        }


    }

}
