package at.htlkaindorf.heirim12.energieeffizienz.data;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by richard on 29.12.2016.
 */

public class Records
{
  private final List<Record> records = new ArrayList<>();

  public Records()
  {
//    records.add(new Record(new GregorianCalendar(2016,10,1,12,20), 1000, 100,
//            12.05, 3.5, 700, 70, 13, 2, 300, 30));
//
//    records.add(new Record(new GregorianCalendar(2016,10,2,12,20), 2000, 110,
//            12.7, 5, 700, 70, 11, 1, 1300, 40));
//
//    records.add(new Record(new GregorianCalendar(2016,10,3,12,20), 3000, 120,
//            13, 1, 2000, 100, 10, 4, 1000, 20));
//
//    records.add(new Record(new GregorianCalendar(2016,10,6,12,20), 4000, 130,
//            11, 3.5, 3000, 60, 12.7, 2.8, 1000, 70));
//
//    records.add(new Record(new GregorianCalendar(2016,10,7,12,20), 5000, 140,
//            13, 4, 1300, 100, 13, 2, 3700, 40));
//
//    records.add(new Record(new GregorianCalendar(2016,10,8,12,20), 1000, 100,
//            12.05, 3.5, 700, 70, 13, 2, 300, 30));
//
//    records.add(new Record(new GregorianCalendar(2016,10,9,12,20), 2000, 110,
//            12.7, 5, 700, 70, 11, 1, 1300, 40));
//
//    records.add(new Record(new GregorianCalendar(2016,10,10,12,20), 3000, 120,
//            13, 1, 2000, 100, 10, 4, 1000, 20));
//
//    records.add(new Record(new GregorianCalendar(2016,10,11,12,20), 4000, 130,
//            11, 3.5, 3000, 60, 12.7, 2.8, 1000, 70));
//
//    records.add(new Record(new GregorianCalendar(2016,10,12,12,20), 5000, 140,
//            13, 4, 1300, 100, 13, 2, 3700, 40));
//
//    records.add(new Record(new GregorianCalendar(2016,10,13,12,20), 1000, 100,
//            12.05, 3.5, 700, 70, 13, 2, 300, 30));
//
//    records.add(new Record(new GregorianCalendar(2016,10,14,12,20), 2000, 110,
//            12.7, 5, 700, 70, 11, 1, 1300, 40));
//
//    records.add(new Record(new GregorianCalendar(2016,10,15,12,20), 3000, 120,
//            13, 1, 2000, 100, 10, 4, 1000, 20));
//
//    records.add(new Record(new GregorianCalendar(2016,10,16,12,20), 4000, 130,
//            11, 3.5, 3000, 60, 12.7, 2.8, 1000, 70));
//
//    records.add(new Record(new GregorianCalendar(2016,10,17,12,20), 5000, 140,
//            13, 4, 1300, 100, 13, 2, 3700, 40));
  }

  public int getSize()
  {
    return records.size();
  }

  public boolean add (Record e)
  {
    return records.add(e);
  }

  public Record get (int index)
  {
    return records.get(index);
  }

  public void set (int index, Record record)
  {
    records.set(index,record);
  }

  public Record remove (int index)
  {
    return records.remove(index);
  }

  public List<Record> getRecords()
  {
    return records;
  }
}
