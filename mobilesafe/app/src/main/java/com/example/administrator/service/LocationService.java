package com.example.administrator.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.example.administrator.util.ConstantValue;

/**
 * Created by Administrator on 2016/7/11.
 */
public class LocationService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);

        MyLocationListener myLocationListener = new MyLocationListener();
        lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            double longitude = location.getLongitude();

            double latitude = location.getLatitude();

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(ConstantValue.CONTACT_PHONE, null, "longitude = "+longitude+",latitude = "+latitude, null, null);
        }

        @Override
        public void onProviderDisabled(String provider) {


        }

        @Override
        public void onProviderEnabled(String provider) {


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
