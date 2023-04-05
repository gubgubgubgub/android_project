package com.example.shared_calendar;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class ItemDecoration extends RecyclerView.ItemDecoration {
    private final int padding = 5; // 여백을 5로 설정
    @Override
     public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      super.getItemOffsets(outRect, view, parent, state);
      outRect.top = padding; 
    }
}
