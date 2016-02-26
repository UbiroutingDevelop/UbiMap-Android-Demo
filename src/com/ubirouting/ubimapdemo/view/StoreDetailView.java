package com.ubirouting.ubimapdemo.view;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubirouting.ubimapdemo.R;

public class StoreDetailView extends RelativeLayout implements View.OnClickListener {
	private Context context;

	private Button setAsStartBtn, setAsEndBtn, showDetailBtn;
	private TextView titleTxt, subTitlelTxt;
	private ImageView imageView;

	private StoreDetailClickListener listener;

	public StoreDetailView(Context context) {
		super(context);
		this.context = context;

		initView();
	}

	public StoreDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		initView();
	}

	public StoreDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;

		initView();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public StoreDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		this.context = context;

		initView();
	}

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_store_detail, this);

		this.setAsStartBtn = (Button) this.findViewById(R.id.store_detail_setAsStart);
		setAsStartBtn.setOnClickListener(this);
		this.setAsEndBtn = (Button) this.findViewById(R.id.store_detail_setAsEnd);
		setAsEndBtn.setOnClickListener(this);
		this.showDetailBtn = (Button) this.findViewById(R.id.store_detail_detail);
		showDetailBtn.setOnClickListener(this);

		titleTxt = (TextView) this.findViewById(R.id.store_detail_name);
		subTitlelTxt = (TextView) this.findViewById(R.id.store_detail_subname);

		imageView = (ImageView) this.findViewById(R.id.store_detail_icon);

		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}

	public void setListener(StoreDetailClickListener l) {
		this.listener = l;
	}

	public void show() {

		boolean startAnimation = (this.getVisibility() != View.VISIBLE);
		this.setVisibility(View.VISIBLE);
		if (startAnimation) {
			ValueAnimator v = ValueAnimator.ofFloat(0, 1.f);
			v.setDuration(500);
			v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					StoreDetailView.this.setAlpha((Float) animation.getAnimatedValue());
				}
			});
			v.start();
		}

	}

	public void hide() {
		this.setVisibility(View.INVISIBLE);
	}

	public void setTitle(String title) {
		titleTxt.setText(title);
	}

	public void setIcon(Bitmap icon) {
		imageView.setImageBitmap(icon);
	}

	public void setSubTitlel(String subTitlel) {
		subTitlelTxt.setText(subTitlel);
	}

	@Override
	public void onClick(View v) {
		if (listener == null)
			return;
		if (v.getId() == R.id.store_detail_setAsStart) {
			listener.onClickStart();
		} else if (v.getId() == R.id.store_detail_setAsEnd) {
			listener.onClickEnd();
		} else if (v.getId() == R.id.store_detail_detail) {
			listener.onClickDetail();
		}
	}

	public interface StoreDetailClickListener {
		void onClickStart();

		void onClickEnd();

		void onClickDetail();
	}
}
