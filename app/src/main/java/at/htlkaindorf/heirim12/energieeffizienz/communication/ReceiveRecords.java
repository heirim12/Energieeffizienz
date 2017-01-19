package at.htlkaindorf.heirim12.energieeffizienz.communication;

import at.htlkaindorf.heirim12.energieeffizienz.data.Records;
import at.htlkaindorf.heirim12.energieeffizienz.data.RecordsSettings;

/**
 * Created by richard on 07.01.2017.
 */

public class ReceiveRecords
{
  private final RecordsSettings recordsSettings;
  private Records records = null;

  public ReceiveRecords(RecordsSettings recordsSettings) throws InterruptedException
  {
    Thread.sleep(3000);
    this.recordsSettings = recordsSettings;
    records = new Records();
  }

  public Records getRecords()
  {
    return records;
  }
}
