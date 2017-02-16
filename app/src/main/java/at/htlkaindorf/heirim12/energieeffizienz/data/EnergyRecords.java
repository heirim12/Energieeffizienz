package at.htlkaindorf.heirim12.energieeffizienz.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 16.02.2017.
 */

public class EnergyRecords
{
  private final List<EnergyRecord> records = new ArrayList<>();

  public EnergyRecords()
  {
  }

  public int getSize()
  {
    return records.size();
  }

  public boolean add (EnergyRecord e)
  {
    return records.add(e);
  }

  public EnergyRecord get (int index)
  {
    return records.get(index);
  }

  public void set (int index, EnergyRecord record)
  {
    records.set(index,record);
  }

  public EnergyRecord remove (int index)
  {
    return records.remove(index);
  }

  public List<EnergyRecord> getRecords()
  {
    return records;
  }
}
