/**
 *
 */

public class AtmosphericCorrectionFactor {

    /**
     * Global constants used in calculations
     */
    static final double REFERENCE_TEMPERATURE = 20;         // °C
    static final double REFERENCE_PRESSURE = 101.3;         // kPa
    static final double REFERENCE_ABSOLUTE_HUMIDITY = 11;   // g/m^3

    /**
     * Enum defining 3 types of voltage used in calculations
     */
    public enum voltageType {
        DC,
        AC,
        IMPULSE,
    }

    /**
     * Private variables
     */
    private double temperature;
    private double pressure;
    private double absoluteHumidity;
    private voltageType vType;
    private double v50;
    private double minimumDischargePath;

    /**
     * Constructor class for high voltage atmospheric correction factor calculator
     */
    public AtmosphericCorrectionFactor() {
        temperature = REFERENCE_TEMPERATURE;
        pressure = REFERENCE_PRESSURE;
        absoluteHumidity = REFERENCE_ABSOLUTE_HUMIDITY;
        vType = voltageType.DC;
        v50 = 1.0;
        minimumDischargePath = 1.0;
    }

    /**
     * Sets an air temperature used for calculations
     * @param temperature air temperature in °C, must be above -273.15 °C
     */
    public void setTemperature(double temperature) {
        if (temperature >= -273.15) {
            this.temperature = temperature;
        } else {
            throw new ArithmeticException("Air can't be colder than -273.15 °C");
        }
    }

    /**
     * Returns air temperature used for calculations
     * @return temperature in °C
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Sets an air pressure used for calculations
     * @param pressure air pressure in kPa, must be greater or equal than 0 kPa
     */
    public void setPressure(double pressure) throws ArithmeticException {
        if (pressure >= 0.0) {
            this.pressure = pressure;
        } else {
            throw new ArithmeticException("Pressure can't be negative");
        }
    }

    /**
     * Returns pressure used for calculations
     * @return temperature in kPa
     */
    public double getPressure() {
        return pressure;
    }

    /**
     * Sets an absolute air humidity used for calculations
     * @param absoluteHumidity absolute air humidity in g/m^3, must be greater or equal than 0 g/m^3
     */
    public void setAbsoluteHumidity(double absoluteHumidity) {
        if (absoluteHumidity >= 0) {
            this.absoluteHumidity = absoluteHumidity;
        } else {
            throw new ArithmeticException("Absolute air humidity can't be negative");
        }

    }

    /**
     * Returns absolute air humidity used for calculations
     * @return absolute air humidity in g/m^3
     */
    public double getAbsoluteHumidity() {
        return absoluteHumidity;
    }

    public void setRelativeHumidity(double relativeHumidity) {
        if (relativeHumidity <= 0.0 && relativeHumidity >= 100.0) { // relative humidity must be in range from 0% to 100%
            absoluteHumidity = (6.11 * relativeHumidity * Math.exp((17.6 * temperature) / (243.0 + temperature))) / (0.4615 * (273.0 + temperature));
        } else {
            throw new ArithmeticException("Relative air humidity can't be outside range of 0% - 100%");
        }
    }

    public double getRelativeHumidity() {
        return (0.4615 * absoluteHumidity * (273.0 + temperature)) / (6.11 * Math.exp((17.6 * temperature) / (243.0 + temperature)));
    }

    public void setVoltageType(voltageType vType) {
        this.vType = vType;
    }

    public voltageType getVoltageType() {
        return vType;
    }

    public void setV50(double v50) {
        this.v50 = Math.abs(v50); // this voltage value must be positive
    }

    public double getV50() {
        return v50;
    }

    public void setMinimumDischargePath(double minimumDischargePath) {
        this.minimumDischargePath = Math.abs(minimumDischargePath); // length must be positive
    }

    public double getMinimumDischargePath() {
        return minimumDischargePath;
    }



    public double getRelativeAirDensity() {
        return ((pressure) / (REFERENCE_PRESSURE)) * ((273.0 + REFERENCE_TEMPERATURE) / (273.0 + temperature));
    }

    public double getKFactor() {
        double k = 1;
        double absoluteHumidity = getAbsoluteHumidity();
        double relativeAirDensity = getRelativeAirDensity();

        switch (vType) {
            case DC:
                k = 1 + (0.014 * ((absoluteHumidity / relativeAirDensity) - 11.0)) - (0.00022 * Math.pow(((absoluteHumidity / relativeAirDensity) - 11.0), 2));
                break;

            case AC:
                k = 1 + 0.012 * ((absoluteHumidity / relativeAirDensity) - 11.0);
                break;

            case IMPULSE:
                k = 1 + 0.010 * ((absoluteHumidity / relativeAirDensity) - 11.0);
                break;
        }

        return k;
    }

    public double getGFactor() {
        double relativeAirDensity = getRelativeAirDensity();
        double k = getKFactor();

        return (v50) / (500.0 * minimumDischargePath * relativeAirDensity * k);
    }

    public double getMFactor() {
        double m;
        double g = getGFactor();

        if (g < 0.2) {
            m = 0.0;
        } else if (g < 1.0) {
            m = g * (g - 0.2) / 0.8;
        } else {
            m = 1.0;
        }

        return m;
    }

    public double getWFactor() {
        double w;
        double g = getGFactor();

        if (g < 0.2) {
            w = 0.0;
        } else if (g < 1.0) {
            w = g * (g - 0.2) / 0.8;
        } else if (g < 1.2) {
            w = 1;
        } else if (g < 2.0) {
            w = (2.2 - g) * (2.0 - g) / 0.8;
        } else {
            w = 0.0;
        }

        return w;
    }

    public double getAirDensityCorrectionFactor() {
        double relativeAirDensity = getRelativeAirDensity();
        double mFactor = getMFactor();

        return Math.pow(relativeAirDensity, mFactor);
    }

    public double getHumidityCorrectionFactor() {
        double kFactor = getKFactor();
        double wFactor = getWFactor();

        return Math.pow(kFactor, wFactor);
    }

    public double getAtmosphericCorrectionFactor() {
        double airDensityCorrectionFactor = getAirDensityCorrectionFactor();
        double humidityCorrectionFactor = getHumidityCorrectionFactor();

        return airDensityCorrectionFactor * humidityCorrectionFactor;
    }

}
