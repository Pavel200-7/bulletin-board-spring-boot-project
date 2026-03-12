package com.example.rabbitMQ_events_contracts.contracts;

public class QueueContract {
    public static final String USER_QUEUE = "user.queue";

    public static final String BULLETIN_USER_REGISTERED_QUEUE = "bulletin.user_registered_queue";
    public static final String BULLETIN_USER_BLOCKED_QUEUE = "bulletin.user_blocked_queue";
    public static final String BULLETIN_USER_UNBLOCKED_QUEUE = "bulletin.user_unblocked_queue";

    public static final String NOTIFICATION_USER_REGISTERED_QUEUE = "notification.user_registered_queue";
    public static final String NOTIFICATION_BULLETIN_PUBLISHED_QUEUE = "notification.bulletin_published_queue";

    private QueueContract() {}
}
