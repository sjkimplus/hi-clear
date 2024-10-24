package com.play.hiclear.domain.thread.controller;

import com.play.hiclear.domain.thread.service.ThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadService threadService;
}
