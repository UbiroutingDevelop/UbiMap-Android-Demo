package com.ubirouting.ubimapdemo.view;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;
import android.widget.TextView;

import com.ubirouting.ubimapdemo.R;


class HorizontalListView extends AdapterView<ListAdapter> {

	public boolean mAlwaysOverrideTouch = true;
	protected ListAdapter mAdapter;
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnItemLongClickListener mOnItemLongClicked;
	private boolean mDataChanged = false;

	private int currentItemIndex = 0;
	private String initFloorText;
	private boolean firstInit = true;

	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();

		getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (firstInit) {
							initItem();
							firstInit = false;
						}
					}
				});
	}

	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
	}

	@Override
	public void setOnItemSelectedListener(
			AdapterView.OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		mOnItemClicked = listener;
	}

	@Override
	public void setOnItemLongClickListener(
			AdapterView.OnItemLongClickListener listener) {
		mOnItemLongClicked = listener;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized (HorizontalListView.this) {
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}

	};

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		// TODO: implement
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		reset();
	}

	private synchronized void reset() {
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {
		// TODO: implement
	}

	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		}

		addViewInLayout(child, viewPos, params, true);
		child.measure(
				MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
//		Log.d("ViewHeight", "" + getHeight());
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top,
			int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mAdapter == null) {
			return;
		}

		if (mDataChanged) {
			int oldCurrentX = mCurrentX;
			initView();
			removeAllViewsInLayout();
			mNextX = oldCurrentX;
			mDataChanged = false;
		}

		if (mScroller.computeScrollOffset()) {
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
		}

		if (mNextX <= 0) {
			mNextX = 0;
			mScroller.forceFinished(true);
		}
		if (mNextX >= mMaxX) {
			mNextX = mMaxX;
			mScroller.forceFinished(true);
		}

		int dx = mCurrentX - mNextX;

		// removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);

		mCurrentX = mNextX;

		if (!mScroller.isFinished()) {
			post(new Runnable() {
				@Override
				public void run() {
					requestLayout();
				}
			});

		}
	}

	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount() - 1);
		if (child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);

	}

	private void fillListRight(int rightEdge, final int dx) {
		// rightEdge + dx < getWidth() &&
		while (mRightViewIndex < mAdapter.getCount()) {

			View child = mAdapter.getView(mRightViewIndex,
					mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth() + child.getPaddingRight();

			if (mRightViewIndex == mAdapter.getCount() - 1) {
				mMaxX = mCurrentX + rightEdge - getWidth();
			}

			if (mMaxX < 0) {
				mMaxX = 0;
			}
			mRightViewIndex++;
		}

	}

	private void fillListLeft(int leftEdge, final int dx) {
		while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			View child = mAdapter.getView(mLeftViewIndex,
					mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
	}

	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while (child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mLeftViewIndex++;
			child = getChildAt(0);

		}

		child = getChildAt(getChildCount() - 1);
		while (child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount() - 1);
		}
	}

	private void positionItems(final int dx) {
		if (getChildCount() > 0) {
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, child.getTop(), left + childWidth,
						child.getTop() + child.getMeasuredHeight());
				left += childWidth + child.getPaddingRight();
			}
		}
	}

	public void setInitItem(String floorText) {
		this.initFloorText = floorText;
	}
	
	private int currentViewWidth=0;

	public void setCurrentItem(String floorText) {
//		Log.d("Creator", "getChildCount():"+getChildCount());
		
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			String text = ((TextView) child).getText().toString();
//			Log.d("Creator", "text:"+text);
			if (text.equals(floorText)) {
				((TextView) child).setTextColor(Color.parseColor("#00baf0"));
				((TextView) child).setBackground(getResources().getDrawable(
						R.drawable.locate_xiahua));
//				mScroller.fling((int)getChildAt(currentItemIndex).getX(), 0, 20, 0, 0, mMaxX, (int)getChildAt(i).getX(), 0);
//				mScroller.startScroll((int)getChildAt(currentItemIndex).getX(), 0, (int)getChildAt(i).getX(), 0,10);
				currentViewWidth=child.getWidth();
				scrollTo((int)getChildAt(i).getX());
				currentItemIndex=i;
//				Log.d("Creator", "i:"+i);
			} else {
				((TextView) child).setTextColor(Color.BLACK);
				((TextView) child).setBackground(null);
			}
		}
	}

	private void initItem() {
//		Log.d("ChildCount", ">>" + getChildCount());
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			String text = ((TextView) child).getText().toString();
			if (text.equalsIgnoreCase(initFloorText)) {
				((TextView) child).setTextColor(Color.parseColor("#00baf0"));
				((TextView) child).setBackground(getResources().getDrawable(
						R.drawable.locate_xiahua));
//				Log.d("InitItem", "found");
				currentViewWidth=child.getWidth();
				currentItemIndex=i;
				scrollTo(child.getLeft());
				break;
			} else {
//				Log.d("InitItem", "not found");
			}
		}
	}

	public void moveToLeft() {
		if (currentItemIndex - 1 >= 0) {
//			Log.d("Creator", "currentItemIndex:"+currentItemIndex);
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				// child.setBackground(new ColorDrawable(Color
				// .parseColor("#ffffff")));
				((TextView) child).setTextColor(Color.BLACK);
				((TextView) child).setBackground(null);
			}
			currentItemIndex -= 1;
			View child = getChildAt(currentItemIndex);
			((TextView) child).setTextColor(Color.parseColor("#00baf0"));
			((TextView) child).setBackground(getResources().getDrawable(
					R.drawable.locate_xiahua));
			if (mOnItemClicked != null) {
				mOnItemClicked.onItemClick(
						HorizontalListView.this,
						child,
						mLeftViewIndex + 1 + currentItemIndex,
						mAdapter.getItemId(mLeftViewIndex + 1
								+ currentItemIndex));
			}
			if (mOnItemSelected != null) {
				mOnItemSelected.onItemSelected(
						HorizontalListView.this,
						child,
						mLeftViewIndex + 1 + currentItemIndex,
						mAdapter.getItemId(mLeftViewIndex + 1
								+ currentItemIndex));
			}
		}
	}

	public void moveToRight() {
		if (currentItemIndex + 1 < getChildCount()) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				// child.setBackground(new ColorDrawable(Color
				// .parseColor("#ffffff")));
				((TextView) child).setTextColor(Color.BLACK);
				((TextView) child).setBackground(null);
			}
			currentItemIndex += 1;
			View child = getChildAt(currentItemIndex);
			((TextView) child).setTextColor(Color.parseColor("#00baf0"));
			((TextView) child).setBackground(getResources().getDrawable(
					R.drawable.locate_xiahua));
			if (mOnItemClicked != null) {
				mOnItemClicked.onItemClick(
						HorizontalListView.this,
						child,
						mLeftViewIndex + 1 + currentItemIndex,
						mAdapter.getItemId(mLeftViewIndex + 1
								+ currentItemIndex));
			}
			if (mOnItemSelected != null) {
				mOnItemSelected.onItemSelected(
						HorizontalListView.this,
						child,
						mLeftViewIndex + 1 + currentItemIndex,
						mAdapter.getItemId(mLeftViewIndex + 1
								+ currentItemIndex));
			}
		}
	}

	public synchronized void scrollTo(int x) {
		int num=getWidth()/2-currentViewWidth/2;
		mScroller.startScroll(mNextX, 0, x-num , 0);
//		Log.d("Creator", "num:"+num);
//		Log.d("Creator", "getWidth():"+getWidth());
//		Log.d("Creator", "currentViewWidth():"+currentViewWidth);
		
//		requestLayout();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled |= mGesture.onTouchEvent(ev);
		return handled;
	}

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		synchronized (HorizontalListView.this) {
			mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
		}
		requestLayout();

		return true;
	}

	protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}

	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontalListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return HorizontalListView.this
					.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			synchronized (HorizontalListView.this) {
				mNextX += (int) distanceX;
			}
			requestLayout();

			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					for (int j = 0; j < getChildCount(); j++) {
						View child1 = getChildAt(j);
						// child.setBackground(new ColorDrawable(Color
						// .parseColor("#ffffff")));
						((TextView) child1).setTextColor(Color.BLACK);
						((TextView) child1).setBackground(null);
					}
					currentItemIndex = i;
					((TextView) child)
							.setTextColor(Color.parseColor("#00baf0"));
					((TextView) child).setBackground(getResources()
							.getDrawable(R.drawable.locate_xiahua));
					// child.setBackground(new ColorDrawable(Color
					// .parseColor("#ff0000")));
					if (mOnItemClicked != null) {
						mOnItemClicked.onItemClick(HorizontalListView.this,
								child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					if (mOnItemSelected != null) {
						mOnItemSelected.onItemSelected(HorizontalListView.this,
								child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemLongClicked != null) {
						mOnItemLongClicked.onItemLongClick(
								HorizontalListView.this, child, mLeftViewIndex
										+ 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}

			}
		}

		private boolean isEventWithinView(MotionEvent e, View child) {
			Rect viewRect = new Rect();
			int[] childPosition = new int[2];
			child.getLocationOnScreen(childPosition);
			int left = childPosition[0];
			int right = left + child.getWidth();
			int top = childPosition[1];
			int bottom = top + child.getHeight();
			viewRect.set(left, top, right, bottom);
			return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
		}
	};

}