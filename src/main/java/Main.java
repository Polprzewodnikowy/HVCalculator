import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.US);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Kalkulator współczynnika korekcyjnego");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
