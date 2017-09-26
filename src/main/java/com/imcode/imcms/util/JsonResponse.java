package com.imcode.imcms.util;

import java.io.Serializable;
import java.net.HttpURLConnection;

public class JsonResponse implements Serializable {

    private static final long serialVersionUID = 5943589664651898417L;
    public int code;
    public String message;

    private JsonResponse(int statusCode) {
        this.code = statusCode;
    }

    private JsonResponse(int statusCode, String message) {
        this.code = statusCode;
        this.message = message;
    }

    public static JsonResponse ok() {
        return new JsonResponse(HttpURLConnection.HTTP_OK);
    }

    public static JsonResponse error(String message) {
        return new JsonResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, message);
    }
}
