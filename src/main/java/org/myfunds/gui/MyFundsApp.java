package org.myfunds.gui;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.myfunds.data.Fund;
import org.myfunds.data.FundsDataMgr;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MyFundsApp extends Application {

    private MainController controller;
    private List<Fund> funds = Collections.emptyList();
    private boolean dataFound = true;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));

        VBox root = loader.<VBox>load();
        controller = loader.getController();
        controller.setStage(primaryStage);

        Scene scene = new Scene(root);
        controller.addAccelerators(scene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("牛棒棒");
        //primaryStage.setResizable(false);
//        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        if (!dataFound) {
            Optional<ButtonType> result = infoBox("Click OK to download today's funds data.", "Information Dialog", "Today's data is not fetched yet!");
            if (result.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                controller.scheduleToDownloadData();
            }
        }

        controller.setFundsData(funds);
    }



    private void updateFundsTable() {

    }

    private static Optional<ButtonType> infoBox(String infoMessage, String titleBar, String headerMessage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        return alert.showAndWait();
    }

    @Override
    public void init() throws Exception {
        super.init();

        if (FundsDataMgr.hasCurrentDateData()) {
            dataFound = true;
            funds = FundsDataMgr.load();
        } else {
            dataFound = false;
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        controller.saveState();
    }
}