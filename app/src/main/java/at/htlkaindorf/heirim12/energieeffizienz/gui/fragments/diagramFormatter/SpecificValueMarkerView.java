package at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.diagramFormatter;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import at.htlkaindorf.heirim12.energieeffizienz.R;

/**
 * Created by richard on 18.02.2017.
 */

public class SpecificValueMarkerView extends MarkerView implements IMarker
{
  private final long referenceTime;
  private final long middleTimeStep;
  private final TextView markerViewText;
  private final SimpleDateFormat dateFormat;
  private final String leftUnit;
  private final String rightUnit;
  private Boolean diagramSide; // false = left, true = right
  private MPPointF mOffset;

  public SpecificValueMarkerView(Context context, int layoutResource, long referenceTime,
                                 long lastDateInMillis, String leftUnit, String rightUnit)
  {
    super(context, layoutResource);
    markerViewText = (TextView) findViewById(R.id.test_MarkerViewTextView);
    this.referenceTime = referenceTime;
    this.middleTimeStep = ((lastDateInMillis / 1000) - referenceTime) / 2;
    this.dateFormat = new SimpleDateFormat("HH:mm; dd.MM.yyyy");
    this.leftUnit = leftUnit;
    this.rightUnit = rightUnit;
    this.diagramSide = false;
  }

  @Override
  public void refreshContent(Entry e, Highlight highlight)
  {
    if (e.getX() > middleTimeStep)
      diagramSide = true;
    else
      diagramSide = false;

    String text;
    if (highlight.getAxis() == YAxis.AxisDependency.LEFT)
      text = String.format("%.2f%s %s\n%s", e.getY(), leftUnit,
              getContext().getString(R.string.fragment_diagram_marker_view_at),
              dateFormat.format(new Date((referenceTime + (long) e.getX()) * 1000)));
    else
      text = String.format("%.2f%s %s\n%s", e.getY(), rightUnit,
              getContext().getString(R.string.fragment_diagram_marker_view_at),
              dateFormat.format(new Date((referenceTime + (long) e.getX()) * 1000)));

    markerViewText.setText(text);
    super.refreshContent(e, highlight);
  }

  @Override
  public MPPointF getOffset()
  {
    if (diagramSide)
      mOffset = new MPPointF(-getWidth(), -getHeight());
    else
      mOffset = new MPPointF(0, -getHeight());
    return mOffset;
  }
}