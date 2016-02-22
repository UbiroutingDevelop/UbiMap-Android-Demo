package com.ubirouting.ubimapdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ubirouting.ubimaplib.UbiMapView;

public class MapActivity extends Activity {

	UbiMapView mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		Intent i = getIntent();
		long mapId = i.getLongExtra("mapId", -1);

		mMap = (UbiMapView) findViewById(R.id.mapview);
		mMap.load(mapId);
	}

}
