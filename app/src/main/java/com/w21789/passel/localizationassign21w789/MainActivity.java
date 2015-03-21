package com.w21789.passel.localizationassign21w789;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ArrayList<String> dataItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this here is needed so we can update the ListView as we get data
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                dataItems);
        ListView dataListView = (ListView)findViewById(R.id.dataListView);
        dataListView.setAdapter(adapter);

        //Local messages passed go through here
        LocalBroadcastManager.getInstance(this).registerReceiver(localizationMessageReceiver,
                new IntentFilter("send-localization-data"));
    }

    private BroadcastReceiver localizationMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            addItems(message);
            Log.d("receiver", "Got message: " + message);
        }
    };

    //Adds a new row to the ListView with contents of message
    public void addItems(String message) {
        dataItems.add(message);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Start gathering data!
    public void startGathering(View view) {
        startService(new Intent(getBaseContext(), GetLocAndLogData.class));
    }

    //Stop gathering data!
    public void stopGathering(View view) {
        stopService(new Intent(getBaseContext(), GetLocAndLogData.class));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localizationMessageReceiver);
        super.onDestroy();
    }
}
