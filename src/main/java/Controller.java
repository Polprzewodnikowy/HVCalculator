import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class AcfController {

    /**
     *
     */
    public TextField vTemperature;
    public TextField vPressure;
    public TextField vRelativeHumidity;
    public TextField vAbsoluteHumidity;
    public TextField vV50;
    public TextField vMinimumDischargePath;
    public ChoiceBox<AtmosphericCorrectionFactor.voltageType> vVoltageType;

    /**
     *
     */
    private AtmosphericCorrectionFactor acf;

    /**
     *
     */
    public AcfController() {
        acf = new AtmosphericCorrectionFactor();
    }

    /**
     *
     */
    @FXML
    public void initialize() {
        // Populate ChoiceBox
        vVoltageType.setItems(FXCollections.observableArrayList(AtmosphericCorrectionFactor.voltageType.values()));

        // Set default values
        vTemperature.setText(Double.toString(acf.getTemperature()));
        vPressure.setText(Double.toString(acf.getPressure()));
        vRelativeHumidity.setText(Double.toString(acf.getRelativeHumidity()));
        vAbsoluteHumidity.setText(Double.toString(acf.getAbsoluteHumidity()));
        vV50.setText(Double.toString(acf.getV50()));
        vMinimumDischargePath.setText(Double.toString(acf.getMinimumDischargePath()));
        vVoltageType.setValue(acf.getVoltageType());
    }

}
