package com.netsun.labuy.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/3/3.
 */

public class UpRefreshLayout extends RelativeLayout implements View.OnTouchListener {
    //滑动刷新
    public static final int PULL_TO_REFRESH = 1;
    //释放刷新
    public static final int RELEASE_TO_REFRESH = 2;
    //正在刷新
    public static final int REFRESHING = 3;
    //刷新完成
    public static final int DONE = 4;
    //刷新成功
    public static final int REFRESH_SUCCESS = 1;
    //刷新失败
    public static final int REFRESH_FAIL = 2;
    //当前状态
    private int state = PULL_TO_REFRESH;
    //滑动距离Y
    private float moveDeltaY;
    //释放刷新距离
    private int refreshDist = 200;
    //刷新回调接口
    SwipeRefreshLayout.OnRefreshListener refreshListener;
    //当前位置Y坐标
    private float pointY;
    //上一个位置Y坐标
    private float lastY;
    //是否第一次执行布局
    private boolean isLayouted = false;
    //是否在刷新过程中滑动
    private boolean isRefreshing = false;
    //是否可以滑动
    public boolean canPull = false;
    //手指滑动距离和下拉头滑动距离比，随正切函数变化
    private float radio = 2;

    private View contentView;
    private View bottomView;

    public UpRefreshLayout(Context context) {
        super(context);
    }

    public UpRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void refreshFinish(int refreshState) {
        state = PULL_TO_REFRESH;
        if (REFRESH_SUCCESS == refreshState) {
            canPull = false;
        }
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        this.refreshListener = onRefreshListener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isLayouted) {
            //第一次执行布局
            contentView = getChildAt(0);
            bottomView = getChildAt(1);
            contentView.setOnTouchListener(this);
            isLayouted = true;
        }
        super.onLayout(changed,l,t,r,b);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pointY = ev.getY();
                lastY = pointY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (canPull) {
                    moveDeltaY = moveDeltaY + (ev.getY() - lastY) / radio;
                    if (state == REFRESHING) {
                        isRefreshing = true;
                    }
                }
                pointY = ev.getY();
                radio = (float) (Math.tan(Math.PI / 2 / getMeasuredHeight() * Math.abs(moveDeltaY)) * 2 + 2);
                requestLayout();
                if (Math.abs(moveDeltaY) < refreshDist && state == RELEASE_TO_REFRESH)
                    state = PULL_TO_REFRESH;
                if (Math.abs(moveDeltaY) > refreshDist && state == PULL_TO_REFRESH) {
                    state = RELEASE_TO_REFRESH;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(moveDeltaY) > refreshDist) {
                    //正在刷新时释放下拉头，不隐藏
                    isRefreshing = false;
                }
                if (state == RELEASE_TO_REFRESH && canPull) {
                    state = REFRESHING;
                    if (refreshListener != null) {
                        refreshListener.onRefresh();
                    }
                }
                break;
            default:
                break;
        }
        //事件交给父类分发
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        RecyclerView recyclerView = null;
        RecyclerView.LayoutManager layoutManager = null;
        try {
            recyclerView = (RecyclerView) view;
            layoutManager = recyclerView.getLayoutManager();
        } catch (Exception e) {
            return false;
        }
        if (recyclerView.getChildCount() == 0) {
            canPull = false;
        } else if (recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= getHeight()) {
            canPull = true;
        } else {
            canPull = false;
        }
        return false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }
}
