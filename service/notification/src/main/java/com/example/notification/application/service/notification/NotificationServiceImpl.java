package com.example.notification.application.service.notification;

import com.example.notification.application.service.notification.data.request.SendBulletinPublishedNotificationRequest;
import com.example.notification.application.service.notification.helper.iterator.builder.SubscriptionIteratorFactory;
import com.example.notification.application.service.notification.helper.template.EmailTemplateService;
import com.example.notification.domain.entity.Subscription;
import com.example.notification.domain.enums.NotificationType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailTemplateService templateService;
    private final SubscriptionIteratorFactory iteratorFactory;
    private final JavaMailSender emailSender;

    @Override
    @Transactional(readOnly = true)
    public void sendBulletinPublishedNotification(SendBulletinPublishedNotificationRequest request) {
        String template = templateService.buildBulletinPublishedEmail(request);
        String subject = "Опубликованно новое объявление";

        Iterable<Subscription> iterable = iteratorFactory.createSubscriptionIterator(
                NotificationType.BULLETIN_PUBLISHED,
                request.getPublisherId());

        AtomicInteger sentCounter = new AtomicInteger();
        iterable.iterator().forEachRemaining(subscription -> {
            String email = subscription.getOwner().getEmail();
            MimeMessage message = createMimeMessage(template, email, subject);
            emailSender.send(message);
            sentCounter.getAndIncrement();
        });
        log.info("Было отправлено {} сообщений.", sentCounter.get());
    }

    private MimeMessage createMimeMessage(String body, String email, String subject) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("Pavel200-7@yandex.ru");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            return message;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
