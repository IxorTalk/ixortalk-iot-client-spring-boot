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

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.ixortalk.iot.client.aws.config.AwsIotClient;
import com.ixortalk.iot.client.aws.config.AwsIotClientProperties;
import com.ixortalk.iot.client.aws.config.AwsIotListenerFactory;
import com.ixortalk.iot.client.aws.config.PrivateKeyReader;
import com.ixortalk.iot.client.core.IotClient;
import com.ixortalk.iot.client.core.config.IotClientConfiguration;
import com.ixortalk.iot.client.core.config.IotListenerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;

@Configuration
@EnableConfigurationProperties(AwsIotClientProperties.class)
@ConditionalOnClass(AWSIotMqttClient.class)
@Import(IotClientConfiguration.class)
public class AwsIotClientAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsIotClientAutoConfiguration.class);

    @Inject
    private AwsIotClientProperties awsIotClientProperties;

    @Bean
    public IotClient iotClient() {
        return new AwsIotClient(awsIotMqttClient());
    }

    @Bean
    public IotListenerFactory iotListenerFactory() {
        return new AwsIotListenerFactory(awsIotMqttClient());
    }

    @Bean
    public AWSIotMqttClient awsIotMqttClient() {
        KeyStorePasswordPair pair = getKeyStorePasswordPair(awsIotClientProperties.getCertificateFile(), awsIotClientProperties.getPrivateKeyFile());
        AWSIotMqttClient client = new AWSIotMqttClient(awsIotClientProperties.getEndpoint(), awsIotClientProperties.getClientId(), pair.keyStore, pair.keyPassword);

        client.setMaxConnectionRetries(awsIotClientProperties.getMaxConnectionRetries());
        client.setBaseRetryDelay(awsIotClientProperties.getBaseRetryDelay());
        client.setConnectionTimeout(awsIotClientProperties.getConnectionTimeout());
        client.setKeepAliveInterval(awsIotClientProperties.getKeepAliveInterval());
        client.setMaxOfflineQueueSize(awsIotClientProperties.getMaxOfflineQueueSize());
        client.setMaxRetryDelay(awsIotClientProperties.getMaxRetryDelay());
        client.setNumOfClientThreads(awsIotClientProperties.getNumOfClientThreads());
        client.setServerAckTimeout(awsIotClientProperties.getServerAckTimeout());

        try {
            client.connect();
        } catch (AWSIotException e) {
            throw new IllegalStateException("Could not start AWS IoT Mqtt Client: " + e.getMessage(), e);
        }

        return client;
    }

    private static class KeyStorePasswordPair {
        public KeyStore keyStore;
        public String keyPassword;

        public KeyStorePasswordPair(KeyStore keyStore, String keyPassword) {
            this.keyStore = keyStore;
            this.keyPassword = keyPassword;
        }
    }

    public static KeyStorePasswordPair getKeyStorePasswordPair(final String certificateFile, final String privateKeyFile) {
        if (certificateFile == null || privateKeyFile == null) {
            throw new IllegalArgumentException("Certificate or private key file missing");
        }

        LOGGER.info("Cert file:" +certificateFile + " Private key: "+ privateKeyFile);

        final PrivateKey privateKey = loadPrivateKeyFromFile(privateKeyFile);

        final List<Certificate> certChain = loadCertificatesFromFile(certificateFile);

        if (certChain == null || privateKey == null) return null;

        return getKeyStorePasswordPair(certChain, privateKey);
    }

    public static KeyStorePasswordPair getKeyStorePasswordPair(final List<Certificate> certificates, final PrivateKey privateKey) {
        KeyStore keyStore;
        String keyPassword;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            // randomly generated key password for the key in the KeyStore
            keyPassword = new BigInteger(128, new SecureRandom()).toString(32);

            Certificate[] certChain = new Certificate[certificates.size()];
            certChain = certificates.toArray(certChain);
            keyStore.setKeyEntry("alias", privateKey, keyPassword.toCharArray(), certChain);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new IllegalArgumentException("Failed to create key store: " + e.getMessage(), e);
        }

        return new KeyStorePasswordPair(keyStore, keyPassword);
    }

    private static List<Certificate> loadCertificatesFromFile(final String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            LOGGER.info("Certificate file: " + filename + " is not found.");
            return null;
        }

        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (List<Certificate>) certFactory.generateCertificates(stream);
        } catch (IOException | CertificateException e) {
            throw new IllegalArgumentException("Failed to load certificate file " + filename + ": " + e.getMessage(), e);
        }
    }

    private static PrivateKey loadPrivateKeyFromFile(final String filename) {
        PrivateKey privateKey = null;

        File file = new File(filename);
        if (!file.exists()) {
            throw new IllegalArgumentException("Private key file not found: " + filename);
        }
        try (DataInputStream stream = new DataInputStream(new FileInputStream(file))) {
            privateKey = PrivateKeyReader.getPrivateKey(stream, null);
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalArgumentException("Failed to load private key from file " + filename + ": " + e.getMessage(), e);
        }

        return privateKey;
    }
}
