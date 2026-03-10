package com.example.bulletin.application.service.tradeaccount.data.response;

import com.example.bulletin.application.data.response.TradeAccountResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveTradeAccountResponse {
    private TradeAccountResponse tradeAccountResponse;
}
