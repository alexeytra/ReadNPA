package classes;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.List;

public class WriteExcel {
    private static HSSFCellStyle createStyleForTitle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillBackgroundColor(HSSFColor.LAVENDER.index);
        return style;
    }

    private static HSSFCellStyle createStyleForCell(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    public static void WriteToExcel(List<NPA> l) throws IOException {

        File file = new File("NPA.xls");
        //file.getParentFile().mkdirs();
        HSSFWorkbook workbook = null;

        if (file.exists()){
            //Запись если файл существует
            FileInputStream inputStream = new FileInputStream(file);
            workbook = new HSSFWorkbook(inputStream);
            write(workbook, 1, l, file);
            //
            inputStream.close();
        } else {
            workbook = new HSSFWorkbook();
            write(workbook, 1, l, file);
            //
        }

    }

    public static void write(HSSFWorkbook workbook, int choose, List<NPA> l, File file){
        FileOutputStream outFile = null;
        HSSFSheet sheet;
        switch (choose) {
            case 1: sheet = workbook.createSheet("NPA"); break;
            default:  sheet = workbook.createSheet("error");
        }
        int rownum = 0;
        Cell cell;
        Row row;
        HSSFCellStyle style = createStyleForTitle(workbook);
        HSSFCellStyle styleforSell = createStyleForCell(workbook);

        row = sheet.createRow(rownum);

        for (NPA p : l) {
            rownum++;
            row = sheet.createRow(rownum);
            //
            cell = row.createCell(0, CellType.STRING);
            cell.setCellStyle(styleforSell);
            cell.setCellValue(p.getLevel());
            //
            cell = row.createCell(1, CellType.STRING);
            cell.setCellStyle(styleforSell);
            cell.setCellValue(p.getNameElem());
            //
            cell = row.createCell(2, CellType.STRING);
            cell.setCellStyle(styleforSell);
            cell.setCellValue(p.getNumber());
            //
            cell = row.createCell(3, CellType.STRING);
            cell.setCellStyle(styleforSell);
            cell.setCellValue(p.getName());
            //
            cell = row.createCell(4, CellType.STRING);
            cell.setCellStyle(styleforSell);
            cell.setCellValue(p.getContent());
            //
            cell = row.createCell(5, CellType.STRING);
            cell.setCellStyle(styleforSell);
            cell.setCellValue(p.getSequence());
        }
        //Запись результата
        try {
            outFile = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            workbook.write(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Все ok " + file.getAbsolutePath());
    }
}
