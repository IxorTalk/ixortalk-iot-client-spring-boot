# IxorTalk IoT Client

The IxorTalk IoT Client is a Spring Boot library allowing you to connect to the Microsoft Azure and Amazon Web Services IoT offerings.

The library consists of a generic IoT Client (both for sending and receiving messages) and an Azure and AWS implementation
This will allow you to connect your Spring Boot application to either Azure IoT HUB or AWS IoT in a matter of seconds.
We also provide a sample Spring Boot application showing you how to work with the library.

# Cloud IoT offerings

Before you'll be able to use the library, you'll need to register a device on the cloud IoT offering.

## Amazon Web Services IoT

Before you'll be able to use the library, you'll need to register a device [on AWS IoT](http://docs.aws.amazon.com/iot/latest/developerguide/register-device.html).
During the creation of a thing in AWS IoT, you'll be prompted to download the x509 certificates (PEM files).
Store them somewhere as you'll need them to configure your AWS IoT connection.

## Microsoft Azure IoT HUB

If you're using Azure, you'll need to [setup an Azure Iot HUB](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-create-through-portal) if you haven't done so already
and [register a device in the IoT hub](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-raspberry-pi-kit-python-get-started) (don't worry, you don't need a Raspberry PI, you just need to obtain the device connection string. The Spring Boot app running on your computer will act as the device.)

Azure uses a device connection string that you can view in the portal. Take a note of it, as you'll be needing it in the Azure configuration.

# Maven Dependencies

In order to use the IoT client, you'll need to add the following maven dependencies

## Azure

When using Azure, add the following dependencies:

```
<dependency>
    <groupId>com.ixortalk</groupId>
    <artifactId>ixortalk.iot.client.core</artifactId>
    <version>${ixortalk.iot.client.core.version}</version>
</dependency>
<dependency>
    <groupId>com.ixortalk</groupId>
    <artifactId>ixortalk.iot.client.azure.device</artifactId>
    <version>${ixortalk.iot.client.azure.device.version}</version>
    <scope>runtime</scope>
</dependency>
```

And make sure you have the following configuration :

```
ixortalk:
  iot:
    client:
      azure:
        device:
           device-connection-string: "HostName=your-iothub.azure-devices.net;DeviceId=device1;SharedAccessKey=XXXXXXXXXXXXXXXXXXX="
```

The device connection string can be retrieved from the Azure portal

## AWS

When using AWS, add the following dependencies:

```
<dependency>
    <groupId>com.ixortalk</groupId>
    <artifactId>ixortalk.iot.client.core</artifactId>
    <version>${ixortalk.iot.client.core.version}</version>
</dependency>
<dependency>
    <groupId>com.ixortalk</groupId>
    <artifactId>ixortalk.iot.client.aws</artifactId>
    <version>${ixortalk.iot.client.aws.version}</version>
    <scope>runtime</scope>
</dependency>
```

And make sure you have the following configuration

```
ixortalk:
  iot:
    client:
      aws:
        endpoint: yourendpoint.iot.eu-central-1.amazonaws.com
        client-id: ixortalk-iot-client-${random.int}
        default-topic: sdk/test/java
        certificate-file: /etc/aws_iot/certificate.pem.crt
        private-key-file: /etc/aws_iot/private.pem.key
```

The pem files can be downloaded when you create a thing in the IoT thing registry.

# Publishing messages

To publish a message, simply inject the IoTClient bean and call the publish method.
```
@Inject
private IotClient iotClient;

@PostMapping
@RequestMapping(method = POST, path = "/send")
public ResponseEntity send(@RequestBody String payload) {
    iotClient.publish(payload);
    return ok().build();
}
```
When working with the AWS implementaton, you can specify an (optional) topic to publish to.
The Azure Device SDK does not support user-defined topics.


# Receiving messages

Annotate a method with the `@IoTListener` annotation and start receiving messages.
You can optionally specify the topic you want to listen on (only for AWS).
If no topic is provided, the AWS implementation will use the default-topic provided in the config.

```
@IotListener
public void listen(Object message) {
    LOGGER.info("Consumed message: " + message);
}
```

# License
```
The MIT License (MIT)

Copyright (c) 2016-present IxorTalk CVBA

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```


