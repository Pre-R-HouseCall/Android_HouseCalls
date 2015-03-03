package com.prer;

import com.loopj.android.http.*;

public final class RestClient {
    private static final String BASE_URL = "http://54.191.98.90/api/1.0/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(android.content.Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(android.content.Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), params, responseHandler);
    }

    public static String buildUrl() {
        //this method will be used to build the relative URL to be used by the get method
        return null;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
