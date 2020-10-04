import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.util.calendar.BaseCalendar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Parser {

    private static Document getPage() throws IOException {

        String url = "https://vk.com/search?c%5Bgroup%5D=90950972&c%5Bname%5D=1&c%5Bonline%5D=1&c%5Bper_page%5D=40&c%5Bphoto%5D=1&c%5Bsection%5D=people";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }
    private static double getN( Element a) throws IOException {
        String b = a.text();
        b = b.substring( b.indexOf(" "), b.lastIndexOf(" "));
        b=b.trim();
        int N = Integer.parseInt( b );
        return N;
    }
   /* private static void getPeepls (Element a) {
        String b =a.text();
        System.out.println(b);
        Pattern pattern= Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(b);
        System.out.println(matcher);
        System.out.println();
    }
*/

    public static void main(String[] args) throws IOException {


         while (true) {
             label1:
             {
                 Calendar call= Calendar.getInstance();
                 Date today = call.getTime();
                 int moon = today.getMonth();
                 int d = today.getDate();
                 int h = today.getHours();
                 int m = today.getMinutes();

                 //System.out.println(today.toString());
                 System.out.println(h+":"+m);

                 if (h < 8) {
                     long l = ((7 - h) * 60 + (63 - m)) * 60000;
                     try {
                         Thread.sleep(l); // Замораживает весь поток на 10 секунд
                         break label1;
                     } catch (Exception e) {
                         System.out.println("Получили исключение по времени!");
                     }

                 }


                 Document page = getPage();
                 Elements span = page.select("span");
                 Element h2 = span.get(1);
                 double N = getN(h2);


                 System.out.println(h2);

                 FileInputStream fis = new FileInputStream("C:/Users/Lenovo/IdeaProjects/traffic/ststistica.xls");
                 Workbook wb = new HSSFWorkbook(fis);
                 fis.close();


                 Sheet sh1 = wb.getSheetAt(0);
                 Row r1 = sh1.getRow(d);
                 System.out.println("Строка " + d);

                try {

                    Cell cell = r1.createCell(((h - 8) * 4) + (m / 15)+1);
                    cell.setCellValue(N);
                    System.out.println( "Значение " + N + " записано в " + (((h - 8) * 4) + (m / 15)+1) + " столбик" );
                } catch (Exception e) {
                    System.out.println("Не удалось создать ячейку");
                }


                 FileOutputStream fos = new FileOutputStream("C:/Users/Lenovo/IdeaProjects/traffic/ststistica.xls");
                 wb.write(fos);
                 fos.close();
                 System.out.println("Таблица сохранена");

                 try {
                     Thread.sleep(900000-2500); // Замораживает весь поток на
                 } catch (Exception e) {
                     System.out.println("Получили исключение по времени!");
                 }
             }
         }






    }
}
