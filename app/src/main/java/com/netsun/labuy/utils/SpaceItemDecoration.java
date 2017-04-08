package com.netsun.labuy.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.netsun.labuy.R;

/**
 * Created by Administrator on 2017/3/16.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private Drawable mDivider;//分割线Drawable

    public SpaceItemDecoration(int space) {
        this.space = space;
        mDivider = ContextCompat.getDrawable(MyApplication.getContext(), R.color.secondary_text_dark);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager().getClass().equals(LinearLayoutManager.class)) {
            LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();
            if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
                if (parent.getChildAdapterPosition(view) != 0)
                    outRect.top = space;
            } else if (manager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                if (parent.getChildAdapterPosition(view) != 0)
                    outRect.left = space;
            }
        } else if (parent.getLayoutManager().getClass().equals(GridLayoutManager.class)) {
            GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
            outRect.left = space;
            outRect.top = space;
            if (parent.getChildAdapterPosition(view) % 2 == 0) {
                outRect.left = 0;
            }
            if (parent.getChildAdapterPosition(view) <= 1) {
                outRect.top = 0;
                outRect.top = 0;
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        if (parent.getLayoutManager().getClass().equals(LinearLayoutManager.class)) {
            LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();
            if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    int top = child.getBottom() + params.bottomMargin;
                    int bottom = top + space;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            } else {

            }
        } else if (parent.getLayoutManager().getClass().equals(GridLayoutManager.class)) {
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + space;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                mDivider.setBounds(child.getLeft()- space,child.getTop(),child.getLeft(),child.getBottom());
                mDivider.draw(c);
            }
        }
    }
}
