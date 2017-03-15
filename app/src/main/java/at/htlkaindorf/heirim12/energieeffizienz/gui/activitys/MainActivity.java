package at.htlkaindorf.heirim12.energieeffizienz.gui.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.GregorianCalendar;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.database.PhotovoltaicDatabase;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentCurrentMeasurement;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentDiagramEnergy;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentDiagramOneDay;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentHome;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentInfoSystem;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentTable;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
  private NavigationView navigationView;
  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle actionBarDrawerToggle;

  //================================================================================================
  // Helping Methods
  //================================================================================================
  private void showSnackbar(String text)
  {
    Snackbar.make(findViewById(android.R.id.content),
            text, Snackbar.LENGTH_LONG).show();
  }

  //Opens a AlertDialog and shows an about-page
  private void showAppInfo()
  {
    final LinearLayout aboutLayout =
            (LinearLayout) this.getLayoutInflater().inflate(R.layout.dialog_about, null);
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.main_activity_alert_about)
            .setView(aboutLayout)
            .setPositiveButton(R.string.main_activity_alert_close, null);
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  //Replaces the old fragment with the new
  private void loadFragment(Fragment fragment)
  {
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final FragmentTransaction transaction  = fragmentManager.beginTransaction();
    transaction.replace(R.id.main_activity_fragmanetholder, fragment);
    transaction.commit();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if (item.getItemId() == android.R.id.home)
    {
      if (drawerLayout.isDrawerOpen(navigationView))
        drawerLayout.closeDrawer(navigationView);
      else
        drawerLayout.openDrawer(navigationView);
    }

    return super.onOptionsItemSelected(item);
  }

  public boolean onNavigationItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {

      case R.id.nav_home:
        loadFragment(new FragmentHome());
        break;

      case R.id.nav_current_measurement:
        loadFragment(new FragmentCurrentMeasurement());
        break;

      case R.id.nav_records_table:
        loadFragment(new FragmentTable());
        break;

      case R.id.nav_records_diagram_energy:
        loadFragment(new FragmentDiagramEnergy());
        break;

      case R.id.nav_records_diagram_detailled_daily_values:
        loadFragment(new FragmentDiagramOneDay());
        break;

      case R.id.nav_information_system:
        loadFragment(new FragmentInfoSystem());
        break;

      case R.id.nav_app:
        showAppInfo();
        break;

      case R.id.nav_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        break;
    }

    drawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    navigationView =
            (NavigationView) findViewById(R.id.main_activity_navigation_drawer);
    drawerLayout =
            (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
    actionBarDrawerToggle =
            new ActionBarDrawerToggle(this, drawerLayout, R.string.opendrawer, R.string.closedrawer);

    drawerLayout.addDrawerListener(actionBarDrawerToggle);

    //Show the Hamburger-Symbol in the ActionBar when the Drawer is not visible
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(true);
    //Show the Back-Symbol when the ActionBar is visible
    actionBar.setDisplayHomeAsUpEnabled(true);

    actionBarDrawerToggle.syncState();

    navigationView.setNavigationItemSelectedListener(this);

    //reload settings
    final SharedPreferences sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preference_file1), Context.MODE_PRIVATE);
    try
    {
      PhotovoltaicDatabase.changeSettings(
              sharedPreferences.getString(getString(R.string.shared_preference_saved_ip), "localhost"),
              sharedPreferences.getString(getString(R.string.shared_preference_saved_port), "5432"),
              sharedPreferences.getString(getString(R.string.shared_preference_saved_user), "smartphone"),
              sharedPreferences.getString(getString(R.string.shared_preference_saved_password), "htl"));
    } catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }

    if (savedInstanceState == null)
    {
      navigationView.setCheckedItem(R.id.nav_home);
      loadFragment(new FragmentHome());
    }
  }

  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState)
  {
    super.onPostCreate(savedInstanceState);
    actionBarDrawerToggle.syncState();
  }
}
