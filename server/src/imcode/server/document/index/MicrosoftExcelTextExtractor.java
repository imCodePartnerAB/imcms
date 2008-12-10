package imcode.server.document.index;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

class MicrosoftExcelTextExtractor implements StreamTextsExtractor {
    public String[] extractTexts(InputStream in) throws IOException {
        List texts = new ArrayList();
        HSSFWorkbook workbook = new HSSFWorkbook(in);
        int sheetCount = workbook.getNumberOfSheets();
        for ( int i = 0; i < sheetCount; ++i ) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            Iterator rowIterator = sheet.rowIterator();
            while ( rowIterator.hasNext() ) {
                HSSFRow row = (HSSFRow) rowIterator.next();
                Iterator cellIterator = row.cellIterator();
                while ( cellIterator.hasNext() ) {
                    HSSFCell cell = (HSSFCell) cellIterator.next();
                    int cellType = cell.getCellType();
                    if ( cellType == HSSFCell.CELL_TYPE_STRING ) {
                        texts.add(cell.getStringCellValue());
                    }
                }
            }
        }
        return (String[]) texts.toArray(new String[texts.size()]);
    }
}
