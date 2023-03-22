package com.van.demo.utils;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class HttpUtils {

    public static final String DEFAULT_MEDIA_TYPE = "application/json; charset=utf-8";

    public static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .addInterceptor(new AccessTokenInterceptor())
            .build();

    private static class AccessTokenInterceptor implements Interceptor {

        private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NjQxNTk1ODYsImV4cCI6NDgxNzc1OTU4NiwiaXNzIjoicG9ydGFsIiwiYXVkIjoib3BlbkFQSSIsImFpZCI6IjYzMjQxNjk4MmEzMGIxNzU5ZjU2MjZlMiIsInN1YiI6ImFwcDo2MzMxMGY2MmNkM2FmODY2ODg2YWVkYjIiLCJ1bmlxIjoiMzU0NDY3NTk0NjMzMTBmNjJiNDU1YjYuMDI3NDc4NzQifQ.7ikc2pFYYro7GdRGjPAvCVRCTPELkRul5I48Id1UJng";

        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Headers headers = Headers.of().newBuilder().add("x-access-token", TOKEN)
                    .add("Content-Type", "application/json")
                    .build();
            return chain.proceed(chain.request().newBuilder().headers(headers).build());
        }
    }

    private static class LogInterceptor implements Interceptor {

        @SuppressWarnings("all")
        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request().newBuilder().build();
            System.out.println(request.body());
            Response response = chain.proceed(request);
            if (Objects.nonNull(response.body())) {
                System.out.println(JSON.parseObject(response.body().string(), String.class));
            }
            return response;
        }
    }
}
