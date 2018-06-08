public class ACF {

    static final double REFERENCE_TEMPERATURE = 20;         // °C
    static final double REFERENCE_PRESSURE = 1013;          // hPa
    static final double REFERENCE_ABSOLUTE_HUMIDITY = 11;   // g/m^3

    public enum voltageType {
        DC {
            @Override
            public String toString() {
                return "DC";
            }
        },
        AC {
            @Override
            public String toString() {
                return "AC";
            }
        },
        IMPULSE {
            @Override
            public String toString() {
                return "Udarowe";
            }
        },
    }

    private double temperature;
    private double pressure;
    private double absoluteHumidity;
    private voltageType vType;
    private double v50;
    private double minimumDischargePath;

    public ACF() {
        temperature = REFERENCE_TEMPERATURE;
        pressure = REFERENCE_PRESSURE;
        absoluteHumidity = REFERENCE_ABSOLUTE_HUMIDITY;
        vType = voltageType.DC;
        v50 = 100.0;
        minimumDischargePath = 0.1;
    }

    public void setTemperature(double temperature) throws IllegalArgumentException {
        if (temperature >= -273.15) {
            this.temperature = temperature;
        } else {
            throw new IllegalArgumentException("Air can't be colder than -273.15 °C");
        }
    }

    public double getTemperature() {
        return temperature;
    }

    public void setPressure(double pressure) throws IllegalArgumentException {
        if (pressure >= 0.0) {
            this.pressure = pressure;
        } else {
            throw new IllegalArgumentException("Pressure can't be negative");
        }
    }

    public double getPressure() {
        return pressure;
    }

    public void setAbsoluteHumidity(double absoluteHumidity) throws IllegalArgumentException {
        if (absoluteHumidity >= 0) {
            this.absoluteHumidity = absoluteHumidity;
        } else {
            throw new IllegalArgumentException("Absolute air humidity can't be negative");
        }
    }

    public double getAbsoluteHumidity() {
        return absoluteHumidity;
    }

    public void setRelativeHumidity(double relativeHumidity) throws IllegalArgumentException {
        if (relativeHumidity >= 0.0 && relativeHumidity <= 100.0) {
            absoluteHumidity = (6.11 * relativeHumidity * Math.exp((17.6 * temperature) / (243.0 + temperature))) / (0.4615 * (273.0 + temperature));
        } else {
            throw new IllegalArgumentException("Relative air humidity can't be outside range of 0% - 100%");
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

    public void setV50(double v50) throws IllegalArgumentException {
        if (v50 >= 0) {
            this.v50 = v50;
        } else {
            throw new IllegalArgumentException("This voltage must be positive");
        }
    }

    public double getV50() {
        return v50;
    }

    public void setMinimumDischargePath(double minimumDischargePath) throws IllegalArgumentException {
        if (minimumDischargePath >= 0) {
            this.minimumDischargePath = minimumDischargePath;
        } else {
            throw new IllegalArgumentException("Distance must be positive");
        }
    }

    public double getMinimumDischargePath() {
        return minimumDischargePath;
    }



    //--------------------------------------------------



    private double getRelativeAirDensity() {
        return ((pressure) / (REFERENCE_PRESSURE)) * ((273.0 + REFERENCE_TEMPERATURE) / (273.0 + temperature));
    }

    private double getKFactor() {
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

    private double getGFactor() {
        double relativeAirDensity = getRelativeAirDensity();
        double k = getKFactor();

        return (v50) / (500.0 * minimumDischargePath * relativeAirDensity * k);
    }

    private double getMFactor() {
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

    private double getWFactor() {
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

    private double getAirDensityCorrectionFactor() {
        double relativeAirDensity = getRelativeAirDensity();
        double mFactor = getMFactor();

        return Math.pow(relativeAirDensity, mFactor);
    }

    private double getHumidityCorrectionFactor() {
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
