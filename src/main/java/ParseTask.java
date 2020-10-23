import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

@Slf4j
public class ParseTask extends TimerTask {
    private final URL urlToParse;
    private final Timer timer;
    private StatFileHandler statFileHandler;

    public ParseTask(URL urlToParse, StatFileHandler statFileHandler, Timer timer) {
        this.urlToParse = urlToParse;
        this.statFileHandler = statFileHandler;
        this.timer = timer;
    }

    @Override
    public void run() {
        Parser parser = new Parser();

        Calendar date = Calendar.getInstance();
        int d = date.get(Calendar.DAY_OF_MONTH);
        int h = date.get(Calendar.HOUR_OF_DAY);
        int m = date.get(Calendar.MINUTE);

        // если время мньше 8 утра, выходим из таймера
        if (h < 8) {
            timer.cancel();
        } else {
            Document page = null;
            try {
                page = parser.getDocument(urlToParse);
                Elements countElement = page.select("span.page_block_header_count");
                log.debug("{}", countElement);

                int count = parseInt(countElement.text());
                Workbook wb = statFileHandler.getWorkBook();

                Sheet sheet = wb.getSheetAt(0);
                Row row = sheet.getRow(d);
                log.info("Строка {}", d);

                try {
                    Cell cell = row.createCell(((h - 8) * 4) + (m / 15) + 1);
                    cell.setCellValue(count);
                    log.info("Значение {} записано в {} столбик", count, (((h - 8) * 4) + (m / 15) + 1));
                } catch (Exception e) {
                    log.error("Не удалось создать ячейку", e);
                }

                statFileHandler.write(wb);

            } catch (Exception e) {
                log.error("Что-то пошло не так ", e);
                if (page != null) {
                    log.debug("{}", page);
                }
                timer.cancel();
            }
        }
    }

}
