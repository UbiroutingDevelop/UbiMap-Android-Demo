package com.ubirouting.ubimapdemo;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ubirouting.ubimaplib.data.MapTypeWrongException;
import com.ubirouting.ubimaplib.data.UbiMapDataHelper;
import com.ubirouting.ubimaplib.model.map.Area;
import com.ubirouting.ubimaplib.model.map.Floor;
import com.ubirouting.ubimaplib.model.map.MapModel;
import com.ubirouting.ubimaplib.model.map.Mark;

public class MapDataActivity extends Activity {

	private long mMapId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		mMapId = i.getLongExtra("mapId", -1);

		List<Floor> floors;
		try {
			floors = UbiMapDataHelper.allFloor(mMapId);

			for (Floor f : floors) {
				Log.d("UbiMapDemo", f.toString());
			}

			int floor = floors.get(0).area;
			List<MapModel> pois = UbiMapDataHelper.allPoi(mMapId, floor);
			
			for (MapModel poi : pois) {
				if (poi.isArea()) {
					Log.d("UbiMapDemo", ((Area) poi).toString());
				} else if (poi.isMark()) {
					Log.d("UbiMapDemo", ((Mark) poi).toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MapTypeWrongException e) {
			e.printStackTrace();
		}

	}
}
