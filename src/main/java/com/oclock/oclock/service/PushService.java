package com.oclock.oclock.service;

import com.google.firebase.messaging.Message;
import com.oclock.oclock.dto.Member;

public interface PushService {
    void pushMessage(Message message);
}
