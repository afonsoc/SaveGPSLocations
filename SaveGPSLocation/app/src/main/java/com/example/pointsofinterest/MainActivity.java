package com.example.pointsofinterest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.content.Context;
import android.widget.Toast;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;
    ItemizedIconOverlay.OnItemGestureListener<OverlayItem> markerGestureListener;
    ArrayList<OverlayItem> PointsSave = new ArrayList<OverlayItem>();
    double Lat;
    double Lon;
    boolean AutoSave;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_main);

        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);

        LocationManager mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mv = findViewById(R.id.map);

        mv.setMultiTouchControls(true);
        mv.getController().setZoom(15.0);

        markerGestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            public boolean onItemLongPress(int i, OverlayItem item) {
                Toast.makeText(MainActivity.this, "Name: " + item.getTitle() + "details: " + item.getSnippet(), Toast.LENGTH_LONG).show();
                return true;
            }

            public boolean onItemSingleTapUp(int i, OverlayItem item) {
                Toast.makeText(MainActivity.this, "Name: " + item.getTitle()  + "details: " + item.getSnippet(), Toast.LENGTH_SHORT).show();
                return true;
            }
        };

        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), markerGestureListener);
        mv.getOverlays().add(items);
        items.addItems(PointsSave);

        loadText();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveItem) {
            Intent intent = new Intent(this, SavePoints.class);
            startActivityForResult(intent, 0);
            return true;
        } else if (item.getItemId() == R.id.preferences) {
            Intent intent = new Intent(this, Pref.class);
            startActivityForResult(intent, 1);
            return true;
        } else if (item.getItemId() == R.id.MenuSave) {
            SavetoFile(PointsSave);
            PointsSave.clear();
            return true;
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                Bundle extras = intent.getExtras();
                String name = extras.getString("ptsName");
                String type = extras.getString("ptsType");
                String desc = extras.getString("ptsDesc");

                OverlayItem Marker = new OverlayItem(name, type + "," + desc, new GeoPoint(Lat, Lon));
                items.addItem(Marker);
                String title = Marker.getTitle();
                String snippet = Marker.getSnippet();
                String lat = "" + Marker.getPoint().getLatitude();
                String lon = "" + Marker.getPoint().getLongitude();

                if (AutoSave == true) {
                    savePreference(title + "," + snippet + "," + lat + "," + lon);
                } else {
                    PointsSave.add(Marker);
                }
                mv.getOverlays().add(items);

            }
        }
    }

    // Save points to file through menu option
    private void SavetoFile(ArrayList<OverlayItem> PointsSave) {
        PrintWriter printWriter = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pointsofinterest.txt");
            printWriter = new PrintWriter(new FileOutputStream(file, true));

            for (OverlayItem ItemtoSave : PointsSave) {
                String saveText = ItemtoSave.getTitle() + "," + ItemtoSave.getSnippet() + "," + ItemtoSave.getPoint().getLatitude() + "," + ItemtoSave.getPoint().getLongitude();
                printWriter.println(saveText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null)
                printWriter.close();
        }
    }

    // Saving by Preferences
    private void savePreference(String text) {
        PrintWriter printWriter = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pointsofinterest.txt");
            printWriter = new PrintWriter(new FileOutputStream(file, true));
            printWriter.println(text);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null)
                printWriter.close();
        }
    }

    // Correct reading from file

    public void loadText() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "pointsofinterest.txt");
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            while ((line = br.readLine()) != null) {
                String[] arrayslist = line.split(",");
                double lat = Double.parseDouble(arrayslist[3]);
                double lon = Double.parseDouble(arrayslist[4]);
                OverlayItem Marker = new OverlayItem(arrayslist[0], "type: " + arrayslist[1] + "desc: " + arrayslist[2], new GeoPoint(lat, lon));
                items.addItem(Marker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean autosave = prefs.getBoolean("autosave", false);
        if (autosave == false) {
            AutoSave = false;
        } else {
            AutoSave = true;
        }
    }

    @Override
    public void onLocationChanged(Location newLoc) {
        mv.getController().setCenter(new GeoPoint(newLoc.getLatitude(), newLoc.getLongitude()));
        Lat = newLoc.getLatitude();
        Lon = newLoc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Provider " + provider +
                " disabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Provider " + provider +
                " enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Toast.makeText(this, "Status changed: " + status,
                Toast.LENGTH_LONG).show();
    }
}