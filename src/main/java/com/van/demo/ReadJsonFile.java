package com.van.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONReader;
import lombok.Data;
import lombok.SneakyThrows;
import okhttp3.*;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class ReadJsonFile {

    public static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .build();

    @SneakyThrows
    public static void main(String[] args) {
        File file = new File("KUU-BIcB-O7-ndPQyWWD.json");
        List<Event> memberIds = new ArrayList<>();
        try (JSONReader reader = new JSONReader(new FileReader(file))) {
            JSONArray array = JSON.parseArray(reader.readString());
            for (Object o : array) {
                Body body = JSON.parseObject(JSON.toJSONString(o), Body.class);
                Event event = JSON.parseObject(body.getBody(), Event.class);
                memberIds.add(event);
            }
        }
        Map<String, Set<Event>> collect = memberIds.stream().collect(Collectors.groupingBy(Event::getAccountId, Collectors.toSet()));
        for (Map.Entry<String, Set<Event>> entry : collect.entrySet()) {
            List<String> ids = entry.getValue().stream().map(Event::getMemberId).collect(Collectors.toList());
            System.out.println(entry.getKey() + ":" + ids.size());
            for (String memberId : ids) {
//                reCalcMemberLevel(entry.getKey(), memberId);
                if ("62f321f3bfa1726ebe1dc262".equals(entry.getKey())) {
                    System.out.println(memberId);
                }
            }
        }
    }

    private static final String CP_TOKEN = "";
    private static final String MAJE_TOKEN = "";
    private static final String SANDRO_TOKEN = "";

    public static void reCalcMemberLevel(String accountId, String memberId) {
        try {
            Headers headers = null;
            switch (accountId) {
                case "62f3214fd9e7c238ee672ed2":
                    headers = Headers.of().newBuilder().add("x-access-token", CP_TOKEN)
                            .add("Content-Type", "application/json")
                            .build();
                    break;
                case "62f321c18ddb0f2afe098972":
                    headers = Headers.of().newBuilder().add("x-access-token", SANDRO_TOKEN)
                            .add("Content-Type", "application/json")
                            .build();
                    break;
                case "62f321f3bfa1726ebe1dc262":
                    headers = Headers.of().newBuilder().add("x-access-token", MAJE_TOKEN)
                            .add("Content-Type", "application/json")
                            .build();
                    break;
                default:
            }
            if (Objects.isNull(headers)) {
                return;
            }
            String url = "https://businessapi-clbwaf-prod.smcpgroup.cn/apps/smcp/levels/reCalcMemberLevel/" + memberId;
            String json = "";
            MediaType mediaType = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(json, mediaType);
            Request request = new Request.Builder().url(url).headers(headers).post(body).build();
            try (Response response = CLIENT.newCall(request).execute()) {
                System.out.println(response.isSuccessful());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class Body {
        private String body;

    }

    @Data
    public static class Event {
        private String memberId;
        private String accountId;
    }
}
