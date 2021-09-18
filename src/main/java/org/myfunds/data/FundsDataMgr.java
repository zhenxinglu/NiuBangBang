package org.myfunds.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FundsDataMgr {
    public static final String DATA_STORE_PATH = Paths.get(System.getProperty("user.home"), "myFunds").toString();
//    private static String dataStorePath = "f:\\tmp\\fundsData";
    private static List<Fund> funds = Collections.emptyList();


    public static boolean hasCurrentDateData() {
        String currentDate = getCurrentDate();
        return Paths.get(DATA_STORE_PATH, currentDate+ ".csv").toFile().exists();
    }

    public static String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(LocalDateTime.now());
    }

    public static List<Fund> load() {
        String currentDate = getCurrentDate();

        File dataFile = Paths.get(DATA_STORE_PATH, currentDate+ ".csv").toFile();

        return load(dataFile);
    }

    public static List<Fund> load(File dataFile) {
        funds = new ArrayList<>(9000);

        try (Stream<String> stream = Files.lines(dataFile.toPath())) {
            stream.forEach(line -> funds.add(parseFundFromLine(line)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return funds;
    }

    private static Fund parseFundFromLine(String line) {
        String[] parts = line.split(",");

        Fund fund = new Fund();
        fund.setId(parts[0]);
//        System.out.println("load from file:" + parts[1]);
        fund.setName(parts[1]);
        fund.setDate(parts[2]);
        fund.setNetAssetValue(parts[3]);
        fund.setAccumulatedNetValue(parts[4]);
        fund.setDailyIncrease(parts[5]);
        fund.setWeeklyIncrease(parts[6]);
        fund.setMonthlyIncrease(parts[7]);
        fund.setQuarterlyIncrease(parts[8]);
        fund.setHalfYearIncrease(parts[9]);
        fund.setLastYearIncrease(parts[10]);
        fund.setLastTwoYearsIncrease(parts[11]);
        fund.setLastThreeYearsIncrease(parts[12]);
        fund.setSinceThisYearIncrease(parts[13]);
        fund.setSinceFoundIncrease(parts[14]);
        fund.setCustomIncrease(parts[15]);
        fund.setFee(parts[16]);

        return fund;
    }

}
