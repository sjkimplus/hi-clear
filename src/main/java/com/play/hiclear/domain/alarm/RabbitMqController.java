//package com.play.hiclear.domain.alarm;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@Slf4j
//@RequiredArgsConstructor
//@RestController
//public class RabbitMqController {
//
//    private final RabbitMqService rabbitMqService;
//
//    /**
//     * Queue로 메시지를 발행
//     *
//     * @param messageDto 발행할 메시지의 DTO 객체
//     * @return ResponseEntity 객체로 응답을 반환
//     */
//    @RequestMapping(value = "/send/message", method = RequestMethod.POST)
//    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
//        rabbitMqService.sendMessage(messageDto);
//        return ResponseEntity.ok("Message sent to RabbitMQ!");
//    }
//}
