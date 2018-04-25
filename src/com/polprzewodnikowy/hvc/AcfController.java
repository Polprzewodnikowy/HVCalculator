package com.polprzewodnikowy.hvc;

import javafx.fxml.FXML;

public class AcfController {

    private AtmosphericCorrectionFactor acf;

    public AcfController() {
        acf = new AtmosphericCorrectionFactor();
    }

    @FXML
    public void initialize() {
        acf.setTemperature(20.0);
        acf.setPressure(101.3);
        acf.setAbsoluteHumidity(11.0);
//      acf.setRelativeHumidity(78.1903);
        acf.setVoltageType(AtmosphericCorrectionFactor.voltageType.DC);
        acf.setV50(500.0);
        acf.setMinimumDischargePath(1.0);

        System.out.print("Wartość współczynnika: ");
        System.out.println(acf.getAtmosphericCorrectionFactor());
    }

}
