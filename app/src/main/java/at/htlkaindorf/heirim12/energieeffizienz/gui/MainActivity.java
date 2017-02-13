package at.htlkaindorf.heirim12.energieeffizienz.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentCurrentMeasurement;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentDiagram;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentHome;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentInfoStudents;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentInfoSystem;
import at.htlkaindorf.heirim12.energieeffizienz.gui.fragments.FragmentTable;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

  private FragmentTransaction fragmentTransaction;
  private FragmentManager fragmentManager;
  private ActionBarDrawerToggle actionBarDrawerToggle;
  private DrawerLayout drawerLayout;
  private NavigationView navigationView;
  private int fragmentChoose;

  private void loadFragment(int sel)
  {
    navigationView.setCheckedItem(sel);
    fragmentChoose = sel;
    switch (sel)
    {
      case 0:
        FragmentHome fragmentHome = new FragmentHome();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_fragmanetholder, fragmentHome);
        fragmentTransaction.commit();
        break;

      case 1:
        FragmentCurrentMeasurement fragmentCurrentMeasurement = new FragmentCurrentMeasurement();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_fragmanetholder, fragmentCurrentMeasurement);
        fragmentTransaction.commit();

        break;

      case 2:
        FragmentDiagram fragmentDiagram = new FragmentDiagram();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_fragmanetholder, fragmentDiagram);
        fragmentTransaction.commit();
        break;

      case 3:
        FragmentTable fragmentTable = new FragmentTable();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_fragmanetholder, fragmentTable);
        fragmentTransaction.commit();
        break;

      case 4:
        FragmentInfoSystem fragmentInfoSystem = new FragmentInfoSystem();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_fragmanetholder, fragmentInfoSystem);
        fragmentTransaction.commit();
        break;

      case 5:

        FragmentInfoStudents fragmentInfoStudents = new FragmentInfoStudents();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_fragmanetholder, fragmentInfoStudents);
        fragmentTransaction.commit();
        break;
    }
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
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    switch (id)
    {
      case R.id.nav_home:
        loadFragment(0);
        break;

      case R.id.nav_current_measurement:
        loadFragment(1);
        break;

      case R.id.nav_records_diagram:
        loadFragment(2);
        break;

      case R.id.nav_records_table:
        loadFragment(3);
        break;

      case R.id.nav_information_system:
        loadFragment(4);
        break;

      case R.id.nav_information_students:
        loadFragment(5);
        break;

      case R.id.nav_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        break;
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    drawerLayout = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
    actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
            R.string.opendrawer, R.string.closedrawer);
    drawerLayout.addDrawerListener(actionBarDrawerToggle);

    //Show the Hamburger-Symbol in the ActionBar when the Drawer is not visible
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(true);
    //Show the Back-Symbol when the ActionBar is visible
    actionBar.setDisplayHomeAsUpEnabled(true);

    actionBarDrawerToggle.syncState();

    navigationView = (NavigationView) findViewById(R.id.main_activity_navigation_drawer);
    navigationView.setNavigationItemSelectedListener(this);

    drawerLayout.addDrawerListener(actionBarDrawerToggle);

    fragmentManager = getSupportFragmentManager();
    if (savedInstanceState == null)
      loadFragment(0);
  }

  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState)
  {
    super.onPostCreate(savedInstanceState);
    actionBarDrawerToggle.syncState();
  }
}
