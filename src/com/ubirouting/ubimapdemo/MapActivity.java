package com.ubirouting.ubimapdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.ubirouting.ubimapdemo.view.HorizontalSwitchListView;
import com.ubirouting.ubimapdemo.view.HorizontalSwitchListView.HorizontalSwitchClickListener;
import com.ubirouting.ubimapdemo.view.StoreDetailView;
import com.ubirouting.ubimapdemo.view.StoreDetailView.StoreDetailClickListener;
import com.ubirouting.ubimaplib.UbiMapListener;
import com.ubirouting.ubimaplib.UbiMapView;
import com.ubirouting.ubimaplib.model.map.Area;
import com.ubirouting.ubimaplib.model.map.MapModel;
import com.ubirouting.ubimaplib.model.map.Mark;

public class MapActivity extends Activity {

	//地图控件
	private UbiMapView mMap;
	//选层横向List
	private HorizontalSwitchListView hslvSwitchList;
	//设置起终点面板
	private StoreDetailView sdvDetailView;
	
	private ArrayList<String> list;
	private MapModel currentModel=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		//------------------------------------------
		// 添加地图
		//------------------------------------------
		Intent i = getIntent();
		long mapId = i.getLongExtra("mapId", -1);

		mMap = (UbiMapView) findViewById(R.id.mapview);
		
		//地图SDK，根据mapId加载地图
		mMap.load(mapId);
		
		//------------------------------------------
		// 设置横向List
		// ------------------------------------------
		hslvSwitchList=(HorizontalSwitchListView) findViewById(R.id.hslvSwitchList);
		list = new ArrayList<String>();
		// 您可以通过获取我们SDK ，其中的某些功能，获取楼层列表，这里只是模拟数据
		initList();
		
		hslvSwitchList.setAdapter(list,this);
		hslvSwitchList.setOnHorizontalSwitchListener(new HorizontalSwitchClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
			}
		});
		hslvSwitchList.setInitItem("楼层1");
		
		
		//------------------------------------------
		//设置起点终点
		//------------------------------------------
		sdvDetailView=(StoreDetailView) findViewById(R.id.sdvDetailView);
		sdvDetailView.setListener(new StoreDetailClickListener() {
			
			@Override
			public void onClickStart() {
				if(currentModel!=null){
					mMap.markAsStart(currentModel);
				}
			}
			
			@Override
			public void onClickEnd() {
				if(currentModel!=null){
					mMap.markAsEnd(currentModel);
				}
			}
			
			@Override
			public void onClickDetail() {
				// TODO Auto-generated method stub
				
			}
		});
		
		mMap.setOnMapListener(new UbiMapListener() {

			@Override
			public void onSwitchFloor(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadData(List arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailedLoadData() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClickMap(float arg0, float arg1) {
				// TODO Auto-generated method stub
				if (sdvDetailView != null) {
					sdvDetailView.disapeal();
				}
			}

			@Override
			public void onClickArea(Area arg0) {
				// TODO Auto-generated method stub
				if (sdvDetailView != null) {
					sdvDetailView.show();
				}
				currentModel = arg0;
				sdvDetailView.setTitle(arg0.getName().trim().equals("")?"Other":arg0.getName());
				sdvDetailView.setIcon(arg0.getIcon());
			}

			@Override
			public void onClickMark(Mark arg0) {
				// TODO Auto-generated method stub
				if (sdvDetailView != null) {
					sdvDetailView.show();
				}
				currentModel = arg0;
				sdvDetailView.setTitle("Other");
				sdvDetailView.setIcon(arg0.getIcon());
			}

		});
	}
	
	private void initList(){
		list.add("楼层1");
		list.add("楼层2");
		list.add("楼层3");
		list.add("楼层4");
		list.add("楼层5");
		list.add("楼层6");
		list.add("楼层7");
	}

}
