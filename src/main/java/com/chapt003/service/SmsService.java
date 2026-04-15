package com.chapt003.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    
    @Value("${aliyun.sms.access-key-id:}")
    private String accessKeyId;
    
    @Value("${aliyun.sms.access-key-secret:}")
    private String accessKeySecret;
    
    @Value("${aliyun.sms.sign-name:}")
    private String signName;
    
    @Value("${aliyun.sms.template-code:}")
    private String templateCode;
    
    public void sendPasswordResetCode(String mobile, String code) {
        try {
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);
            
            SendSmsRequest request = new SendSmsRequest();
            request.setSysMethod(MethodType.POST);
            request.setPhoneNumbers(mobile);
            request.setSignName(signName);
            request.setTemplateCode(templateCode);
            request.setTemplateParam("{\"code\":\"" + code + "\"}");
            
            SendSmsResponse response = client.getAcsResponse(request);
            
            if ("OK".equals(response.getCode())) {
                logger.info("Password reset code sent to mobile: {}", mobile);
            } else {
                logger.error("Failed to send SMS to mobile: {}, code: {}, message: {}", 
                    mobile, response.getCode(), response.getMessage());
                throw new RuntimeException("发送短信失败，请稍后重试");
            }
        } catch (ClientException e) {
            logger.error("Failed to send password reset SMS to mobile: {}", mobile, e);
            throw new RuntimeException("发送短信失败，请稍后重试");
        } catch (Exception e) {
            logger.error("Unexpected error sending password reset SMS to mobile: {}", mobile, e);
            throw new RuntimeException("发送短信失败，请稍后重试");
        }
    }
}
