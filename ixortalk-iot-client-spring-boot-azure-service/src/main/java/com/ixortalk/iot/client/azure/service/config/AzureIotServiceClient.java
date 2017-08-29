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
import com.microsoft.azure.sdk.iot.service.DeliveryAcknowledgement;
import com.microsoft.azure.sdk.iot.service.FeedbackBatch;
import com.microsoft.azure.sdk.iot.service.Message;
import com.microsoft.azure.sdk.iot.service.ServiceClient;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubDeviceMaximumQueueDepthExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AzureIotServiceClient implements IotClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureIotServiceClient.class);

    private ServiceClient serviceClient;

    public AzureIotServiceClient(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @Override
    public void publish(String topic, String payload) throws RuntimeException {
        try {

            List<CompletableFuture<Void>> futureList = new ArrayList<CompletableFuture<Void>>();

            Message messageToSend = new Message(payload);
            messageToSend.setDeliveryAcknowledgement(DeliveryAcknowledgement.Full);

            // Setting standard properties
            messageToSend.setMessageId(java.util.UUID.randomUUID().toString());

//        // Setting user properties
//        propertiesToSend.clear();
//        propertiesToSend.put("key_" + Integer.toString(i), "value_" + Integer.toString(i));
//        messageToSend.setProperties(propertiesToSend);

            // send the message
            CompletableFuture<Void> future = serviceClient.sendAsync(deviceId, messageToSend);
            futureList.add(future);

            System.out.println("Waiting for all sends to be completed...");
            for (CompletableFuture<Void> future : futureList)
            {
                try
                {
                    future.get();
                }
                catch (ExecutionException e)
                {
                    if (e.getCause() instanceof IotHubDeviceMaximumQueueDepthExceededException)
                    {
                        System.out.println("Maximum queue depth reached");
                    }
                    else
                    {
                        System.out.println("Exception : " + e.getMessage());
                    }
                }
            }
            System.out.println("All sends completed !");

            System.out.println("Waiting for the feedback...");
            CompletableFuture<FeedbackBatch> future = feedbackReceiver.receiveAsync(); // Default timeout is 60 seconds. [DEFAULT_TIMEOUT_MS = 60000]
            FeedbackBatch feedbackBatch = future.get();

            if (feedbackBatch != null) // check if any feedback was received
            {
                System.out.println(" Feedback received, feedback time: " + feedbackBatch.getEnqueuedTimeUtc());
                System.out.println(" Record size: " + feedbackBatch.getRecords().size());

                for (int i=0; i < feedbackBatch.getRecords().size(); i++)
                {
                    System.out.println(" Messsage id : " + feedbackBatch.getRecords().get(i).getOriginalMessageId());
                    System.out.println(" Device id : " + feedbackBatch.getRecords().get(i).getDeviceId());
                    System.out.println(" Status description : " + feedbackBatch.getRecords().get(i).getDescription());
                }
            }
            else
            {
                System.out.println("No feedback received");
            }


        } catch (Exception e) {
            throw new RuntimeException("Error publishing to Azure IoT: " + e.getMessage(), e);
        }    }

    @Override
    public void publish(String payload) throws RuntimeException {
        throw new IllegalArgumentException("Error publishing to device: Need to specify a deviceId.");

    }


}
