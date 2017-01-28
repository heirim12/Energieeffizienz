package at.htlkaindorf.heirim12.energieeffizienz.testClasses;

import java.sql.SQLOutput;

/**
 * Created by richard on 26.01.2017.
 */

public class Test
{
  public static void main(String[] args)
  {
    double test = Double.NaN;

test =3;
    try
  {
    if (Double.isNaN(test))
      System.out.println("Juhuhuh");

    System.out.println(test);
  }
  catch (Exception ex)
  {
    System.out.println(ex.getLocalizedMessage());
  }



  }
}
