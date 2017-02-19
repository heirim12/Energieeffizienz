package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.diagramFormatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by richard on 18.02.2017.
 */

public class YAxisUnitValueFormatter implements IAxisValueFormatter
{
  private final String unit;

  public YAxisUnitValueFormatter(String unit)
  {
    this.unit = unit;
  }

  @Override
  public String getFormattedValue(float value, AxisBase axis)
  {
    return String.format("%.1f%s", value, unit);
  }

  @Override
  public int getDecimalDigits()
  {
    return 0;
  }
}
