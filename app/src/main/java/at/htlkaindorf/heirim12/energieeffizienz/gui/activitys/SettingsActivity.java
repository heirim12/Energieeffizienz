package at.htlkaindorf.heirim12.energieeffizienz.gui.activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import at.htlkaindorf.heirim12.energieeffizienz.R;
import at.htlkaindorf.heirim12.energieeffizienz.database.PhotovoltaicDatabase;

public class SettingsActivity extends AppCompatActivity
{
  private void showSnackbar(String text)
  {
    Snackbar.make(findViewById(android.R.id.content),
            text, Snackbar.LENGTH_LONG).show();
  }

  private String getInput(int id, int name)
          throws Exception
  {
    final EditText editText = (EditText) findViewById(id);
    String string = editText.getText().toString();
    if (string.matches(""))
      throw new Exception(getString(R.string.activity_settings_non_input_exception)
              + " " + getString(name));
    return string;
  }

  private void saveSettings(SharedPreferences sharedPreferences)
          throws Exception
  {

    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(getString(R.string.shared_preference_saved_ip),
            getInput(R.id.activity_settings_serverIp, R.string.activity_settings_server_ip));
    editor.putString(getString(R.string.shared_preference_saved_port),
            getInput(R.id.activity_settings_serverPort, R.string.activity_settings_server_port));
    editor.putString(getString(R.string.shared_preference_saved_user),
            getInput(R.id.activity_settings_user, R.string.activity_settings_user));
    editor.putString(getString(R.string.shared_preference_saved_password),
            getInput(R.id.activity_settings_password, R.string.activity_settings_password));
    editor.commit();
  }

  private void setEditText(int id, String string)
  {
    final EditText editText = (EditText) findViewById(id);
    editText.setText(string);
  }

  private void loadOldSettings(SharedPreferences sharedPreferences)
  {
    setEditText(R.id.activity_settings_serverIp,
            sharedPreferences.getString(getString(R.string.shared_preference_saved_ip), ""));
    setEditText(R.id.activity_settings_serverPort,
            sharedPreferences.getString(getString(R.string.shared_preference_saved_port), ""));
    setEditText(R.id.activity_settings_user,
            sharedPreferences.getString(getString(R.string.shared_preference_saved_user), ""));
//    setEditText(R.id.activity_settings_password,
//            sharedPreferences.getString(getString(R.string.shared_preference_saved_password), ""));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if (item.getItemId() == android.R.id.home)
    {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    setTitle(getString(R.string.activity_settings_title));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    final SharedPreferences sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preference_file1),
                    Context.MODE_PRIVATE);
    loadOldSettings(sharedPreferences);

//    InputFilter[] ipFilter = new InputFilter[1];
//    ipFilter[0] = new InputFilter()
//    {
//      @Override
//      public CharSequence filter(CharSequence input, int start, int end,
//                                 Spanned dest, int dstart, int dend)
//      {
//        if (end > start)
//        {
//          String destTxt = dest.toString();
//          String resultingTxt = destTxt.substring(0, dstart)
//                  + input.subSequence(start, end)
//                  + destTxt.substring(dend);
//          if (!resultingTxt
//                  .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?"))
//          {
//            return "";
//          } else
//          {
//            String[] splits = resultingTxt.split("\\.");
//            for (int i = 0; i < splits.length; i++)
//            {
//              if (Integer.valueOf(splits[i]) > 255)
//              {
//                return "";
//              }
//            }
//          }
//        }
//        return null;
//      }
//    };
//
//    final EditText ip = (EditText) findViewById(R.id.activity_settings_serverIp);
//    ip.setFilters(ipFilter);

    final Button saveButton = (Button) findViewById(R.id.activity_settings_saveButton);
    saveButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        try
        {
          saveSettings(sharedPreferences);
          PhotovoltaicDatabase.changeSettings(
                  sharedPreferences.getString(getString(R.string.shared_preference_saved_ip),
                          "localhost"),
                  sharedPreferences.getString(getString(R.string.shared_preference_saved_port),
                          "5432"),
                  sharedPreferences.getString(getString(R.string.shared_preference_saved_user),
                          "smartphone"),
                  sharedPreferences.getString(getString(R.string.shared_preference_saved_password),
                          "htl"));
          showSnackbar(getString(R.string.activity_settings_successfully_changed));
          finish();
        } catch (Exception ex)
        {
          showSnackbar(ex.getLocalizedMessage());
        }
      }
    });
  }
}
