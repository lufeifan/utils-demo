package com.van.demo.excle;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadLargeExcelDemo {

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

    public static void main(String[] args) {

        String fileName = "SMCP 会员信息 OCRM 全量人群.xlsx";
        AtomicInteger number = new AtomicInteger(0);
        int batchSize = 500;
        List<DemoData> list = new ArrayList<>();
        List<DemoData> dataList = new CopyOnWriteArrayList<>();

        ExcelWriter excelWriter = EasyExcel.write(new File("./test1.xlsx"), DemoData.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();

        AnalysisEventListener<DemoData> listener = new AnalysisEventListener<DemoData>() {

            @Override
            public void invoke(DemoData demoData, AnalysisContext analysisContext) {
                list.add(demoData);
                Integer totalCount = analysisContext.readSheetHolder().getApproximateTotalRowNumber();
                Integer rowIndex = analysisContext.readRowHolder().getRowIndex();
                System.out.println(rowIndex);

                if (totalCount - 1 == rowIndex) {
//                    TODO
                    System.out.println("--2000--");
                    CompletableFuture.allOf(list.stream()
                            .map(number -> CompletableFuture
                                    .supplyAsync(() -> getMemberLevel(number), THREAD_POOL)
                                    .thenAccept(dataList::add))
                            .toArray(CompletableFuture<?>[]::new)).join();
                    excelWriter.write(dataList, writeSheet);
                    dataList.clear();
                    list.clear();
                }
                if (list.size() % batchSize == 0) {
                    CompletableFuture.allOf(list.stream()
                            .map(number -> CompletableFuture
                                    .supplyAsync(() -> getMemberLevel(number), THREAD_POOL)
                                    .thenAccept(dataList::add))
                            .toArray(CompletableFuture<?>[]::new)).join();
                    excelWriter.write(dataList, writeSheet);
                    dataList.clear();
                    list.clear();
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                Integer totalCount = analysisContext.readSheetHolder().getApproximateTotalRowNumber();
                System.out.println("总行数：" + totalCount);
            }
        };

        ExcelReader excelReader = EasyExcel.read(fileName, DemoData.class, listener)
                .headRowNumber(1)
                .autoCloseStream(true)
                .ignoreEmptyRow(true)
                .autoTrim(true).build();
        ReadSheet readSheet = EasyExcel.readSheet("SMCP").build();
        excelReader.read(readSheet);
        excelReader.close();
        System.out.println(number.get());
        excelWriter.finish();
        excelWriter.close();
    }

    @SneakyThrows
    public static DemoData getMemberLevel(DemoData demoData) {
//        Thread.sleep(50);
        System.out.println("123");
        return demoData;
    }

    @Data
    public static class DemoData {
        @ExcelProperty(value = "店铺")
        private String name;
        @ExcelProperty(value = "hub_uid")
        private String hubUid;
    }
}
