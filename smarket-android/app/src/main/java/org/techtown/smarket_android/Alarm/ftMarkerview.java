package org.techtown.smarket_android.Alarm;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import org.techtown.smarket_android.R;


public class ftMarkerview extends MarkerView {

    private TextView tvContent;
    public ftMarkerview(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public MPPointF getOffset() {
        MPPointF mpPointF = new MPPointF((-(getWidth() / 2)), -getHeight());
        return mpPointF;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //float value = e.getY();
        //String value_s = String.format("%,f", value) +"원";
        tvContent.setText(""+e.getY());

       // super.refreshContent(e, highlight);
    }
}
