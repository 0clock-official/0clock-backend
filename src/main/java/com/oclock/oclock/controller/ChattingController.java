package com.oclock.oclock.controller;

import com.oclock.oclock.service.ChattingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ChattingController {
    @Autowired
    private ChattingService chattingService;
}
