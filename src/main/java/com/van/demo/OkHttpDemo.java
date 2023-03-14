package com.van.demo;

import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public class OkHttpDemo {
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String TOKEN = "";
    public static final Headers HEADERS = new Headers.Builder().add("x-access-token", TOKEN).build();
    public static final String BASE_URL = "";

    public static void main(String[] args) {

    }

    public static String getMemberByOpenId(String openId) {
        String url = BASE_URL + "/v2/members/" + openId;
        Request request = new Request.Builder().url(url).get().headers(HEADERS).build();
        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                System.out.println("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());

        }
        return null;
    }

    public static String post() {
        String url = BASE_URL + "/v2/members/";
        OkHttpClient client = new OkHttpClient();
        String json = "";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).headers(HEADERS).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (Exception e) {
            //
        }
        return null;
    }

    public static String put() {
        String url = BASE_URL + "/v2/members/";
        String json = "";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).headers(HEADERS).put(body).build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (Exception e) {
            //
        }
        return null;
    }

    public static String delete() {
        String url = BASE_URL + "/v2/members/";
        Request request = new Request.Builder().url(url).headers(HEADERS).delete().build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (Exception e) {
            //
        }
        return null;
    }
}
