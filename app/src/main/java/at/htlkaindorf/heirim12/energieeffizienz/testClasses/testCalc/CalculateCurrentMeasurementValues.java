package at.htlkaindorf.heirim12.energieeffizienz.testClasses.testCalc;

/**
 * Created by richard on 11.10.2016.
 */
import at.htlkaindorf.heirim12.energieeffizienz.testClasses.data.*;

public class CalculateCurrentMeasurementValues
{
    private CurrentMeasurementValues currentMeasurementValues;

    public CalculateCurrentMeasurementValues()
    {
        calculate();
    }

    private void calculate ()
    {
        //=> Math.random: Generates a double number between 10 and 13
        //=> Math.round: rounds the number to two decimals
        double panel1Volt = (double) Math.round( (Math.random()*3 + 10) * 100) / 100.0;
        double panel1Ampere = (double) Math.round( (Math.random()*3 + 10) * 100) / 100.0;
        double panel1Watt = (double) Math.round((panel1Volt * panel1Ampere) * 100) / 100.0;
        double panel1Angle = (double) Math.round( (Math.random()*90) *100) / 100.0;
        //Random rand;
        int panel1OrientationInt = 1;// rand.nextInt((1-0)+1);

        double panel2Volt = (double) Math.round( (Math.random()*3 + 10) * 100) / 100.0;
        double panel2Ampere  = (double) Math.round( (Math.random()*3 + 10) * 100) / 100.0;
        double panel2Watt = (double) Math.round((panel2Volt * panel2Ampere) * 100) / 100.0;

        double enginesVolt = (double) Math.round( (Math.random()*3 + 10) * 100) / 100.0;
        double enginesAmpere = (double) Math.round( (Math.random()*3 ) * 100) / 100.0;
        double enginesWatt = (double) Math.round((enginesAmpere * enginesAmpere) * 100) / 100.0;

        double batteryVolt = (double) Math.round( (Math.random()*3 + 10) * 100) / 100.0;

        String panel1OrientationString;
        if (panel1OrientationInt == 1)
            panel1OrientationString = "Süd-Ost";
        else
            panel1OrientationString = "Nord-West";

//        currentMeasurementValues = new CurrentMeasurementValues(panel1Volt,panel1Ampere,panel1Watt,
//                panel1Angle,panel1OrientationString,panel2Volt,panel2Ampere,panel2Watt,enginesVolt,
//                enginesAmpere,enginesWatt,batteryVolt);
    }

    public CurrentMeasurementValues getCurrentMeasurementValues() {
        return currentMeasurementValues;
    }

    public static void main(String[] args) {
        //TODO

    }
}
