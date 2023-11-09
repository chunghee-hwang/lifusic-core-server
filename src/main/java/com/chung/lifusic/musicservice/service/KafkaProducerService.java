package com.chung.lifusic.musicservice.service;

import com.chung.lifusic.musicservice.dto.FileDeleteRequestDto;
import com.chung.lifusic.musicservice.dto.FileCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void produceCreateFileRequest(FileCreateRequestDto fileCreateRequestDto) {
        final String TOPIC = "CREATE_FILE";
        produce(TOPIC, fileCreateRequestDto);
    }

    public void produceDeleteFileRequest(FileDeleteRequestDto fileDeleteRequestDto) {
        final String TOPIC = "DELETE_FILE";
        produce(TOPIC, fileDeleteRequestDto);
    }

    private void produce(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }
}
