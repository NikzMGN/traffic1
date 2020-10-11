import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Parser {

    public Document getDocument(URL urlToParse) throws IOException {
        return Jsoup.parse(urlToParse, (int) SECONDS.toMillis(3));
    }

}
