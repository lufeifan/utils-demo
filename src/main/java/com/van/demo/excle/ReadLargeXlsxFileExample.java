package com.van.demo.excle;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.eventusermodel.XSSFReader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class ReadLargeXlsxFileExample {
    private static final String FILE_NAME = "./SMCP 会员信息 OCRM 全量人群.xlsx";

    public static void main(String[] args) throws Exception {
        InputStream inputStream = Files.newInputStream(new File(FILE_NAME).toPath());
        XSSFReader reader = new XSSFReader(OPCPackage.open(FILE_NAME));
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096 * 2)
                .open(inputStream);
        for (Sheet sheet : workbook) {
            System.out.println(sheet.getSheetName());
        }
        inputStream.close();
        workbook.close();
    }
}
