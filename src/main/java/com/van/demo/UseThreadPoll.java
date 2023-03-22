package com.van.demo;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UseThreadPoll {
    private static final int CORE_POOL_SIZE = 30;
    private static final long KEEP_ALIVE_TIME = 120;
    public static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            CORE_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static void main(String[] args) {
        List<String> list = Arrays.asList("1", "2", "3", "4");
        CompletableFuture.allOf(list.stream()
                .map(number -> CompletableFuture.runAsync(() -> test(number), THREAD_POOL))
                .toArray(CompletableFuture<?>[]::new)).join();
        System.out.println("End1");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            futures.add(CompletableFuture.runAsync(() -> test("1")));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();
        System.out.println("End2");
        THREAD_POOL.shutdown();
    }

    @SneakyThrows
    public static void test(String number) {
        System.out.println(number);
        Thread.sleep(1000);
    }
}
