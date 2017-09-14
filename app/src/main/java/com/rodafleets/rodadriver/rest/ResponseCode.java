package com.rodafleets.rodadriver.rest;

public final class ResponseCode {

    //http status codes
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int DUPLICATE = 409;
    public static  final int INTERNAL_SERVER_ERROR = 500;

    //api specific error codes
    public static final int SENDING_OTP_FAILED = 101;
    public static final int PHONE_NOT_VERIFIED = 102;
    public static final int SESSION_ID_REQUIRED_FOR_OTP_VERIFICATION = 103;
    public static final int INVALID_OTP = 104;
    public static final int INVALID_CREDENTIALS = 105;
    public static final int INVALID_DRIVER_ID = 106;
    public static final int NO_RESULTS = 107;



    public static final int DUPLICATE_ENTRY = 111;
}
