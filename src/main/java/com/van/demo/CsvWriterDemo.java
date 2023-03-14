package com.van.demo;

import lombok.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CsvWriterDemo {

    private static final int CORE_POOL_SIZE = 50;
    private static final long KEEP_ALIVE_TIME = 120;
    public static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            CORE_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(500),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @SneakyThrows
    public static void main(String[] args) {
        File file = new File("./test.csv");
        List<String> headers = Arrays.asList("用户ID", "年龄", "姓名", "地址", "性别");
        CSVFormat csvFormat = CSVFormat.Builder.create().setHeader(headers.toArray(new String[0])).build();
        FileWriter fileWriter = new FileWriter(file);
        try (CSVPrinter printer = new CSVPrinter(fileWriter, csvFormat)) {
            for (int j = 0; j < 300; j++) {
                List<Member> members = generateData();
                CompletableFuture.allOf(members.stream()
                        .map(number -> CompletableFuture.runAsync(() -> process(number, printer), THREAD_POOL))
                        .toArray(CompletableFuture<?>[]::new)).join();
                System.out.println(j);
            }
            fileWriter.flush();
            THREAD_POOL.shutdown();
        } catch (Exception e) {

        }
    }

    // 在多线程的场景下保证线程的安全与防止数据丢失
    public synchronized static void process(Member member, CSVPrinter printer) {
        try {
            printer.printRecord(member.formatLine());
            printer.flush();
            System.out.println(member);
        } catch (Exception e) {

        }
    }

    public static List<Member> generateData() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            members.add(Member.builder()
                    .memberId(UUID.randomUUID().toString())
                    .age(i)
                    .sex("Nan")
                    .address("")
                    .build());
        }
        return members;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Member {
        private String memberId;
        private String name;
        private String address;
        private String sex;
        private Integer age;

        public Object[] formatLine() {
            return new Object[]{
                    this.memberId,
                    this.age,
                    this.name,
                    this.address,
                    this.sex
            };
        }
    }
}
