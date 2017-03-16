package com.netsun.labuy.utils;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/3/16.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();
        if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
            if (parent.getChildAdapterPosition(view) != 0)
                outRect.top = space;
        } else if (manager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            if (parent.getChildAdapterPosition(view) != 0)
                outRect.left = space;
        }
    }
}
