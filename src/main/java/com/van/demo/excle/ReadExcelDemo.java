package com.van.demo.excle;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.XSSFReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ReadExcelDemo {

    public static void main(String[] args) throws Exception {
        // 读取 Excel 文件
        File file = new File("./SMCP 会员信息 OCRM 全量人群.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);
        XSSFReader reader = new XSSFReader(OPCPackage.open(inputStream));
        XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();
        while (sheetIterator.hasNext()) {
            InputStream sheetStream = sheetIterator.next();
            String sheetName = sheetIterator.getSheetName();
            System.out.println(sheetName);
            // 处理该Sheet的数据
            sheetStream.close();
        }

        // 遍历行和单元格
//        for (Row row : sheet) {
//            for (Cell cell : row) {
//                System.out.print(cell.toString() + "\t");
//            }
//            System.out.println();
//            break;
//        }
//
//        // 关闭工作簿和输入流
//        workbook.close();
        inputStream.close();
    }

}
