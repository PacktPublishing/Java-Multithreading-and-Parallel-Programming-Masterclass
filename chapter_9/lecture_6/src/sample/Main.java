package sample;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class HeavyTask extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}

public class Main extends Application {

    private ExecutorService service = Executors.newFixedThreadPool(4);

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();

        Label label = new Label();
        label.setText("Status: not started");

        Button button = new Button();
        button.setText("Click me!");

        button.setOnMouseClicked(event -> {
            HeavyTask heavyTask = new HeavyTask();

            heavyTask.setOnRunning(state -> {
                label.setText("Status: task is in progress");
            });

            heavyTask.setOnSucceeded(state -> {
                label.setText("Status: task completed!");
            });

            service.submit(heavyTask);
            System.out.println("Button pressed!");
        });

        flowPane.getChildren().addAll(button, label);

        FlowPane.setMargin(button, new Insets(20, 20, 20, 20));

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(flowPane, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
