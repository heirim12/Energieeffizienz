package at.htlkaindorf.heirim12.energieeffizienz.testClasses.data;

/**
 * Created by richard on 11.10.2016.
 */

public class CurrentMeasurementValues
{
    private final double panel1Volt;
    private final double panel1Ampere;
    private final double panel1Watt;
    private final double panel1Angle;
    private final String panel1Orientation;

    private final double panel2Volt;
    private final double panel2Ampere;
    private final double panel2Watt;

    private final double enginesVolt;
    private final double enginesAmpere;
    private final double enginesWatt;

    private final double batteryVolt;

    public CurrentMeasurementValues(double panel1Volt, double panel1Ampere, double panel1Watt,
                                    double panel1Angle, String panel1Orientation,
                                    double panel2Volt, double panel2Ampere, double panel2Watt,
                                    double enginesVolt, double enginesAmpere, double enginesWatt,
                                    double batteryVolt)
    {
        this.panel1Volt = panel1Volt;
        this.panel1Ampere = panel1Ampere;
        this.panel1Watt = panel1Watt;
        this.panel1Angle = panel1Angle;
        this.panel1Orientation = panel1Orientation;
        this.panel2Volt = panel2Volt;
        this.panel2Ampere = panel2Ampere;
        this.panel2Watt = panel2Watt;
        this.enginesVolt = enginesVolt;
        this.enginesAmpere = enginesAmpere;
        this.enginesWatt = enginesWatt;
        this.batteryVolt = batteryVolt;
    }

    public double getBatteryVolt() {
        return batteryVolt;
    }

    public double getPanel1Volt() {
        return panel1Volt;
    }

    public double getPanel1Ampere() {
        return panel1Ampere;
    }

    public double getPanel1Watt() {
        return panel1Watt;
    }

    public double getPanel1Angle() {
        return panel1Angle;
    }

    public String getPanel1Orientation() {
        return panel1Orientation;
    }

    public double getPanel2Volt() {
        return panel2Volt;
    }

    public double getPanel2Ampere() {
        return panel2Ampere;
    }

    public double getPanel2Watt() {
        return panel2Watt;
    }

    public double getEnginesVolt() {
        return enginesVolt;
    }

    public double getEnginesAmpere() {
        return enginesAmpere;
    }

    public double getEnginesWatt() {
        return enginesWatt;
    }
}
