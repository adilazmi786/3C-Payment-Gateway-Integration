package com.payment.poc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ThreeCConfig {

    @Value("${merchant_id}")
    private String eMerchantId;

    @Value("${validation_code}")
    private String validationCode;
    
    @Value("${template_id}")
    private String templateId;
}
