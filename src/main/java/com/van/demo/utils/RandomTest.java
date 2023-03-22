package com.van.demo.utils;


import com.github.javafaker.Faker;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class RandomTest {

    @SneakyThrows
    public static void main(String[] args) {

        User user = new User();
        for (int i = 0; i < 20; i++) {
            System.out.println(rand1(User.class));
        }
    }

    @SneakyThrows
    public static <T> T rand1(Class<T> clazz) {
        Faker faker = new Faker();
        T obj = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType() == String.class && faker.random().nextBoolean()) {
                field.set(obj, faker.name().fullName());
            }
            if (field.getType() == Boolean.class && faker.random().nextBoolean()) {
                field.set(obj, faker.random().nextBoolean());
            }
            if (field.getType() == Integer.class && faker.random().nextBoolean()) {
                field.set(obj, faker.random().nextInt(1, 100));
            }
            if (field.getType() == Long.class && faker.random().nextBoolean()) {
                field.set(obj, faker.random().nextLong());
            }
            if (field.getType() == Double.class && faker.random().nextBoolean()) {
                field.set(obj, faker.random().nextDouble());
            }
        }
        return obj;
    }

    @Data
    public static class User {
        private String name;
        private Boolean isMember;
        private Integer age;
        private Double d;
        private Long a;
    }
}
