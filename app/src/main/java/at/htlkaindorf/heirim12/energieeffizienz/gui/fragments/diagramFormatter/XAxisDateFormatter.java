package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.diagramFormatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by richard on 18.02.2017.
 */

public class XAxisDateFormatter implements IAxisValueFormatter
{
  private final Calendar firstDate = new GregorianCalendar();
  private final Calendar lastDate = new GregorianCalendar();
  private final long referenceTimeInSeconds;
  private final SimpleDateFormat dateFormat;

  public XAxisDateFormatter(long firstDate, long lastDate, long referenceTimeInSeconds)
  {
    this.firstDate.setTimeInMillis(firstDate);
    this.lastDate.setTimeInMillis(lastDate);
    this.referenceTimeInSeconds = referenceTimeInSeconds;
    this.dateFormat = getDateFormat();
  }

  private SimpleDateFormat getDateFormat()
  {
    if ((lastDate.get(Calendar.YEAR) - firstDate.get(Calendar.YEAR)) < 1)
    {
      if ((lastDate.get(Calendar.MONTH) - firstDate.get(Calendar.MONTH)) < 1)
      {
        if ((lastDate.get(Calendar.DAY_OF_MONTH) - firstDate.get(Calendar.DAY_OF_MONTH)) < 1)
        {
          return new SimpleDateFormat("H:mm");
        } else
        {
          return new SimpleDateFormat("d.MMM");
        }
      } else
      {
        return new SimpleDateFormat("d.MMM");
      }
    } else
    {
      return new SimpleDateFormat("dd.MM.yy");
    }
  }

  @Override
  public String getFormattedValue(float value, AxisBase axis)
  {
    return dateFormat.format(new Date((referenceTimeInSeconds + (long) value) * 1000));
  }

  @Override
  public int getDecimalDigits()
  {
    return 0;
  }
}