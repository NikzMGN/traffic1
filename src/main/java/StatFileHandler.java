import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
public class StatFileHandler {

    private final File statFile;

    //todo читать не только из ресурсов
    public StatFileHandler(String statisticFileName) throws FileNotFoundException, URISyntaxException {
        URL statistic = getClass().getClassLoader().getResource(statisticFileName);
        if (statistic == null) {
            throw new FileNotFoundException(statisticFileName);
        }
        statFile = new File(statistic.toURI());
    }

    public Workbook getWorkBook() throws IOException {
        try (FileInputStream fis = new FileInputStream(statFile)) {
            return new HSSFWorkbook(fis);
        }
    }

    public void write(Workbook wb) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(statFile)) {
            wb.write(fos);
            log.info("Таблица сохранена");
        }

    }

}
