package com.example.notification.application.service.notification.helper.template;

import com.example.notification.application.service.notification.data.request.SendBulletinPublishedNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final TemplateEngine templateEngine;

    @Override
    public String buildBulletinPublishedEmail(SendBulletinPublishedNotificationRequest request) {
        Context context = new Context();
        context.setVariable("publisherName", request.getPublisherName());
        context.setVariable("bulletinName", request.getBulletinName());

        context.setVariable("bulletinUrl", "/bulletin/" + request.getPublisherId());
        context.setVariable("bulletinPrice", request.getPrice());

        return templateEngine.process("email/bulletin-published", context);
    }
}
