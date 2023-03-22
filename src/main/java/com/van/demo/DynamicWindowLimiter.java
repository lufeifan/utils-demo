package com.van.demo;

import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.Queue;

public class DynamicWindowLimiter {
    private final int limit;
    private final int windowSize;
    private final Queue<Long> timestamps;

    public DynamicWindowLimiter(int limit, int windowSize) {
        this.limit = limit;
        this.windowSize = windowSize;
        this.timestamps = new LinkedList<>();
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        while (!timestamps.isEmpty() && timestamps.peek() < now - windowSize) {
            timestamps.poll();
        }
        if (timestamps.size() < limit) {
            timestamps.offer(now);
            return true;
        }
        return false;
    }

    @SneakyThrows
    public static void main(String[] args) {
        DynamicWindowLimiter limiter = new DynamicWindowLimiter(5, 2);
        for (int i = 0; i < 2000; i++) {
            System.out.println(limiter.allowRequest());
        }
    }
}
