import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Slf4j
public class App {
    @Setter
    private StatFileHandler statFileHandler;

    public static void main(String[] args) throws IOException, URISyntaxException {
        //todo parse args
        URL urlToParse = null;
        StatFileHandler statFileHandler = null;

        if (args.length == 0) {
            Properties defaults = new Properties();
            InputStream inputStream = App.class.getClassLoader().getResourceAsStream("default.properties");
            defaults.load(inputStream);
            String protocol = defaults.getProperty("default.url.protocol");
            String host = defaults.getProperty("default.url.host");
            String path = defaults.getProperty("default.url.path");
            String searchQuery = defaults.getProperty("default.url.searchQuery");
            urlToParse = new URI(protocol, host, path, searchQuery).toURL();
            statFileHandler = new StatFileHandler(defaults.getProperty("default.files.statistic"));
        }
        App app = new App();

        app.setStatFileHandler(statFileHandler);
        app.run(urlToParse);
    }

    private void run(URL urlToParse) {
        //todo авторизация точно была бы не лишней
        log.info("Парсим страницу: {}", urlToParse);
        final Timer timer = new Timer();

        TimerTask task = new ParseTask(urlToParse, statFileHandler, timer);
        Calendar firstRunTime = Calendar.getInstance();
        int h = firstRunTime.get(Calendar.HOUR_OF_DAY);
        int m = firstRunTime.get(Calendar.MINUTE);

        log.info("Сейчас {}", firstRunTime.getTime());
        if (h < 8) {
            firstRunTime.set(Calendar.HOUR_OF_DAY, 8);
        }
        // измерения проводятся каждую четверть часа.
        // Если с начала четверти прошло более 5 минут, ждем следующю четверть
        if (m % 15 > 5) {
            firstRunTime.add(Calendar.MINUTE, (16 - m % 15));
        } else {
            // иначе добовляем 10 секунд
            firstRunTime.add(Calendar.SECOND, 10);
        }
        log.info("Время первого опроса {}", firstRunTime.getTime());

        timer.scheduleAtFixedRate(task, firstRunTime.getTime(), TimeUnit.MINUTES.toMillis(15));
    }

}
