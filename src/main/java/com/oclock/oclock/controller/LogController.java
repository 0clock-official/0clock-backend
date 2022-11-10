package com.oclock.oclock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

  @GetMapping("/log")
  public void log() {
    logger.info("로깅 발생!");
    logger.debug("Debug");
  }
}
