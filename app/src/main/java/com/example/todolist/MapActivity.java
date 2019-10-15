package com.example.todolist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity {
    MapView map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    /*    MapKitFactory.setApiKey("a20243e2-e394-4f01-ad34-38e7e979cc8a");
        MapKitFactory.initialize(this);*/

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(53.1110, 50.0715);
        mapController.setCenter(startPoint);

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(MapActivity.this), map);

        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
        ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
        geoPoints.add(startPoint);

        GeoPoint endPoint = new GeoPoint(53.211962,50.177502);
        geoPoints.add(endPoint);
//add your points here
   //     Polyline line = new Polyline();   //see note below!
//        line.setPoints(geoPoints);
//        line.setOnClickListener(new Polyline.OnClickListener() {
//            @Override
//            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
//                //Toast.makeText(mapView.getContext(), "polyline with " + polyline.getPoints().size() + "pts was tapped", Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });
//        map.getOverlayManager().add(line);
        OSRMRoadManager osrmRoadManager = new OSRMRoadManager(this);
        //osrmRoadManager.setService("https");
        RoadManager roadManager = osrmRoadManager;
        Road road = roadManager.getRoad(geoPoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(roadOverlay);
        map.invalidate();
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

}
