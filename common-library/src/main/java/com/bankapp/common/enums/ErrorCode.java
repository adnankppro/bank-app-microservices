package com.bankapp.common.enums;

public enum ErrorCode {

    //OTP
    OTP_GENERATE_LIMIT_EXCEEDED,
    OTP_VALIDATE_LIMIT_EXCEEDED,
    INVALID_OTP,

    //Auth
    EMAIL_ALREADY_EXISTS,
    INCORRECT_PASSWORD,
    UNAUTHORIZED_REQUEST,
    ID_NOT_FOUND,
    DATA_NOT_FOUND,
    INVALID_REFRESH_TOKEN,

    //profile
    PROFILE_ALREADY_EXISTS,
    PROFILE_NOT_FOUND,

    //Service
    SERVICE_ERROR,

    //branch
    BRANCH_NOT_FOUND
}