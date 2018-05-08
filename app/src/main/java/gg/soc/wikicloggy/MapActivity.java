package gg.soc.wikicloggy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import net.daum.mf.map.api.MapView;


public class MapActivity extends AppCompatActivity {
    MapView mapView;

    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = new MapView(this);

        container = (RelativeLayout) findViewById(R.id.map_view);
        container.addView(mapView);
    }
}
