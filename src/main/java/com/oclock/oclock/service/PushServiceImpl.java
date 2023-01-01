package com.oclock.oclock.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.oclock.oclock.dto.Member;
import org.springframework.stereotype.Service;

@Service
public class PushServiceImpl implements PushService {
    @Override
    public void pushMessage(Message message) {
        try {
            String response = FirebaseMessaging.getInstance(FirebaseApp.getInstance("oclock")).send(message);
        }catch (FirebaseMessagingException e){
            e.printStackTrace();
        }
    }
}
