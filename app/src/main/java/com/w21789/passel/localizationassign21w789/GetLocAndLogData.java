package com.w21789.passel.localizationassign21w789;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GetLocAndLogData extends Service {
    //For running automated tasks
    private Timer timer;

    private final int REQUEST_LOCATION_UPDATE_TIMER = 10*1000;
    private final int REQUEST_LOCATION_UPDATE_MINDISTANCE_METER = 5;

    public GetLocAndLogData() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //write to log file
    public void appendLog(String text) {
        File externalStorageDir = Environment.getExternalStorageDirectory();
        File logFile = new File(externalStorageDir, "localization_log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void sendMessage(String message) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("send-localization-data");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String provider = intent.getStringExtra("provider");
        Toast.makeText(this, "Service Started with provider: " + provider, Toast.LENGTH_LONG).show();

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                float accuracy = location.getAccuracy();
                String provider = location.getProvider();

                String message = String.valueOf(lat) + "," + String.valueOf(lng) +
                        "," + String.valueOf(accuracy) + "," + provider + "," +
                        convertDate(location.getTime());

                sendMessage(message);  // send to the main Activity so user can see
                appendLog(message);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                provider, //LocationProvider.NETWORK_PROVIDER, // GPS_PROVIDER
                REQUEST_LOCATION_UPDATE_TIMER, // 5*60*1000
                REQUEST_LOCATION_UPDATE_MINDISTANCE_METER, // 500
                locationListener);

//         we'll use this for the networking app
        /**
        int delay = 1000; // delay for 1 sec.
        int period = 10000; // repeat every 10 sec.

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                String message = "Hello!";
                sendMessage(message);  // send to the main Activity so user can see
                appendLog(message);
            }
        }, delay, period);
        **/


        return START_NOT_STICKY;
    }

    public static String convertDate(Long dateInMilliseconds) {
        return DateFormat.format("MM/dd/yyyy hh:mm:ss", dateInMilliseconds).toString();
    }

    @Override
    public void onDestroy() {
//        timer.cancel();
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
