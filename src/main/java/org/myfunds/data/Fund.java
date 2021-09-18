package org.myfunds.data;

public class Fund {
//    002943,广发多因子混,06-11,3.0182,3.2585,0.17%,1.59%,4.19%,20.41%,65.82%,108.49%,191.32%,209.94%,48.95%,241.77%,109.36%,0.15%
//近1周	近1月	近3月	近6月	近1年	近2年	近3年	今年来	成立来
//自定义
//手续费
    private int rating;
    private String id;
    private String name;
    private String date;
    private String netAssetValue;
    private String accumulatedNetValue;

    private String dailyIncrease;
    private String weeklyIncrease;
    private String monthlyIncrease;
    private String quarterlyIncrease;
    private String halfYearIncrease;
    private String lastYearIncrease;
    private String lastTwoYearsIncrease;
    private String lastThreeYearsIncrease;
    private String sinceThisYearIncrease;
    private String sinceFoundIncrease;
    private String customIncrease;
    private String fee;


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNetAssetValue() {
        return netAssetValue;
    }

    public void setNetAssetValue(String netAssetValue) {
        this.netAssetValue = netAssetValue;
    }

    public String getAccumulatedNetValue() {
        return accumulatedNetValue;
    }

    public void setAccumulatedNetValue(String accumulatedNetValue) {
        this.accumulatedNetValue = accumulatedNetValue;
    }

    public String getDailyIncrease() {
        return dailyIncrease;
    }

    public void setDailyIncrease(String dailyIncrease) {
        this.dailyIncrease = dailyIncrease;
    }

    public String getWeeklyIncrease() {
        return weeklyIncrease;
    }

    public void setWeeklyIncrease(String weeklyIncrease) {
        this.weeklyIncrease = weeklyIncrease;
    }

    public String getMonthlyIncrease() {
        return monthlyIncrease;
    }

    public void setMonthlyIncrease(String monthlyIncrease) {
        this.monthlyIncrease = monthlyIncrease;
    }

    public String getQuarterlyIncrease() {
        return quarterlyIncrease;
    }

    public void setQuarterlyIncrease(String quarterlyIncrease) {
        this.quarterlyIncrease = quarterlyIncrease;
    }

    public String getHalfYearIncrease() {
        return halfYearIncrease;
    }

    public void setHalfYearIncrease(String halfYearIncrease) {
        this.halfYearIncrease = halfYearIncrease;
    }

    public String getLastYearIncrease() {
        return lastYearIncrease;
    }

    public void setLastYearIncrease(String lastYearIncrease) {
        this.lastYearIncrease = lastYearIncrease;
    }

    public String getLastTwoYearsIncrease() {
        return lastTwoYearsIncrease;
    }

    public void setLastTwoYearsIncrease(String lastTwoYearsIncrease) {
        this.lastTwoYearsIncrease = lastTwoYearsIncrease;
    }

    public String getLastThreeYearsIncrease() {
        return lastThreeYearsIncrease;
    }

    public void setLastThreeYearsIncrease(String lastThreeYearsIncrease) {
        this.lastThreeYearsIncrease = lastThreeYearsIncrease;
    }

    public String getSinceThisYearIncrease() {
        return sinceThisYearIncrease;
    }

    public void setSinceThisYearIncrease(String sinceThisYearIncrease) {
        this.sinceThisYearIncrease = sinceThisYearIncrease;
    }

    public String getSinceFoundIncrease() {
        return sinceFoundIncrease;
    }

    public void setSinceFoundIncrease(String sinceFoundIncrease) {
        this.sinceFoundIncrease = sinceFoundIncrease;
    }

    public String getCustomIncrease() {
        return customIncrease;
    }

    public void setCustomIncrease(String customIncrease) {
        this.customIncrease = customIncrease;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
