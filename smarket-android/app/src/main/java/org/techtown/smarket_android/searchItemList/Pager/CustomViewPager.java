package org.techtown.smarket_android.searchItemList.Pager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    public CustomViewPager (Context context) {
        super(context);
    }

    public CustomViewPager (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int mode = MeasureSpec.getMode(heightMeasureSpec);
//        if(mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST){
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            int height = 0;
//            for(int i=0; i<getChildCount(); i++){
//                View child = getChildAt(i);
//                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//                int h = child.getMeasuredHeight();
//                if(h > height) height = h;
//            }
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;

        final View tab = getChildAt(0);
        int width = getMeasuredWidth();
        int tabHeight = tab.getMeasuredHeight();

        if (wrapHeight) {
            // Keep the current measured width.
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        int fragmentHeight = measureFragment(((Fragment) getAdapter().instantiateItem(this, getCurrentItem())).getView());
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(tabHeight + fragmentHeight + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()), MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int measureFragment(View view) {
        if (view == null)
            return 0;

        view.measure(0, 0);
        return view.getMeasuredHeight();
    }
}