package com.ubirouting.ubimapdemo.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ubirouting.ubimapdemo.R;

/**
 * @function:
 * @author ZhangDao
 * @file_name HorizontalListViewAdapter.java
 * @package_name£∫com.ubirouting.nature.creator.view
 * @project_name£∫MagneticKit
 * @department£∫Android—–∑¢
 * @date 2015-7-21 …œŒÁ10:32:36
 * @version V1.0
 */
class HorizontalListViewAdapter extends BaseAdapter {

	private ArrayList<String> infos;
	private LayoutInflater inflater = null;

	public HorizontalListViewAdapter(Context context, ArrayList<String> infos) {
		inflater = LayoutInflater.from(context);
		this.infos = infos;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder=null;
		// final ViewHolder viewHolder;
		if(view==null){
			holder=new ViewHolder();
			view = inflater.inflate(R.layout.horizontal_text, null);
			holder.text=(TextView) view.findViewById(R.id.horizontalText);
			view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		holder.text.setText(infos.get(position));
		holder.text.setTextColor(Color.BLACK);

		return view;
	}

	private class ViewHolder {
		TextView text;
	}
}
