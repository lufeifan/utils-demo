package com.van.demo.excle;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteExcelDemo {

    public static void main(String[] args) throws IOException {
        // 创建 Excel 文件
        Workbook workbook = WorkbookFactory.create(true);
        Sheet sheet = workbook.createSheet("Sheet1");

        // 写入数据
        Row row1 = sheet.createRow(0);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue("Hello");

        Row row2 = sheet.createRow(1);
        Cell cell2 = row2.createCell(0);
        cell2.setCellValue("World");

        // 保存文件
        File file = new File("./file.xlsx");
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);

        // 关闭工作簿和输出流
        workbook.close();
        outputStream.close();
    }

}
