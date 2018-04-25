package com.polprzewodnikowy.hvc;

public class AtmosphericCorrectionFactor {

    static final double REFERENCE_TEMPERATURE = 20;         // °C
    static final double REFERENCE_PRESSURE = 101.3;         // kPa
    static final double REFERENCE_ABSOLUTE_HUMIDITY = 11;   // g/m^3

    public enum voltageType {
        DC,
        AC,
        IMPULSE,
    }

    private double temperature;             // °C
    private double pressure;                // kPa
    private double absoluteHumidity;        // g/m^3
    private voltageType vType;              // DC / AC / IMPULSE
    private double v50;                     // kV
    private double minimumDischargePath;    // m
    private double relativeHumidity;        // %

    public AtmosphericCorrectionFactor() {
        temperature = REFERENCE_TEMPERATURE;
        pressure = REFERENCE_PRESSURE;
        setAbsoluteHumidity(REFERENCE_ABSOLUTE_HUMIDITY);
        vType = voltageType.DC;
        v50 = 20.0;
        minimumDischargePath = 1.0;
    }

    public void setTemperature(double temperature) {
        if(temperature >= -273.15) {
            this.temperature = temperature;
            setAbsoluteHumidity(absoluteHumidity);
        }
    }

    public double getTemperature() {
        return temperature;
    }

    public void setPressure(double pressure) {
        if(pressure >= 0.0) {
            this.pressure = pressure;
        }
    }

    public double getPressure() {
        return pressure;
    }

    public void setAbsoluteHumidity(double absoluteHumidity) {
        this.absoluteHumidity = absoluteHumidity;
        relativeHumidity = (0.4615 * absoluteHumidity * (273 + temperature)) / (6.11 * Math.exp((17.6 * temperature) / (243 + temperature)));
    }

    public double getAbsoluteHumidity() {
        return absoluteHumidity;
    }

    public void setRelativeHumidity(double relativeHumidity) {
        absoluteHumidity = (6.11 * relativeHumidity * Math.exp((17.6 * temperature) / (243 + temperature))) / (0.4615 * (273 + temperature));
        this.relativeHumidity = relativeHumidity;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setVoltageType(voltageType vType) {
        this.vType = vType;
    }

    public voltageType getVoltageType() {
        return vType;
    }

    public void setV50(double v50) {
        if(v50 >= 0.0) {
            this.v50 = v50;
        }
    }

    public double getV50() {
        return v50;
    }

    public void setMinimumDischargePath(double minimumDischargePath) {
        if(minimumDischargePath >= 0) {
            this.minimumDischargePath = minimumDischargePath;
        }
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
                k += (0.014 * ((absoluteHumidity / relativeAirDensity) - 11.0)) - (0.00022  * Math.pow(((absoluteHumidity / relativeAirDensity) - 11.0), 2));
                break;

            case AC:
                k += 0.012 * ((absoluteHumidity / relativeAirDensity) - 11.0);
                break;

            case IMPULSE:
                k += 0.010 * ((absoluteHumidity / relativeAirDensity) - 11.0);
                break;

            default:
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

        if(g < 0.2) {
            m = 0.0;
        } else if(g < 1.0) {
            m = g * (g - 0.2) / 0.8;
        } else {
            m = 1.0;
        }

        return m;
    }

    public double getWFactor() {
        double w;
        double g = getGFactor();

        if(g < 0.2) {
            w = 0.0;
        } else if(g < 1.0) {
            w = g * (g - 0.2) / 0.8;
        } else if(g < 1.2) {
            w = 1;
        } else if(g < 2.0) {
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
