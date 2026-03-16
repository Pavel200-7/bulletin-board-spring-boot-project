package com.example.chat.application.service.message.base.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteChatMessageResponse {
    @Builder.Default
    private boolean succeed = true;
}
