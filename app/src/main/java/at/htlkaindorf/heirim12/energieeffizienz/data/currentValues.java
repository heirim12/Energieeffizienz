package at.htlkaindorf.heirim12.energieeffizienz.data;

/**
 * Created by richard on 11.10.2016.
 */

public class CurrentValues
{
    private final float panel1Voltage;
    private final float panel1Current;
    private final float panel1Power;
    private final int panel1Azimuth;
    private final int panel1Elevation;

    private final float panel2Voltage;
    private final float panel2Current;
    private final float panel2Power;
    private final int panel2Azimuth;
    private final int panel2Elevation;

    private final float accuVoltage;

    private final Exception exception;

    public CurrentValues(float panel1Voltage, float panel1Current, float panel1Power,
                         int panel1Azimuth, int panel1Elevation,
                         float panel2Voltage, float panel2Current, float panel2Power,
                         float accuVoltage)
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
        this.exception = null;
    }

    public CurrentValues(Exception exception)
    {
        this.panel1Voltage = Float.NaN;
        this.panel1Current = Float.NaN;
        this.panel1Power = Float.NaN;
        this.panel1Azimuth = (int) Float.NaN;
        this.panel1Elevation = (int) Float.NaN;
        this.panel2Voltage = Float.NaN;
        this.panel2Current = Float.NaN;
        this.panel2Power = Float.NaN;
        this.panel2Azimuth = (int) Float.NaN;
        this.panel2Elevation = (int) Float.NaN;
        this.accuVoltage = Float.NaN;
        this.exception = exception;
    }

    public float getPanel1Voltage()
    {
        return panel1Voltage;
    }

    public float getPanel1Current()
    {
        return panel1Current;
    }

    public float getPanel1Power()
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

    public float getPanel2Voltage()
    {
        return panel2Voltage;
    }

    public float getPanel2Current()
    {
        return panel2Current;
    }

    public float getPanel2Power()
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

    public float getAccuVoltage()
    {
        return accuVoltage;
    }

    public Exception getException()
    {
        return exception;
    }
}
