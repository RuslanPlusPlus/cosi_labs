package com.rusned.laba1.app;

import com.rusned.laba1.view.App;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        new App().launch();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
