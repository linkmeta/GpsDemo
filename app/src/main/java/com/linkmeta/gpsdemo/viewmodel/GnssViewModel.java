package com.linkmeta.gpsdemo.viewmodel;

import android.app.Service;
import android.content.Context;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.linkmeta.gpsdemo.model.GnssInfoModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GnssViewModel extends ViewModel {
    public String TAG = "GpsDemo";
    public final ObservableField<String> latitude = new ObservableField<>();
    public final ObservableField<String> longtitude = new ObservableField<>();
    public final ObservableField<String> altitude = new ObservableField<>();
    public final ObservableField<String> TTFF = new ObservableField<>();
    public final ObservableField<String> inused = new ObservableField<>();

    public List<GnssInfoModel> mGnssInfoList;
    public LocationManager mLocationManager;
    private Context context;
    public GnssViewModel(Context context) {
        latitude.set("0");
        longtitude.set("0");
        altitude.set("0");
        TTFF.set("0");
        inused.set("0/0");
        this.context = context;

        mGnssInfoList = new ArrayList<>();
        GnssInfoModel gnssInfoModel = new GnssInfoModel("SvID", "SNR", "EL", "AZ", "Type", "Used");
        mGnssInfoList.add(gnssInfoModel);

        mLocationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Log.i(TAG,location.getLongitude() + "|" + location.getLatitude());
            latitude.set(Double.toString(location.getLatitude()));
            longtitude.set(Double.toString(location.getLongitude()));
            altitude.set(Double.toString(location.getAltitude()));

        }

        @Override
        public void onProviderDisabled(String providmer) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {
        }

    };
    private GnssStatus.Callback mGnsslistner = new GnssStatus.Callback() {

        @Override
        public void onFirstFix(int ttffMillis) {
            Log.i(TAG,"TTFF is " + (double) ttffMillis / 1000);
            Double t1 = (double) ttffMillis / 1000;
            TTFF.set(Double.toString(t1));
        }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            if (status != null) {
                mGnssInfoList.clear();
                GnssInfoModel gnssInfoModel = new GnssInfoModel("SvID", "SNR", "EL", "AZ", "Type", "Used");
                mGnssInfoList.add(gnssInfoModel);

                int SV_total = status.getSatelliteCount();
                int in_used = 0;
                for (int i = 0; i < SV_total; i++) {
                    GnssInfoModel item = new GnssInfoModel();
                    item.setSnr(Float.toString(status.getCn0DbHz(i)));
                    item.setEl(String.format("%.2f", status.getElevationDegrees(i)) + "°");
                    item.setAz(String.format("%.2f", status.getAzimuthDegrees(i)) + "°");
                    boolean used1 = status.usedInFix(i);
                    if (used1) {
                        item.setUsedInFix("Y");
                        in_used++;
                    } else {
                        item.setUsedInFix("N");
                    }

                    int temp = status.getConstellationType(i);
                    switch(temp){
                        case 1:
                            item.setFlag("Gps");
                            item.setPrn(Integer.toString(status.getSvid(i)));
                            break;
                        case 2:
                            item.setFlag("Sbas");
                            item.setPrn(Integer.toString(status.getSvid(i)));
                            break;
                        case 3:
                            item.setFlag("Glo");
                            item.setPrn(Integer.toString(status.getSvid(i) + 64));
                            break;
                        case 4:
                            item.setFlag("Qzss");
                            item.setPrn(Integer.toString(status.getSvid(i)));
                            break;
                        case 5:
                            item.setFlag("Bds");
                            item.setPrn(Integer.toString(status.getSvid(i) + 200));
                            break;
                        case 6:
                            item.setFlag("GAL");
                            item.setPrn(Integer.toString(status.getSvid(i) + 300));
                            break;

                        case 0:
                        default:
                            item.setFlag("unknow");
                            item.setPrn(Integer.toString(status.getSvid(i)));
                            break;
                    }

                    mGnssInfoList.add(item);
                }
                //update inused/total in UI
                inused.set(Integer.toString(in_used) + "/" + Integer.toString(SV_total));
            }
        }
    };

    private void ResetGnssstatus() {
        try {
            this.latitude.set("0");
            this.longtitude.set("0");
            this.altitude.set("0");
            this.TTFF.set("0");
            this.inused.set("0/0");
            this.mGnssInfoList.clear();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private GnssMeasurementsEvent.Callback mEventlister = new GnssMeasurementsEvent.Callback() {
        @Override
        public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
            Log.v(TAG,"in event callback function feature");
            Iterator<GnssMeasurement> iterator = eventArgs.getMeasurements()
                    .iterator();
            while (iterator.hasNext()) {
                GnssMeasurement gmeasurement = iterator.next();
                Log.i(TAG,"SV= " + gmeasurement.getSvid() + "|C/N0=" + gmeasurement.getCn0DbHz());
            }
        }

        @Override
        public void onStatusChanged(int status) {
            Log.i(TAG,"Event status changed");
            super.onStatusChanged(status);
        }
    };

    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int param1Int) {
            GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
            Log.v(TAG,"gpsstatus" + gpsStatus);
        }
    };
    public synchronized void gpsStart() {
        if (mLocationManager == null) {
            return;
        }

        mLocationManager.addGpsStatusListener(gpsStatusListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
        mLocationManager.registerGnssStatusCallback(mGnsslistner);
        mLocationManager.registerGnssMeasurementsCallback(mEventlister);
        Log.i(TAG,"started...");
    }

    public synchronized void gpsStop() {
        if (mLocationManager == null) {
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
        mLocationManager.unregisterGnssMeasurementsCallback(mEventlister);
        mLocationManager.unregisterGnssStatusCallback(mGnsslistner);
        mLocationManager.removeGpsStatusListener(gpsStatusListener);
        ResetGnssstatus();
        Log.i(TAG,"stopped!");
    }

    public boolean sendExtraCommand(String command) {
        return mLocationManager.sendExtraCommand(LocationManager.GPS_PROVIDER, command, null);
    }
}
