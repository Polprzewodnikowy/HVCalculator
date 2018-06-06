import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AcfView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Kalkulator współczynnika korekcyjnego na warunki atmosferyczne");
        //primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

}
