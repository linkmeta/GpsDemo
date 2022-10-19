package com.linkmeta.gpsdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.linkmeta.gpsdemo.adapter.ListViewAdapter;
import com.linkmeta.gpsdemo.databinding.ActivityMainBinding;
import com.linkmeta.gpsdemo.viewmodel.GnssViewModel;

public class MainActivity extends AppCompatActivity {

    private Switch mSwitch;
    public String TAG = "GpsDemo";
    private GnssViewModel mGnssViewModel;
    private ActivityMainBinding mDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mGnssViewModel = new GnssViewModel(this);
        mDataBinding.setViewModel(mGnssViewModel);

        ListViewAdapter adapter = new ListViewAdapter(mGnssViewModel.mGnssInfoList, R.layout.listview_items, BR.gnssInfo);
        mDataBinding.listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (!mGnssViewModel.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Toast.makeText(this, "Please Enable GPS in Setting page", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.WAKE_LOCK
                            }, 100);
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.switch_menu, menu);
        MenuItem item = menu.findItem(R.id.gps_switch_item);

        if (item != null) {
            mSwitch = MenuItemCompat.getActionView(item).findViewById(R.id.gps_switch);
            if (mSwitch != null) {
                // Set up listener for GPS on/off switch
                mSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Turn GPS on or off
                        if (!mSwitch.isChecked()) {
                            mGnssViewModel.gpsStop();
                        } else {
                            if (mSwitch.isChecked()) {
                                mGnssViewModel.gpsStart();
                            }
                        }
                    }
                });
            }
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.clear_aiding_data:

                Bundle extras = new Bundle();
                extras.putBoolean("all", true);
                if (mGnssViewModel.mLocationManager.sendExtraCommand("gps", "delete_aiding_data", extras)) {
                    Log.i(TAG,"==>Delete aiding data");
                    Toast.makeText(getApplicationContext(), "Delete all aid data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Delete all aid data Failed", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.about:
                Toast.makeText(getApplicationContext(), "LinkMeta @ Copyright V1.0.3", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}