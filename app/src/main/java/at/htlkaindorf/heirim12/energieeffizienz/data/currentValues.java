package at.htlkaindorf.heirim12.energieeffizienz.data;

/**
 * Created by richard on 11.10.2016.
 */

public class CurrentValues
{
    private final double panel1Voltage;
    private final double panel1Current;
    private final double panel1Power;
    private final int panel1Azimuth;
    private final int panel1Elevation;

    private final double panel2Voltage;
    private final double panel2Current;
    private final double panel2Power;
    private final int panel2Azimuth;
    private final int panel2Elevation;

    private final double accuVoltage;

    public CurrentValues(double panel1Voltage, double panel1Current, double panel1Power,
                         int panel1Azimuth, int panel1Elevation,
                         double panel2Voltage, double panel2Current, double panel2Power,
                         double accuVoltage)
    {
        this.panel1Voltage = panel1Voltage;
        this.panel1Current = panel1Current;
        this.panel1Power = panel1Power;
        this.panel1Azimuth = panel1Azimuth;
        this.panel1Elevation = panel1Elevation;
        this.panel2Voltage = panel2Voltage;
        this.panel2Current = panel2Current;
        this.panel2Power = panel2Power;
        this.panel2Azimuth = 30;
        this.panel2Elevation = 30;
        this.accuVoltage = accuVoltage;
    }

    public double getPanel1Voltage()
    {
        return panel1Voltage;
    }

    public double getPanel1Current()
    {
        return panel1Current;
    }

    public double getPanel1Power()
    {
        return panel1Power;
    }

    public int getPanel1Azimuth()
    {
        return panel1Azimuth;
    }

    public int getPanel1Elevation()
    {
        return panel1Elevation;
    }

    public double getPanel2Voltage()
    {
        return panel2Voltage;
    }

    public double getPanel2Current()
    {
        return panel2Current;
    }

    public double getPanel2Power()
    {
        return panel2Power;
    }

    public int getPanel2Azimuth()
    {
        return panel2Azimuth;
    }

    public int getPanel2Elevation()
    {
        return panel2Elevation;
    }

    public double getAccuVoltage()
    {
        return accuVoltage;
    }
}
