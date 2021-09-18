package org.myfunds.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.myfunds.analysis.RateFunds;
import org.myfunds.data.Fund;
import org.myfunds.data.FundsDataFetcher;
import org.myfunds.data.FundsDataMgr;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static java.lang.Double.parseDouble;

public class MainController {
    private static final String APP_STATE_FILE = "state.properties";
    private static final String FAVORITE_FUNDS_PROP = "favorite.funds";

    @FXML
    private TableView<Fund> table;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField dailyIncreaseWeight;

    @FXML
    private TextField weeklyIncreaseWeight;
    @FXML
    private TextField monthlyIncreaseWeight;

    @FXML
    private TextField quarterlyIncreaseWeight;

    @FXML
    private TextField halfYearIncreaseWeight;

    @FXML
    private TextField lastYearIncreaseWeight;
    @FXML
    private TextField lastTwoYearsIncreaseWeight;
    @FXML
    private TextField lastThreeYearsIncreaseWeight;
    @FXML
    private TextField sinceThisYearIncreaseWeight;
    @FXML
    private TextField sinceFoundIncreaseWeight;

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox showFavoritesOnly;

    @FXML
    private Button rateButton;
    private List<Fund> funds;
    private TableColumn<Fund, Integer> ratingColumn;
    private Stage stage;

    private Set<String> favoriteFunds = new HashSet<>();
    private List<Fund> foundFunds;
    private int foundFundsIndex = -1;

    public MainController() {
    }

    @FXML
    public void initialize() {
        initRateButton();
        loadState();
        initTable();
    }

    private void initRateButton() {
        BooleanBinding allWeightsFilledBinding = dailyIncreaseWeight.textProperty().isEmpty()
                                                        .or(weeklyIncreaseWeight.textProperty().isEmpty())
                                                        .or(monthlyIncreaseWeight.textProperty().isEmpty())
                                                        .or(quarterlyIncreaseWeight.textProperty().isEmpty())
                                                        .or(halfYearIncreaseWeight.textProperty().isEmpty())
                                                        .or(lastYearIncreaseWeight.textProperty().isEmpty())
                                                        .or(lastTwoYearsIncreaseWeight.textProperty().isEmpty())
                                                        .or(lastThreeYearsIncreaseWeight.textProperty().isEmpty())
                                                        .or(sinceThisYearIncreaseWeight.textProperty().isEmpty())
                                                        .or(sinceFoundIncreaseWeight.textProperty().isEmpty());

        rateButton.disableProperty().bind(allWeightsFilledBinding);
    }

    public void initTable() {
        ratingColumn = new TableColumn<>("评分");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        TableColumn<Fund, String> idColumn = new TableColumn<>("代码");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Fund, String> nameColumn = new TableColumn<>("名字");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(190);

        TableColumn<Fund, String> dateColumn = new TableColumn<>("更新日期");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Fund, String> netAssetValueColumn = new TableColumn<>("净值");
        netAssetValueColumn.setCellValueFactory(new PropertyValueFactory<>("netAssetValue"));

        TableColumn<Fund, String> accumulatedNetValueColumn = new TableColumn<>("累计净值");
        accumulatedNetValueColumn.setCellValueFactory(new PropertyValueFactory<>("accumulatedNetValue"));

        TableColumn<Fund, String> dailyIncreaseColumn = new TableColumn<>("日涨幅");
        dailyIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("dailyIncrease"));

