package com.bankapp.common.feign;

import com.bankapp.common.enums.ErrorCode;
import com.bankapp.common.exception.*;
import com.bankapp.common.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        final ObjectMapper objectMapper = new ObjectMapper();

        String message = "Service error";
        ErrorCode errorCode = ErrorCode.SERVICE_ERROR;

        try {
            if(response.body() != null){
                InputStream inputStream = response.body().asInputStream();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                ErrorResponse errorResponse = objectMapper.readValue(body, ErrorResponse.class);

                if (errorResponse.errorCode() != null){
                    errorCode = errorResponse.errorCode();
                }
                if (errorResponse.message() != null){
                    message = errorResponse.message();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return switch (errorCode){
            case INVALID_OTP -> new InvalidOtpException(message);
            case EMAIL_ALREADY_EXISTS -> new EmailAlreadyExistsException(message);
            case OTP_GENERATE_LIMIT_EXCEEDED -> new OtpGenerateLimitExceededException(message);
            case OTP_VALIDATE_LIMIT_EXCEEDED -> new OtpValidateLimitExceededException(message);
            case BRANCH_NOT_FOUND -> new BranchNotFoundException(message);
            default -> new ServiceException(message);
        };
    }
}
