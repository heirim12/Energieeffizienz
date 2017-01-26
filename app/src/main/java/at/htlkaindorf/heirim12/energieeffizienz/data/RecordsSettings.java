package at.htlkaindorf.heirim12.energieeffizienz.data;

import java.util.Calendar;
import java.util.Objects;

/**
 * Created by richard on 28.12.2016.
 */

public class RecordsSettings
{
  private final Calendar startDate, endDate;

  private final boolean
          bothPower, bothEnergy,
          panel1Voltage, panel1Current, panel1Power, panel1Energy,
          panel2Voltage, panel2Current, panel2Power, panel2Energy;

  public RecordsSettings(Calendar startDate, Calendar endDate,
                         boolean bothPower, boolean bothEnergy,
                         boolean panel1Voltage, boolean panel1Current,
                         boolean panel1Power, boolean panel1Energy,
                         boolean panel2Voltage, boolean panel2Current,
                         boolean panel2Power, boolean panel2Energy)
  {
    this.startDate = startDate;
    this.endDate = endDate;
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

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof RecordsSettings)) return false;
    RecordsSettings that = (RecordsSettings) o;
    return getStartDate().getTimeInMillis() == that.getStartDate().getTimeInMillis() &&
            getEndDate().getTimeInMillis() == that.getEndDate().getTimeInMillis() &&
            isBothPower() == that.isBothPower() &&
            isBothEnergy() == that.isBothEnergy() &&
            isPanel1Voltage() == that.isPanel1Voltage() &&
            isPanel1Current() == that.isPanel1Current() &&
            isPanel1Power() == that.isPanel1Power() &&
            isPanel1Energy() == that.isPanel1Energy() &&
            isPanel2Voltage() == that.isPanel2Voltage() &&
            isPanel2Current() == that.isPanel2Current() &&
            isPanel2Power() == that.isPanel2Power() &&
            isPanel2Energy() == that.isPanel2Energy();
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(getStartDate(), getEndDate(),
            isBothPower(), isBothEnergy(),
            isPanel1Voltage(), isPanel1Current(), isPanel1Power(), isPanel1Energy(),
            isPanel2Voltage(), isPanel2Current(), isPanel2Power(), isPanel2Energy());
  }

  public Calendar getStartDate()
  {
    return startDate;
  }

  public Calendar getEndDate()
  {
    return endDate;
  }

  public boolean isBothPower()
  {
    return bothPower;
  }

  public boolean isBothEnergy()
  {
    return bothEnergy;
  }

  public boolean isPanel1Voltage()
  {
    return panel1Voltage;
  }

  public boolean isPanel1Current()
  {
    return panel1Current;
  }

  public boolean isPanel1Power()
  {
    return panel1Power;
  }

  public boolean isPanel1Energy()
  {
    return panel1Energy;
  }

  public boolean isPanel2Voltage()
  {
    return panel2Voltage;
  }

  public boolean isPanel2Current()
  {
    return panel2Current;
  }

  public boolean isPanel2Power()
  {
    return panel2Power;
  }

  public boolean isPanel2Energy()
  {
    return panel2Energy;
  }
}
