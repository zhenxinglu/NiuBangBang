package org.myfunds.analysis;

import org.myfunds.data.Fund;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RateFunds {
    public static final String DAILY_INCREASE_WEIGHT = "dailyIncreaseWeight";
    public static final String WEEKLY_INCREASE_WEIGHT = "weeklyIncreaseWeight";
    public static final String MONTHLY_INCREASE_WEIGHT = "monthlyIncreaseWeight";
    public static final String QUATERLY_INCREASE_WEIGHT = "quaterlyIncreaseWeight";
    public static final String HALF_YEAR_INCREASE_WEIGHT = "halfYearIncreaseWeight";
    public static final String YEAR_INCREASE_WEIGHT = "yearIncreaseWeight";
    public static final String TWO_YEARS_INCREASE_WEIGHT = "twoYearsIncreaseWeight";
    public static final String THREE_YEARS_INCREASE_WEIGHT = "threeYearsIncreaseWeight";
    public static final String SINCE_THIS_YEAR_INCREASE_WEIGHT = "sinceThisYearIncreaseWeight";
    public static final String SINCE_FOUND_INCREASE_WEIGHT = "sinceFoundIncreaseWeight";

    private List<Fund> funds;
    private Map<String, Double> factors;

    public RateFunds(List<Fund> funds, Map<String,Double> factors) {
        this.funds = funds;
        this.factors =factors;
    }

    public Map<String, Double> rate() {
        Map<String, Double> dailyIncreaseRates = rate(normalizeIncrease(Fund::getDailyIncrease),  factors.get(DAILY_INCREASE_WEIGHT));
        Map<String, Double> weeklyIncreaseRates = rate(normalizeIncrease(Fund::getWeeklyIncrease), factors.get(WEEKLY_INCREASE_WEIGHT));
        Map<String, Double> monthlyIncreaseRates = rate(normalizeIncrease(Fund::getMonthlyIncrease), factors.get(MONTHLY_INCREASE_WEIGHT));
        Map<String, Double> quaterlyIncreaseRates = rate(normalizeIncrease(Fund::getQuarterlyIncrease), factors.get(QUATERLY_INCREASE_WEIGHT));
        Map<String, Double> halfYearIncreaseRates = rate(normalizeIncrease(Fund::getHalfYearIncrease), factors.get(HALF_YEAR_INCREASE_WEIGHT));
        Map<String, Double> yearlyIncreaseRates = rate(normalizeIncrease(Fund::getLastYearIncrease), factors.get(YEAR_INCREASE_WEIGHT));
        Map<String, Double> twoYearsIncreaseRates = rate(normalizeIncrease(Fund::getLastTwoYearsIncrease), factors.get(TWO_YEARS_INCREASE_WEIGHT));
        Map<String, Double> threeYearIncreaseRates = rate(normalizeIncrease(Fund::getLastThreeYearsIncrease), factors.get(THREE_YEARS_INCREASE_WEIGHT));
        Map<String, Double> sinceThisYearIncreaseRates = rate(normalizeIncrease(Fund::getSinceThisYearIncrease), factors.get(SINCE_THIS_YEAR_INCREASE_WEIGHT));
        Map<String, Double> sinceFoundIncreaseRates = rate(normalizeIncrease(Fund::getSinceFoundIncrease), factors.get(SINCE_FOUND_INCREASE_WEIGHT));

        return Stream.of(dailyIncreaseRates, weeklyIncreaseRates, monthlyIncreaseRates, quaterlyIncreaseRates,
                                                  halfYearIncreaseRates,yearlyIncreaseRates, twoYearsIncreaseRates, threeYearIncreaseRates,
                                                  sinceThisYearIncreaseRates, sinceFoundIncreaseRates).flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1 + v2));

    }


    private Map<String, Double> rate(Map<String, Double> increases, Double weight) {
        double sumSquare = increases.values().stream().reduce(0.0d, (sum, increase) -> sum + increase* increase).doubleValue();

        return increases.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
            double increase = e.getValue();
            double rating = increase * increase * weight/ sumSquare;
            return e.getValue() > 0.0d? rating : -rating;
        }));
    }

    private Map<String, Double> normalizeIncrease(Function<Fund, String> dataSupplier) {
        Map<String, Double> increases = funds.stream().collect(Collectors.toMap(Fund::getId, fund -> {
            String increase = dataSupplier.apply(fund).replace("%", "");

            return parse(increase);
        }));

        double averageIncrease = increases.values().stream().filter(d -> !Double.isNaN(d)).mapToDouble(d -> d).average().getAsDouble();

        increases.forEach((id, increase) -> {
            if(Double.isNaN(increase)) {
                increases.put(id, averageIncrease);
            }
        });

        return increases;
    }

    public static void main(String[] args) {

        Map<String, Double> myMap = new HashMap<>();
        myMap.put("1", 1.0);
        myMap.put("2", 2.0);

        Map<String, Double> myMap2 = new HashMap<>();
        myMap2.put("1", 10.0);
        myMap2.put("2", 20.0);

        Map<String, Double> myMap3 = new HashMap<>();
        myMap3.put("1", 100.0);
        myMap3.put("2", 200.0);
        myMap3.put("3", 200.0);

        Map<String, Double> targetMap = Stream.of(myMap, myMap2, myMap3).flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1 + v2));

        System.out.println(targetMap);

    }

    private Double parse(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            System.out.println("fail to parse double value: " + str);
        }

        return Double.NaN;
    }

}