        TableColumn<Fund, String> weeklyIncreaseColumn = new TableColumn<>("周涨幅");
        weeklyIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("weeklyIncrease"));

        TableColumn<Fund, String> monthlyIncreaseColumn = new TableColumn<>("月涨幅");
        monthlyIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyIncrease"));

        TableColumn<Fund, String> quarterlyIncreaseColumn = new TableColumn<>("近3月涨幅");
        quarterlyIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("quarterlyIncrease"));

        TableColumn<Fund, String> halfYearIncreaseColumn = new TableColumn<>("近半年涨幅");
        halfYearIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("halfYearIncrease"));

        TableColumn<Fund, String> lastYearIncreaseColumn = new TableColumn<>("近1年涨幅");
        lastYearIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("lastYearIncrease"));

        TableColumn<Fund, String> lastTwoYearsIncreaseColumn = new TableColumn<>("近2年涨幅");
        lastTwoYearsIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("lastTwoYearsIncrease"));

        TableColumn<Fund, String> lastThreeYearsIncreaseColumn = new TableColumn<>("近3年涨幅");
        lastThreeYearsIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("lastThreeYearsIncrease"));

        TableColumn<Fund, String> sinceThisYearIncreaseColumn = new TableColumn<>("本年以来涨幅");
        sinceThisYearIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("sinceThisYearIncrease"));

        TableColumn<Fund, String> sinceFoundIncreaseColumn = new TableColumn<>("成立以来涨幅");
        sinceFoundIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("sinceFoundIncrease"));

        TableColumn<Fund, String> feeColumn = new TableColumn<>("费用");
        feeColumn.setCellValueFactory(new PropertyValueFactory<>("fee"));

        ObservableList<TableColumn<Fund, ?>> columns = table.getColumns();
        columns.addAll(ratingColumn,idColumn,nameColumn,dateColumn,netAssetValueColumn, accumulatedNetValueColumn,
                dailyIncreaseColumn, weeklyIncreaseColumn, monthlyIncreaseColumn, quarterlyIncreaseColumn,
                halfYearIncreaseColumn,lastYearIncreaseColumn, lastTwoYearsIncreaseColumn, lastThreeYearsIncreaseColumn,
                sinceThisYearIncreaseColumn, sinceFoundIncreaseColumn,feeColumn);

