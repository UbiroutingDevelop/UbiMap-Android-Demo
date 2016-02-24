package com.ubirouting.ubimapdemo.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ubirouting.ubimapdemo.R;

public class HorizontalSwitchListView extends RelativeLayout{

	private Context context;
	
	public HorizontalListView horList;
	private ImageButton ibtnSwitchFloorLeft;
	private ImageButton ibtnSwitchFloorRight;
	
	private HorizontalSwitchClickListener listener;
	private ArrayList<String> list;
	
	public HorizontalSwitchListView(Context context) {
		super(context);
		this.context=context;
		
		initView();
	}
	
	public HorizontalSwitchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initView();
    }

    public HorizontalSwitchListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        initView();
    }
    
    private void initView(){
    	LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_horizontal_switch, this);
        
		// 添加楼层横向列表
		horList = (HorizontalListView) findViewById(R.id.hlvHView);
		ibtnSwitchFloorLeft = (ImageButton) findViewById(R.id.ibtnSwitchFloorLeft);
		ibtnSwitchFloorRight = (ImageButton) findViewById(R.id.ibtnSwitchFloorRight);

		horList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(listener!=null){
					listener.onItemClick(parent, view, position, id);
				}
				horList.setCurrentItem(list.get(position));
			}
		});

		ibtnSwitchFloorLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 最后也是会调用 list的onItemClickListener
				horList.moveToLeft();
			}
		});

		ibtnSwitchFloorRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				horList.moveToRight();
			}
		});

    }
    
    
    
    public void setAdapter(ArrayList<String> list,Context context){
    	HorizontalListViewAdapter adapter = new HorizontalListViewAdapter(
				context, list);
    	this.list=list;
    	horList.setAdapter(adapter);
    }
    
    public void setInitItem(String itemText){
    	horList.setInitItem(itemText);
    }

    public void setOnHorizontalSwitchListener(HorizontalSwitchClickListener listener){
    	this.listener=listener;
    }
    
    public interface HorizontalSwitchClickListener {
    	/**
    	 * 点击 左右按钮最后也会调用 此方法
    	 * @param parent
    	 * @param view
    	 * @param position
    	 * @param id
    	 */
        public void onItemClick(AdapterView<?> parent, View view,
				int position, long id);
    }
    
}
