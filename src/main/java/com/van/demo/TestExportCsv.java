package com.van.demo;

import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.bson.BsonNull;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Projections.*;

public class TestExportCsv {

    private static final int CORE_POOL_SIZE = 100;
    private static final long KEEP_ALIVE_TIME = 120;
    public static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            CORE_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2000),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @SneakyThrows
    public static void main(String[] args) {
        TestExportCsv testExportCsv = new TestExportCsv();
        File file = new File("./test.csv");
        File file1 = new File("./test1.csv");
        CSVFormat csvFormat = CSVFormat.RFC4180;
        FileWriter fileWriter = new FileWriter(file);
        FileWriter fileWriter1 = new FileWriter(file1);
        try (MongoClient mongoClient = MongoClients.create("mongodb://192.168.1.103:27017");
             CSVPrinter printer = new CSVPrinter(fileWriter, csvFormat);
             CSVPrinter printer1 = new CSVPrinter(fileWriter1, csvFormat)
        ) {
            //选择一个数据库
            MongoDatabase database = mongoClient.getDatabase("local");
            MongoCollection<Document> collection = database.getCollection("smcpOrderStats");
            AggregateIterable<Document> membersResult2 = collection.aggregate(
                    Arrays.asList(new Document("$group",
                                    new Document("_id", "$memberId")),
                            new Document("$group",
                                    new Document("_id",
                                            new BsonNull())
                                            .append("count",
                                                    new Document("$sum", 1L))))
            );
            long total = 0;
            for (Document document : membersResult2) {
                System.out.println(membersResult2);
                total = (long) document.get("count");
            }
            int skip = 0;
            int batchSize = 2000;
            long page = (long) Math.ceil(total * 1.0 / batchSize);
            int aaa = 0;
            while (skip <= page) {
                AggregateIterable<Document> membersResult = collection.aggregate(Arrays.asList(
                                match(new Document("accountId", new ObjectId("62f321f3bfa1726ebe1dc262"))),
                                Aggregates.project(fields(include("memberId"), excludeId())),
                                new Document("$group", new Document("_id", "$memberId").append("memberId", new Document("$first", "$memberId"))),
                                Aggregates.sort(ascending("memberId")),
                                Aggregates.skip(skip),
                                Aggregates.limit(batchSize)
                        )
                );
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (Document document : membersResult) {
                    Object memberId = document.get("memberId");
                    futures.add(CompletableFuture.runAsync(() -> testExportCsv.test(memberId, printer, printer1, collection), THREAD_POOL));
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                skip++;
                aaa++;
                System.out.println(skip);
            }

            System.out.println(total);
            printer.flush();
            printer1.flush();
            fileWriter.close();
            fileWriter1.close();
            System.out.println(aaa);
            THREAD_POOL.shutdown();
            System.out.println("End");
        }
    }

    @SneakyThrows
    public void test(Object memberId, CSVPrinter printer, CSVPrinter printer1, MongoCollection<Document> collection) {
        printer1.printRecord(memberId);
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                match(new Document("accountId", new ObjectId("62f321f3bfa1726ebe1dc262"))
                        .append("memberId", memberId)),
                Aggregates.project(fields(include("memberId", "orderIds", "regularAmount", "specialAmount", "actualAmount"), excludeId())),
                new Document("$group", new Document("_id", "$memberId")
                        .append("memberId", new Document("$first", "$memberId"))
                        .append("orderIds", new Document("$push", "$orderIds"))
                        .append("allRegularAmount", new Document("$sum", "$regularAmount"))
                        .append("allSpecialAmount", new Document("$sum", "$specialAmount"))
                        .append("allAmount", new Document("$sum", "$actualAmount"))
                ),
                unwind("$orderIds"),
                unwind("$orderIds"),
                new Document("$group", new Document("_id", "$memberId")
                        .append("memberId", new Document("$first", "$memberId"))
                        .append("orderIds", new Document("$addToSet", "$orderIds"))
                        .append("allRegularAmount", new Document("$first", "$allRegularAmount"))
                        .append("allSpecialAmount", new Document("$first", "$allSpecialAmount"))
                        .append("allAmount", new Document("$first", "$allAmount"))
                )
        ));
        for (Document doc : result) {
//            synchronized (this) {
            printer.printRecord(doc.toJson());
        }
        System.out.println(memberId);
    }

    // 379488
    // 379498
}

