import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Parser {

    private static Document getPage() throws IOException {

        String url = "https://vk.com/search?c%5Bgroup%5D=90950972&c%5Bname%5D=1&c%5Bonline%5D=1&c%5Bper_page%5D=40&c%5Bphoto%5D=1&c%5Bsection%5D=people";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }
    private static double getN( Element a) {
        String b = a.text();
        b = b.substring( b.indexOf(" "), b.lastIndexOf(" "));
        b=b.trim();
        int N = Integer.parseInt( b );
        return N;
    }


    public static void main(String[] args) throws IOException {

        final Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                Calendar date= Calendar.getInstance();
                int d = date.get(Calendar.DAY_OF_MONTH);
                int h = date.get(Calendar.HOUR_OF_DAY);
                int m = date.get(Calendar.MINUTE);

                if (h<8) {
                    timer.cancel();
                } else {

                    try {


                        Document page = getPage();
                        Elements span = page.select("span");
                        Element h2 = span.get(1);
                        System.out.println(h2);
                        double N = getN(h2);

                        FileInputStream fis = new FileInputStream("ststistica.xls");
                        Workbook wb = new HSSFWorkbook(fis);
                        fis.close();


                        Sheet sh1 = wb.getSheetAt(0);
                        Row r1 = sh1.getRow(d);
                        System.out.println("Строка " + d);

                        try {

                            Cell cell = r1.createCell(((h - 8) * 4) + (m / 15) + 1);
                            cell.setCellValue(N);
                            System.out.println("Значение " + N + " записано в " + (((h - 8) * 4) + (m / 15) + 1) + " столбик");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Не удалось создать ячейку");
                        }


                        FileOutputStream fos = new FileOutputStream("C:/Users/Lenovo/IdeaProjects/traffic/ststistica.xls");
                        wb.write(fos);
                        fos.close();
                        System.out.println("Таблица сохранена");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Что-то пошло не так");
                        timer.cancel();
                    }
                }

            }
        };

        Calendar call= Calendar.getInstance();
        int h = call.get(Calendar.HOUR_OF_DAY);
        int m = call.get(Calendar.MINUTE);
        System.out.println("Сейчас "+call.getTime().toString());

        if (h<8) {call.set(Calendar.HOUR_OF_DAY, 8);
        }
        if (m%15>5) {call.add(Calendar.MINUTE, (16-m%15));
        } else { call.add(Calendar.SECOND, 10);
        }

        System.out.println("Время первого опроса"+call.getTime().toString());

        timer.scheduleAtFixedRate(task, call.getTime(), 900000);



    }
}
