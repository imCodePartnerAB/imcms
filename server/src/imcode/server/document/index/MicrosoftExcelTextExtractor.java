package imcode.server.document.index;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class MicrosoftExcelTextExtractor implements StreamTextsExtractor {
    public String[] extractTexts(InputStream in) throws IOException {
        List texts = new ArrayList();
        HSSFWorkbook workbook = new HSSFWorkbook(in);
        int sheetCount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; ++i) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            Iterator rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                HSSFRow row = (HSSFRow) rowIterator.next();
                Iterator cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
	                HSSFCell cell = (HSSFCell) cellIterator.next();
	                if (cell.getCellType() == CellType.STRING) {
                        texts.add(cell.getStringCellValue());
                    }
                }
            }
        }
        return (String[]) texts.toArray(new String[texts.size()]);
    }
}
