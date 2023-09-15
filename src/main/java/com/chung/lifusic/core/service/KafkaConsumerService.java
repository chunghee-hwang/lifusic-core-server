package com.chung.lifusic.core.service;

import com.chung.lifusic.core.entity.Music;
import com.chung.lifusic.core.dto.CommonResponseDto;
import com.chung.lifusic.core.dto.FileCreateResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "CREATE_FILE_COMPLETE")
    @Transactional
    public void consumeFileCreateComplete(ConsumerRecord<String, Object> record) {
        FileCreateResponseDto response = (FileCreateResponseDto) record.value();
        final boolean isSuccess = response.isSuccess();
        if (isSuccess) {
            Music music = Music.builder().build();
        }
        messagingTemplate.convertAndSend(
                "/topic/post/admin/music" + response.getContent().getRequestUserId(),
                CommonResponseDto.builder().success(isSuccess).build());
    }

    @KafkaListener(topics = "DELETE_FILE_COMPLETE")
    @Transactional
    public void consumeFileDeleteComplete(ConsumerRecord<String, Object> record) {

    }
}
