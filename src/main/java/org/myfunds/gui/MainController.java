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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static java.lang.Double.parseDouble;

public class MainController {
    private static final String APP_STATE_FILE = "state.properties";
    private static final String FAVORITE_FUNDS_PROP = "favorite.funds";
    private static final String LAST_SEVERAL_DAYS_PROP = "last.several.days";

    @FXML
    private TableView<Fund> table;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField dailyIncreaseWeight;

    @FXML
    private TextField weeklyIncreaseWeight;

    @FXML
    private TextField customDaysTextField;
    @FXML
    private TextField customDaysIncreaseWeight;

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
    private TableColumn<Fund, String> customDaysIncreaseColumn;

    public MainController() {
    }

    @FXML
    public void initialize() {
        initRateButton();
        loadState();
        initTable();
        initCustomDaysTextField();
    }
    
    private void initCustomDaysTextField() {
        TextFormatter<Integer> formatter = new TextFormatter<>(c -> Pattern.matches("\\d*", c.getText()) ? c : null);
        customDaysTextField.setTextFormatter(formatter);
    }

    private void initRateButton() {
        BooleanBinding allWeightsFilledBinding = dailyIncreaseWeight.textProperty().isEmpty()
                                                        .or(weeklyIncreaseWeight.textProperty().isEmpty())
                                                        .or(customDaysIncreaseWeight.textProperty().isEmpty())
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
        ratingColumn = new TableColumn<>("??????");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        TableColumn<Fund, String> idColumn = new TableColumn<>("??????");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Fund, String> nameColumn = new TableColumn<>("??????");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(190);

        TableColumn<Fund, String> dateColumn = new TableColumn<>("????????????");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Fund, String> netAssetValueColumn = new TableColumn<>("??????");
        netAssetValueColumn.setCellValueFactory(new PropertyValueFactory<>("netAssetValue"));

        TableColumn<Fund, String> accumulatedNetValueColumn = new TableColumn<>("????????????");
        accumulatedNetValueColumn.setCellValueFactory(new PropertyValueFactory<>("accumulatedNetValue"));

        TableColumn<Fund, String> dailyIncreaseColumn = new TableColumn<>("?????????");
        dailyIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("dailyIncrease"));

