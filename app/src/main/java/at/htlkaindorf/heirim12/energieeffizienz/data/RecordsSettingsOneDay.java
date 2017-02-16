package at.htlkaindorf.heirim12.energieeffizienz.data;

import java.util.Calendar;
import java.util.Objects;

/**
 * Created by richard on 16.02.2017.
 */

public class RecordsSettingsOneDay
{
  private final Calendar date;

  private final boolean
          bothPower,
          panel1Voltage, panel1Current, panel1Power,
          panel2Voltage, panel2Current, panel2Power;

  public RecordsSettingsOneDay(Calendar date, boolean bothPower,
                               boolean panel1Voltage, boolean panel1Current, boolean panel1Power,
                               boolean panel2Voltage, boolean panel2Current, boolean panel2Power)
  {
    this.date = date;
    this.bothPower = bothPower;
    this.panel1Voltage = panel1Voltage;
    this.panel1Current = panel1Current;
    this.panel1Power = panel1Power;
    this.panel2Voltage = panel2Voltage;
    this.panel2Current = panel2Current;
    this.panel2Power = panel2Power;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(getDate(),
            isBothPower(),
            isPanel1Voltage(), isPanel1Current(), isPanel1Power(),
            isPanel2Voltage(), isPanel2Current(), isPanel2Power());
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof RecordsSettingsOneDay)) return false;
    RecordsSettingsOneDay that = (RecordsSettingsOneDay) o;
    return getDate().getTimeInMillis() == that.getDate().getTimeInMillis() &&
            isBothPower() == that.isBothPower() &&
            isPanel1Voltage() == that.isPanel1Voltage() &&
            isPanel1Current() == that.isPanel1Current() &&
            isPanel1Power() == that.isPanel1Power() &&
            isPanel2Voltage() == that.isPanel2Voltage() &&
            isPanel2Current() == that.isPanel2Current() &&
            isPanel2Power() == that.isPanel2Power();
  }

  public Calendar getDate()
  {
    return date;
  }

  public boolean isBothPower()
  {
    return bothPower;
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
}
