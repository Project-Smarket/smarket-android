package org.techtown.smarket_android.searchItemList;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerDecoration extends RecyclerView.ItemDecoration {
    private int size5;

    public RecyclerDecoration(Context context) {

        size5 = dpToPx(context, 4);
    }

    // dp -> pixel 단위로 변경
    private int dpToPx(Context context, int dp) {

        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        //상하 설정
        if(position == 0 || position == 1) {
            // 첫번 째 줄 아이템
            outRect.top = size5;
            outRect.bottom = size5;
        } else {
            outRect.top = size5;
            outRect.bottom = size5;
        }

    }
}
