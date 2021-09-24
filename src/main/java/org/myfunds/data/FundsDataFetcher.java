package org.myfunds.data;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FundsDataFetcher {
    //        full, take 137s
//   private static final String url = "http://fund.eastmoney.com/data/fundranking.html#tall;c0;r;s6yzf;pn10000;ddesc;qsd20200613;qed20210613;qdii;zq;gg;gzbd;gzfs;bbzt;sfbb";
   private static final String url = "http://fund.eastmoney.com/data/fundranking.html#tall;c0;r;s6yzf;pn10000;ddesc;qsdSTART_DATE;qedEND_DATE;qdii;zq;gg;gzbd;gzfs;bbzt;sfbb";
    // 50 records
//    private static final String url = "http://fund.eastmoney.com/data/fundranking.html";
    private static WebClient webClient;

    private static final String QUERY_START_DATE_PLACEHOLDER = "START_DATE";
    private static final String QUERY_END_DATE_PLACEHOLDER = "END_DATE";



    public static List<String> fetch(int customDays) throws IOException {
        setupWebClient();

        String fetchUrl = constructUrl(customDays);
        System.out.println("url:" + fetchUrl);

        WebRequest webRequest = new WebRequest(new URL(constructUrl(customDays)));
        webRequest.setCharset(StandardCharsets.UTF_8);

        System.out.println("time 1: " + System.currentTimeMillis());
        HtmlPage page = webClient.getPage(webRequest);
        System.out.println("time 2: " + System.currentTimeMillis());

        List<HtmlTableBody> tableBodies = page.getByXPath("//*[@id=\"dbtable\"]/tbody");

        System.out.println("body size:" + tableBodies.size());

        HtmlTableBody tb = tableBodies.get(0);
        System.out.println("tb count: " + tb.getRows().size());

        List<HtmlTableRow> rows = tb.getRows();

        List<String> datas = new ArrayList<>(9000);
        rows.forEach(row -> {
            List<HtmlTableCell> cells = row.getCells();
            cells = cells.subList(2, cells.size() - 1);

            String data = cells.stream().map(FundsDataFetcher::getDisplayText).collect(Collectors.joining(","));
            datas.add(data);
        });

        return datas;
    }

    private static String constructUrl(int customDays) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();
        //if today is 20210924, customDays=7,   startDate=20210916, endDate=20210923
        String endDate = dtf.format(now.minusDays(1));
        String startDate = dtf.format(now.minusDays(customDays + 1));

        return url.replace(QUERY_START_DATE_PLACEHOLDER, startDate).replace(QUERY_END_DATE_PLACEHOLDER, endDate);
    }
    
    private static String getDisplayText(HtmlTableCell cell) {
        String defaultText = cell.asNormalizedText();

        DomNodeList<HtmlElement> anchorList = cell.getElementsByTagName("a");
        if(anchorList.isEmpty()) {
            return defaultText;
        }

        String title = anchorList.get(0).getAttribute("title");
        if (!DomElement.ATTRIBUTE_NOT_DEFINED.equals(title) && !DomElement.ATTRIBUTE_VALUE_EMPTY.equals(title)) {
            //the full name of the fund is stored in the title attribute
            //<a href="http://fund.eastmoney.com/004040.html" title="金鹰医疗健康产业A">金鹰医疗健康</a>
//            System.out.println("fetch:" + title);
            return title;
        }

        return defaultText;
    }

    public static void save(List<String> dataLines) throws IOException {
        File csvOutputFile = Paths.get(FundsDataMgr.DATA_STORE_PATH, FundsDataMgr.getCurrentDate() + ".csv").toFile();
        if (csvOutputFile.exists()) {
            csvOutputFile.delete();
        }

        csvOutputFile.getParentFile().mkdirs();
        csvOutputFile.createNewFile();
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.forEach(pw::println);
        }
    }

    public static void setupWebClient(){
        webClient = new WebClient(BrowserVersion.CHROME);

//        webClient.setJavaScriptTimeout(120_0000);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//        webClient.waitForBackgroundJavaScript(120_000);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//        webClient.getOptions().setTimeout(120_000);
        webClient.getCookieManager().setCookiesEnabled(false);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(constructUrl(30));
//        List<String> datas = fetch(14);
//        save(datas);
    }
}
