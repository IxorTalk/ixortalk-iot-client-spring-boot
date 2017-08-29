# Introduction

Sample Spring Boot application using the IxorTalk IoT Client library to send/receive messages on Microsoft Azure or Amazon Web Services (AWS)

# Starting

The sample Spring Boot application contains 2 profiles

- Azure
- AWS

For detailed configuration read on, but here's a fast way to get started:

## For Azure

```
java -Dspring.profiles.active=azure -Dixortalk.iot.client.azure.device.device_connection_string="<YOUR DEVICE CONNECTION STRING>" -jar ixortalk-iot-client-spring-boot-sampleapp-0.0.1-SNAPSHOT.jar
```

## For AWS

```
java -Dspring.profiles.active=aws -jar -Dixortalk.iot.client.aws.endpoint="<ENDPOINT>.iot.eu-central-1.amazonaws.com" -Dixortalk.iot.client.aws.certificate-file="<certificate.pem.crt>" -Dixortalk.iot.client.aws.private-key-file="<private.pem.key>" ixortalk-iot-client-spring-boot-sampleapp-0.0.1-SNAPSHOT.jar
```

# Configuration

The IoT client beans are auto-configured with the following configuration :

## AWS
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

## Azure 
```
ixortalk:
  iot:
    client:
      azure:
        device:
           device-connection-string: "HostName=your-iothub.azure-devices.net;DeviceId=device1;SharedAccessKey=XXXXXXXXXXXXXXXXXXX="
```

# Sending messages

To send a message, execute the following post :

```
curl -v -X POST -H "Content-Type: application/json"  -d '{"deviceid":"arduino-mkr1000","windspeed":13,"temperature":28.25,"humidity":60,"time":"2017:08:28T08:49:56"}' http://localhost:8080/send
```

This will trigger a REST endpoint that uses the IoT Client to publish the message
```
    @Inject
    private IotClient iotClient;

    @PostMapping
    @RequestMapping(method = POST, path = "/send")
    public ResponseEntity control(@RequestBody String payload) {
        iotClient.publish(payload);
        return ok().build();
    }
```    

# Receiving messages

When sending messages to the designated topic, the Spring Boot application will log the message:

```
2017-08-28 10:58:17.742  INFO 57622 --- [pool-1-thread-1] c.i.iot.client.sampleapp.Listener        : Consumed message: {Name=TurnFanOff, Parameters={}}
```

This is due to the presence of an IoTListener method that simply logs the messages

```
    @IotListener(topic = Controller.TOPIC)
    public void listen(Object message) {
        LOGGER.info("Consumed message: " + message);
    }
```


# Azure specifics

Keep in mind that for device2cloud and cloud2device communication, The Azure Device SDK does not use user-defined topics.
The underlying device specific topics used are abstracted away by the device SDK

