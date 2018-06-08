import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {

    public TextField Temperature;
    public TextField Pressure;
    public TextField RelativeHumidity;
    public TextField AbsoluteHumidity;
    public TextField V50;
    public TextField MinimumDischargePath;
    public ChoiceBox<ACF.voltageType> VoltageType;
    public Label AtmosphericCorrectionFactor;

    private ACF acf;

    @FXML
    public void initialize() {
        acf = new ACF();

        VoltageType.setItems(FXCollections.observableArrayList(ACF.voltageType.values()));
        VoltageType.setValue(acf.getVoltageType());
        VoltageType.setOnAction(e -> updateVoltageType());

        Temperature.setText(String.format("%.1f", acf.getTemperature()));
        Temperature.setOnAction(e -> updateTemperature());
        Temperature.focusedProperty().addListener((arg0, oldValue, newValue) -> updateTemperature());

        Pressure.setText(String.format("%.1f", acf.getPressure()));
        Pressure.setOnAction(e -> updatePressure());
        Pressure.focusedProperty().addListener((arg0, oldValue, newValue) -> updatePressure());

        RelativeHumidity.setText(String.format("%.1f", acf.getRelativeHumidity()));
        RelativeHumidity.setOnAction(e -> updateRelativeHumidity());
        RelativeHumidity.focusedProperty().addListener((arg0, oldValue, newValue) -> updateRelativeHumidity());

        AbsoluteHumidity.setText(String.format("%.1f", acf.getAbsoluteHumidity()));
        AbsoluteHumidity.setOnAction(e -> updateAbsoluteHumidity());
        AbsoluteHumidity.focusedProperty().addListener((arg0, oldValue, newValue) -> updateAbsoluteHumidity());

        V50.setText(String.format("%.1f", acf.getV50()));
        V50.setOnAction(e -> updateV50());
        V50.focusedProperty().addListener((arg0, oldValue, newValue) -> updateV50());

        MinimumDischargePath.setText(String.format("%.1f", acf.getMinimumDischargePath()));
        MinimumDischargePath.setOnAction(e -> updateMinimumDischargePath());
        MinimumDischargePath.focusedProperty().addListener((arg0, oldValue, newValue) -> updateMinimumDischargePath());

        updateACF();
    }

    private void updateTemperature() {
        try {
            double temperature = Double.parseDouble(Temperature.getText());
            acf.setTemperature(temperature);
            Temperature.setStyle("");
            RelativeHumidity.setText(String.format("%.1f", acf.getRelativeHumidity()));
            if (acf.getRelativeHumidity() > 100.0) {
                RelativeHumidity.setStyle("-fx-background-color: #ffbcbc;");
            } else {
                RelativeHumidity.setStyle("");
            }
            updateACF();
        } catch (Exception e) {
            Temperature.setStyle("-fx-background-color: #ffbcbc;");
        }
    }

    private void updatePressure() {
        try {
            double pressure = Double.parseDouble(Pressure.getText());
            acf.setPressure(pressure);
            Pressure.setStyle("");
            updateACF();
        } catch (Exception e) {
            Pressure.setStyle("-fx-background-color: #ffbcbc;");
        }
    }

    private void updateRelativeHumidity() {
        try {
            double relativeHumidity = Double.parseDouble(RelativeHumidity.getText());
            acf.setRelativeHumidity(relativeHumidity);
            RelativeHumidity.setStyle("");
            AbsoluteHumidity.setText(String.format("%.1f", acf.getAbsoluteHumidity()));
            updateACF();
        } catch (Exception e) {
            RelativeHumidity.setStyle("-fx-background-color: #ffbcbc;");
        }
    }

    private void updateAbsoluteHumidity() {
        try {
            double absoluteHumidity = Double.parseDouble(AbsoluteHumidity.getText());
            acf.setAbsoluteHumidity(absoluteHumidity);
            AbsoluteHumidity.setStyle("");
            RelativeHumidity.setText(String.format("%.1f", acf.getRelativeHumidity()));
            if (acf.getRelativeHumidity() > 100.0) {
                RelativeHumidity.setStyle("-fx-background-color: #ffbcbc;");
            } else {
                RelativeHumidity.setStyle("");
            }
            updateACF();
        } catch (Exception e) {
            AbsoluteHumidity.setStyle("-fx-background-color: #ffbcbc;");
        }
    }

    private void updateV50() {
        try {
            double v50 = Double.parseDouble(V50.getText());
            acf.setV50(v50);
            V50.setStyle("");
            updateACF();
        } catch (Exception e) {
            V50.setStyle("-fx-background-color: #ffbcbc;");
        }
    }

    private void updateMinimumDischargePath() {
        try {
            double minimumDischargePath = Double.parseDouble(MinimumDischargePath.getText());
            acf.setMinimumDischargePath(minimumDischargePath);
            MinimumDischargePath.setStyle("");
            updateACF();
        } catch (Exception e) {
            MinimumDischargePath.setStyle("-fx-background-color: #ffbcbc;");
        }
    }

    private void updateVoltageType() {
        acf.setVoltageType(VoltageType.getValue());
        updateACF();
    }

    private void updateACF() {
        AtmosphericCorrectionFactor.setText(String.format("%.3f", acf.getAtmosphericCorrectionFactor()));
    }

}
