package com.example.productomain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.Instant;

@Service
public class SqsService {

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public SqsService(@Value("${aws.region:us-east-1}") String region) {
        this.sqsClient = SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public void enviarEvento(String accion, Long productoId, String nombre, String usuario) {
        String mensaje = String.format(
                "{\"accion\":\"%s\",\"productoId\":%d,\"nombre\":\"%s\",\"usuario\":\"%s\",\"fecha\":\"%s\"}",
                accion, productoId, nombre, usuario, Instant.now().toString()
        );

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(mensaje)
                .build());
    }
}