        TableColumn<Fund, String> weeklyIncreaseColumn = new TableColumn<>("?????????");
        weeklyIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("weeklyIncrease"));

        customDaysIncreaseColumn = new TableColumn<>(String.format("???%s?????????",customDaysTextField.getText()));
        customDaysIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("customIncrease"));

        TableColumn<Fund, String> monthlyIncreaseColumn = new TableColumn<>("?????????");
        monthlyIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyIncrease"));

        TableColumn<Fund, String> quarterlyIncreaseColumn = new TableColumn<>("???3?????????");
        quarterlyIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("quarterlyIncrease"));

        TableColumn<Fund, String> halfYearIncreaseColumn = new TableColumn<>("???????????????");
        halfYearIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("halfYearIncrease"));

        TableColumn<Fund, String> lastYearIncreaseColumn = new TableColumn<>("???1?????????");
        lastYearIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("lastYearIncrease"));

        TableColumn<Fund, String> lastTwoYearsIncreaseColumn = new TableColumn<>("???2?????????");
        lastTwoYearsIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("lastTwoYearsIncrease"));

        TableColumn<Fund, String> lastThreeYearsIncreaseColumn = new TableColumn<>("???3?????????");
        lastThreeYearsIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("lastThreeYearsIncrease"));

        TableColumn<Fund, String> sinceThisYearIncreaseColumn = new TableColumn<>("??????????????????");
        sinceThisYearIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("sinceThisYearIncrease"));

        TableColumn<Fund, String> sinceFoundIncreaseColumn = new TableColumn<>("??????????????????");
        sinceFoundIncreaseColumn.setCellValueFactory(new PropertyValueFactory<>("sinceFoundIncrease"));

        TableColumn<Fund, String> feeColumn = new TableColumn<>("??????");
        feeColumn.setCellValueFactory(new PropertyValueFactory<>("fee"));

        ObservableList<TableColumn<Fund, ?>> columns = table.getColumns();
        columns.addAll(ratingColumn,idColumn,nameColumn,dateColumn,netAssetValueColumn, accumulatedNetValueColumn,
                dailyIncreaseColumn, weeklyIncreaseColumn, customDaysIncreaseColumn, monthlyIncreaseColumn, quarterlyIncreaseColumn,
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

        Stream.of(dailyIncreaseColumn, weeklyIncreaseColumn, customDaysIncreaseColumn, monthlyIncreaseColumn,quarterlyIncreaseColumn,
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
                                    MenuItem removeFromFavoritesMenuItem = new MenuItem("??????????????????");
                                    removeFromFavoritesMenuItem.setOnAction(event -> {favoriteFunds.remove(getItem().getId()); table.refresh();});

                                    getContextMenu().getItems().setAll(removeFromFavoritesMenuItem);
                                }
                                setStyle("-fx-background-color:gold");
                            } else {
                                if (getContextMenu() != null) {
                                    MenuItem addToFavoritesMenuItem = new MenuItem("???????????????");
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
        List<String> errors = validateInput();
        if (errors.isEmpty()) {
            statusLabel.setText("?????????????????????...");

        } else {
            System.out.println(errors);
            statusLabel.setText("?????????????????????????????????????????????");
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

                statusLabel.setText("??????????????????");
            });

        }).start();
    }

    public void scheduleToDownloadData() {
        new Thread(() -> {
            List<String> datas = null;
            try {
                Platform.runLater(() -> {
                    stage.getScene().getRoot().setCursor(Cursor.WAIT);
                    setInformation("?????????????????????????????????...");
                });
                datas = FundsDataFetcher.fetch(Integer.parseInt(customDaysTextField.getText()));
            } catch (IOException e) {
                Platform.runLater(() -> setInformation("??????????????????!"));
            }

            try {
                Platform.runLater(() -> setInformation("?????????????????????..."));
                FundsDataFetcher.save(datas);
                Platform.runLater(() -> setInformation("????????????."));

                funds = FundsDataMgr.load();
                Platform.runLater(() -> {
                    stage.getScene().getRoot().setCursor(Cursor.DEFAULT);
                    table.getItems().setAll(funds);
                });
            } catch (IOException e) {
                Platform.runLater(() -> setInformation("????????????????????????"));
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
        weights.put(RateFunds.CUSTOM_DAYS_INCREASE_WEIGHT, parseDouble(customDaysIncreaseWeight.getText()));

        return weights;
    }

    private List<String> validateInput() {
        List<String> errors = new ArrayList<>();
        String error = hasValidDoubleValue(dailyIncreaseWeight, "??????????????????????????????");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(weeklyIncreaseWeight, "??????????????????????????????");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(weeklyIncreaseWeight, "????????????????????????????????????");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(monthlyIncreaseWeight, "??????????????????????????????");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(quarterlyIncreaseWeight, "???3??????????????????????????????");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(halfYearIncreaseWeight, "????????????????????????????????????");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(lastYearIncreaseWeight, "???1??????????????????????????????");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(lastTwoYearsIncreaseWeight, "???2??????????????????????????????");
        if (error != null) {
            errors.add(error);
        }
        error = hasValidDoubleValue(lastThreeYearsIncreaseWeight, "???3??????????????????????????????");
        if (error != null) {
            errors.add(error);
        }
        error = hasValidDoubleValue(sinceThisYearIncreaseWeight, "???????????????????????????????????????");
        if (error != null) {
            errors.add(error);
        }

        error = hasValidDoubleValue(sinceFoundIncreaseWeight, "???????????????????????????????????????");
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
        fileChooser.setTitle("????????????????????????");
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

        Platform.runLater(() -> {
            customDaysIncreaseColumn.setText(String.format("???%s?????????", customDaysTextField.getText()));
        });
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

            Stream.of(dailyIncreaseWeight, weeklyIncreaseWeight, customDaysIncreaseWeight, monthlyIncreaseWeight, quarterlyIncreaseWeight,
                    halfYearIncreaseWeight, lastYearIncreaseWeight, lastTwoYearsIncreaseWeight, lastThreeYearsIncreaseWeight,
                    sinceThisYearIncreaseWeight, sinceFoundIncreaseWeight)
                    .forEach(weigthTextField -> prop.setProperty("weight." + weigthTextField.getId(), weigthTextField.getText()));

            prop.setProperty(FAVORITE_FUNDS_PROP, String.join("#", favoriteFunds));
            prop.setProperty(LAST_SEVERAL_DAYS_PROP, customDaysTextField.getText());

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
        Stream.of(dailyIncreaseWeight, weeklyIncreaseWeight, customDaysIncreaseWeight, monthlyIncreaseWeight, quarterlyIncreaseWeight,
                halfYearIncreaseWeight, lastYearIncreaseWeight, lastTwoYearsIncreaseWeight, lastThreeYearsIncreaseWeight,
                sinceThisYearIncreaseWeight, sinceFoundIncreaseWeight)
                .forEach(weigthTextField -> {
                    String weight = properties.getProperty("weight." + weigthTextField.getId(), "1");
                    weigthTextField.setText(weight);
                });

        customDaysTextField.setText(properties.getProperty(LAST_SEVERAL_DAYS_PROP, "14"));
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
            statusLabel.setText("??????????????????????????????");

        } else {
            foundFundsIndex = 0;
            table.scrollTo(foundFunds.get(0));

            if (foundFunds.size() > 1) {
                statusLabel.setText(String.format("?????????%d???????????????F3????????????????????????Shift+F3????????????????????????",foundFunds.size()));
            } else {
                statusLabel.setText(String.format("?????????%d?????????",foundFunds.size()));
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
            statusLabel.setText("???????????????????????????????????????");
            return;
        }

        foundFundsIndex++;
        table.scrollTo(foundFunds.get(foundFundsIndex));
        statusLabel.setText(String.format("???%d/%d?????????", foundFundsIndex+1, foundFunds.size()));
    }

    /**
     * Press F3 to search next fund.
     */
    private void searchPreviousFund() {
        if (foundFundsIndex == -1) {
            return;
        }
        if (foundFundsIndex == 0) {
            statusLabel.setText("????????????????????????????????????");
            return;
        }

        foundFundsIndex--;
        table.scrollTo(foundFunds.get(foundFundsIndex));
        statusLabel.setText(String.format("???%d/%d?????????", foundFundsIndex+1, foundFunds.size()));
    }
}