//        columns.forEach(c -> c.setPrefWidth(120));
//        accumulatedNetValueColumn.setPrefWidth(160);

        //set the sorting property
        Comparator<String> percentNumberComparator = (v1, v2) -> {
            boolean isV1Valid = v1.contains("%");
            boolean isV2Valid = v2.contains("%");

            if(!isV1Valid) {
                return isV2Valid? -1 : 0;
            }

            //v1 is valid
            if (!isV2Valid) {
                return 1;
            }

            Double d1 = Double.parseDouble(v1.replace("%", "")) ;
            Double d2 = Double.parseDouble(v2.replace("%", ""));
            return d1.compareTo(d2);
        };

        Stream.of(dailyIncreaseColumn, weeklyIncreaseColumn, monthlyIncreaseColumn,quarterlyIncreaseColumn,
                halfYearIncreaseColumn, lastYearIncreaseColumn, lastTwoYearsIncreaseColumn,
                lastThreeYearsIncreaseColumn, sinceThisYearIncreaseColumn, sinceFoundIncreaseColumn,feeColumn)
                .forEach(column -> column.setComparator(percentNumberComparator));

        initTableContextMenu();
    }

    private void initTableContextMenu() {
        table.setRowFactory(tableView -> {
                    final TableRow<Fund> row = new TableRow<Fund>() {
                        @Override
                        protected void updateItem(Fund item, boolean empty) {
                            super.updateItem(item, empty);

                            if (isFavoriteFund(item)) {
                                if (getContextMenu() != null) {
                                    MenuItem removeFromFavoritesMenuItem = new MenuItem("Remove from favorite");
                                    removeFromFavoritesMenuItem.setOnAction(event -> {favoriteFunds.remove(getItem().getId()); table.refresh();});

                                    getContextMenu().getItems().setAll(removeFromFavoritesMenuItem);
                                }
                                setStyle("-fx-background-color:gold");
                            } else {
                                if (getContextMenu() != null) {
                                    MenuItem addToFavoritesMenuItem = new MenuItem("Add to favorite");
                                    addToFavoritesMenuItem.setOnAction(e -> {
                                        favoriteFunds.add(getItem().getId());
                                        table.refresh();
                                    });
                                    getContextMenu().getItems().setAll(addToFavoritesMenuItem);
                                }
                                setStyle("");
                            }
                        }
                    };
                    final ContextMenu rowMenu = new ContextMenu();
//                    addToFavoritesMenuItem = new MenuItem("Add to favorite");
//                    addToFavoritesMenuItem.setOnAction(e -> {
//                        favoriteFunds.add(row.getItem().getId());
//                        table.refresh();
//                    });
//
//                    removeFromFavoritesMenuItem = new MenuItem("Remove from favorite");
//                    removeFromFavoritesMenuItem.setOnAction(event -> {favoriteFunds.remove(row.getItem().getId()); table.refresh();});
//
//                    rowMenu.getItems().addAll(addToFavoritesMenuItem, removeFromFavoritesMenuItem);

                    // only display context menu for non-null items:
                    row.contextMenuProperty().bind(
                            Bindings.when(Bindings.isNotNull(row.itemProperty()))
                                    .then(rowMenu)
                                    .otherwise((ContextMenu)null));



                    return row;
                });
    }

    private boolean isFavoriteFund(Fund fund) {
        if (fund == null) {
            return false;
        }
        return favoriteFunds.stream().anyMatch(f -> fund.getId().equals(f));
    }


    @FXML
    public void onRate() {
        List<String> erros = validateInput();
        if (erros.isEmpty()) {
            statusLabel.setText("正在给基金评分...");

        } else {
            System.out.println(erros);
            statusLabel.setText("权重值必须是数字，请重新输入！");
            return;
        }

        scheduleToRateFunds();
    }

    private void scheduleToRateFunds() {
        new Thread(() -> {
            Map<String, Double> weights = collectWeightInput();

            RateFunds rateFunds = new RateFunds(funds, weights);
            Map<String, Double> rates = rateFunds.rate();

            System.out.println("funds size:" + funds.size());

            funds.forEach( fund -> {
                double rating = rates.get(fund.getId()) * 10000 ;
                fund.setRating((int)rating);
//                System.out.println(fund.getRating());
            });

            Platform.runLater(() -> {
                ratingColumn.setSortType(TableColumn.SortType.DESCENDING);
                table.getSortOrder().setAll(ratingColumn);

                table.getItems().setAll(funds);
                table.sort();

                statusLabel.setText("基金评分完毕");
            });

        }).start();
    }

    public void scheduleToDownloadData() {
        new Thread(() -> {
            List<String> datas = null;
            try {
                Platform.runLater(() -> {
                    stage.getScene().getRoot().setCursor(Cursor.WAIT);
                    setInformation("正在下载今天的基金数据...");
                });
                datas = FundsDataFetcher.fetch();
            } catch (IOException e) {
                Platform.runLater(() -> setInformation("下载数据失败!"));
            }

            try {
                Platform.runLater(() -> setInformation("保存数据到本地..."));
                FundsDataFetcher.save(datas);
                Platform.runLater(() -> setInformation("保存成功."));

                funds = FundsDataMgr.load();
                Platform.runLater(() -> {
                    stage.getScene().getRoot().setCursor(Cursor.DEFAULT);
                    table.getItems().setAll(funds);
                });
            } catch (IOException e) {
                Platform.runLater(() -> setInformation("保存到本地失败！"));
            }

        }).start();
    }

    private Map<String, Double> collectWeightInput() {
        Map<String, Double> weights = new HashMap<>();
        weights.put(RateFunds.DAILY_INCREASE_WEIGHT, parseDouble(dailyIncreaseWeight.getText()));
        weights.put(RateFunds.WEEKLY_INCREASE_WEIGHT, parseDouble(weeklyIncreaseWeight.getText()));
        weights.put(RateFunds.MONTHLY_INCREASE_WEIGHT, parseDouble(monthlyIncreaseWeight.getText()));
        weights.put(RateFunds.QUATERLY_INCREASE_WEIGHT, parseDouble(quarterlyIncreaseWeight.getText()));
        weights.put(RateFunds.HALF_YEAR_INCREASE_WEIGHT, parseDouble(halfYearIncreaseWeight.getText()));
        weights.put(RateFunds.YEAR_INCREASE_WEIGHT, parseDouble(lastYearIncreaseWeight.getText()));
        weights.put(RateFunds.TWO_YEARS_INCREASE_WEIGHT, parseDouble(lastTwoYearsIncreaseWeight.getText()));
        weights.put(RateFunds.THREE_YEARS_INCREASE_WEIGHT, parseDouble(lastThreeYearsIncreaseWeight.getText()));
        weights.put(RateFunds.SINCE_THIS_YEAR_INCREASE_WEIGHT, parseDouble(sinceThisYearIncreaseWeight.getText()));
        weights.put(RateFunds.SINCE_FOUND_INCREASE_WEIGHT, parseDouble(sinceFoundIncreaseWeight.getText()));

        return weights;
    }

    private List<String> validateInput() {
        List<String> errors = new ArrayList<>();
        String error = hasValidDoubleValue(dailyIncreaseWeight, "日增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(weeklyIncreaseWeight, "周增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }
        error = hasValidDoubleValue(monthlyIncreaseWeight, "月增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(quarterlyIncreaseWeight, "近3月增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(halfYearIncreaseWeight, "近半年增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(lastYearIncreaseWeight, "近1年增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(lastTwoYearsIncreaseWeight, "近2年增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }
        error = hasValidDoubleValue(lastThreeYearsIncreaseWeight, "近3年增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }
        error = hasValidDoubleValue(sinceThisYearIncreaseWeight, "今年以来增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(sinceFoundIncreaseWeight, "成立以来增幅权重不是数字！");
        if (error != null) {
            errors.add(error);
        }


        return errors;
    }

    private String hasValidDoubleValue(TextField tf, String error) {
        try {
            parseDouble(tf.getText());
        } catch (NumberFormatException e) {
            return error;
        }

        return null;
    }

    @FXML
    public void onLoadLocalData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开历史数据文件");
        fileChooser.setInitialDirectory(Paths.get(System.getProperty("user.home"), "myFunds").toFile());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            funds = FundsDataMgr.load(file);
            table.getItems().setAll(funds);
        }
    }

    @FXML
    public void onLoadTodayData() {
        scheduleToDownloadData();

    }

    public void setFundsData(List<Fund> fundsData) {
        this.funds = fundsData;
        table.getItems().addAll(fundsData);
    }

    public void setInformation(String info) {
        statusLabel.setText(info);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void addAccelerators(Scene scene) {
        KeyCombination f3 = new KeyCodeCombination(KeyCode.F3);
        scene.getAccelerators().put(f3, this::searchNextFund);

        KeyCombination shift_F3 = new KeyCodeCombination(KeyCode.F3, KeyCombination.SHIFT_DOWN);
        scene.getAccelerators().put(shift_F3, this::searchPreviousFund);
    }

    public void saveState() {
        File stateFile = Paths.get(FundsDataMgr.DATA_STORE_PATH, APP_STATE_FILE).toFile();
        try (OutputStream output = new FileOutputStream(stateFile)) {
            Properties prop = new Properties();

            Stream.of(dailyIncreaseWeight, weeklyIncreaseWeight, monthlyIncreaseWeight, quarterlyIncreaseWeight,
                    halfYearIncreaseWeight, lastYearIncreaseWeight, lastTwoYearsIncreaseWeight, lastThreeYearsIncreaseWeight,
                    sinceThisYearIncreaseWeight, sinceFoundIncreaseWeight)
                    .forEach(weigthTextField -> prop.setProperty("weight." + weigthTextField.getId(), weigthTextField.getText()));

            prop.setProperty(FAVORITE_FUNDS_PROP, String.join("#", favoriteFunds));
            prop.store(output, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadState() {
        File stateFile = Paths.get(FundsDataMgr.DATA_STORE_PATH, APP_STATE_FILE).toFile();
        try(InputStream inputStream = new FileInputStream(stateFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);

            restoreUiState(properties);
            loadFavoriteFunds(properties.getProperty(FAVORITE_FUNDS_PROP, ""));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void restoreUiState(Properties properties) {
        Stream.of(dailyIncreaseWeight, weeklyIncreaseWeight, monthlyIncreaseWeight, quarterlyIncreaseWeight,
                halfYearIncreaseWeight, lastYearIncreaseWeight, lastTwoYearsIncreaseWeight, lastThreeYearsIncreaseWeight,
                sinceThisYearIncreaseWeight, sinceFoundIncreaseWeight)
                .forEach(weigthTextField -> {
                    String weight = properties.getProperty("weight." + weigthTextField.getId(), "1");
                    weigthTextField.setText(weight);
                });

    }


    private void loadFavoriteFunds(String fundsStr) {
         String[] myFunds = fundsStr.split("#");
         favoriteFunds = Stream.of(myFunds).collect(Collectors.toSet());
    }

    @FXML
    public void onShowFavoriteFundsOnlyClicked() {
        if (showFavoritesOnly.isSelected()) {
            List<Fund> myFunds = funds.stream().filter(fund -> favoriteFunds.stream().anyMatch(f -> f.equals(fund.getId()))).collect(Collectors.toList());
            table.getItems().setAll(myFunds);
        } else {
            table.getItems().setAll(funds);
        }
    }

    @FXML
    public void onSearchFieldKeyPressed(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            onSearchFund(null);
        }
    }

    @FXML
    private void onSearchFund(ActionEvent actionEvent) {
        String searchKeyWord = searchField.getText();

        foundFunds = funds.stream()
                                .filter(fund -> fund.getId().contains(searchKeyWord) || fund.getName().contains(searchKeyWord))
                                .collect(Collectors.toList());

        TableView.TableViewSelectionModel<Fund> selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        selectionModel.clearSelection();
        foundFunds.forEach(selectionModel::select);

        foundFundsIndex = -1;

        if (foundFunds.isEmpty()) {
            statusLabel.setText("没有找到相关的基金。");

        } else {
            foundFundsIndex = 0;
            table.scrollTo(foundFunds.get(0));

            if (foundFunds.size() > 1) {
                statusLabel.setText(String.format("共搜到%d只基金。按F3搜索下一只基金，Shift+F3搜索上一只基金。",foundFunds.size()));
            } else {
                statusLabel.setText(String.format("共搜到%d只基金",foundFunds.size()));
            }
        }
    }

    /**
     * Press F3 to search next fund.
     */
    private void searchNextFund() {
        if (foundFundsIndex == -1) {
            return;
        }
        if (foundFundsIndex == foundFunds.size() - 1) {
            statusLabel.setText("已到搜索到的最后一只基金。");
            return;
        }

        foundFundsIndex++;
        table.scrollTo(foundFunds.get(foundFundsIndex));
        statusLabel.setText(String.format("第%d/%d只基金", foundFundsIndex+1, foundFunds.size()));
    }

    /**
     * Press F3 to search next fund.
     */
    private void searchPreviousFund() {
        if (foundFundsIndex == -1) {
            return;
        }
        if (foundFundsIndex == 0) {
            statusLabel.setText("已到搜索到的第一只基金。");
            return;
        }

        foundFundsIndex--;
        table.scrollTo(foundFunds.get(foundFundsIndex));
        statusLabel.setText(String.format("第%d/%d只基金", foundFundsIndex+1, foundFunds.size()));
    }
}
