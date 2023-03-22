package com.van.demo;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Test {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");

    public static void main(String[] args) {
        TestBean testBean = new TestBean();
        testBean.setBeanName("name");
        testBean.setT(999);
        System.out.println(getNonNullValues(testBean));
        
    }

    @Data
    public static class TestBean {

        private String beanName;
        private String beanName1 = "";
        private Integer t;
        private Boolean b;
        private Boolean c;
        private Long l;
    }

    @SneakyThrows
    public static Map<String, Object> getNonNullValues(Object source) {
        Map<String, Object> nonNullValues = new HashMap<>();
        Field[] fields = source.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(System.out::println);
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);
            if (ObjectUtils.isNotEmpty(value)) {
                nonNullValues.put(field.getName(), value);
            }
        }
        return nonNullValues;
    }
}
