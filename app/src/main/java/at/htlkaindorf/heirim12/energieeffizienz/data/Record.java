package at.htlkaindorf.heirim12.energieeffizienz.data;

/**
 * Created by richard on 29.12.2016.
 */

public class Record
{
  private final long dateTimeInMillis;
  //private final Calendar dateTime;
  private final float
                        bothPower, bothEnergy,
                        panel1Voltage, panel1Current, panel1Power, panel1Energy,
                        panel2Voltage, panel2Current, panel2Power, panel2Energy;


  public Record(long dateTimeInMillis, float bothPower, float bothEnergy,
                float panel1Voltage, float panel1Current, float panel1Power, float panel1Energy,
                float panel2Voltage, float panel2Current, float panel2Power, float panel2Energy)
  {
//    this.dateTime = dateTime;
    this.dateTimeInMillis = dateTimeInMillis;
    this.bothPower = bothPower;
    this.bothEnergy = bothEnergy;
    this.panel1Voltage = panel1Voltage;
    this.panel1Current = panel1Current;
    this.panel1Power = panel1Power;
    this.panel1Energy = panel1Energy;
    this.panel2Voltage = panel2Voltage;
    this.panel2Current = panel2Current;
    this.panel2Power = panel2Power;
    this.panel2Energy = panel2Energy;
  }

  public long getDateTimeInMillis()
  {
    return dateTimeInMillis;
  }

  public float getBothPower()
  {
    return bothPower;
  }

  public float getBothEnergy()
  {
    return bothEnergy;
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

  public float getPanel1Energy()
  {
    return panel1Energy;
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

  public float getPanel2Energy()
  {
    return panel2Energy;
  }
}
