package com.netsun.labuy.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.netsun.labuy.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/12.
 */

public class CarouselView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private Context context;

    private int totalCount = 100;//总数，这是为实现无限滑动设置的

    private int showCount;//要显示的轮播图数量

    private int currentPosition = 0, lastPosition = 0;//当前ViewPager的位置

    private ViewPager viewPager;

    private LinearLayout carouselLayout;//展示指示器的布局

    private Adapter adapter;
    private int pageItemWidth;//每个指示器的宽度
    private boolean isUserTouched = false;
    private Timer mTimer;
    private CarouseTask mTask;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == viewPager.getAdapter().getCount() - 1) {
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(currentItem + 1);
            }
        }
    };

    public CarouselView(Context context) {
        super(context);
        this.context = context;
    }

    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * onFinishInflate()方法是自定义控件中常用的一个方法，它表示从XML加载组件完成了，
     * 在该方法中我们通过LayoutInflater.from(context).inflate 获取到个ViewPager对象和carouselLayout对象，
     * 并对pageItemWidth进行了赋值。同时为viewPager设置addOnPageChangeListener。
     * 这里别忘记调用addView();否则控件就展示不了啦！
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTimer = new Timer();
        mTask = new CarouseTask(handler);
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_layout, null);
        this.viewPager = (ViewPager) view.findViewById(R.id.gallery);
        this.carouselLayout = (LinearLayout) view.findViewById(R.id.carousel_ll);
        pageItemWidth = DensityUtil.dip2px(context, 5);
        this.viewPager.addOnPageChangeListener(this);
        addView(view);
    }

    public interface Adapter {
        boolean isEmpty();

        View getView(int position);

        int getCount();
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return totalCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= showCount;
            final View view = adapter.getView(position);
            if (view.getParent() != null) {
                container.removeView(view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            int position = viewPager.getCurrentItem();
            //Viewpager到第一页的实现能向左滑动
            if (position == totalCount - 1) {//ViewPager到最后一页
                position = 0;
            }
            viewPager.setCurrentItem(position, false);
        }
    }

    //为外部提供设置数据源的方法,同时为ViewPager做展示
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        if (adapter != null)
            init();
    }

    private void init() {
        viewPager.setAdapter(null);
        carouselLayout.removeAllViews();
        if (adapter.isEmpty())
            return;
        showCount = adapter.getCount();
        for (int i = 0; i < showCount; i++) {
            ImageView view = new ImageView(context);
            view.setImageResource(R.drawable.shape_point_selector);
            //用来做指示器的View，通过state来做展示效果
            LinearLayout.LayoutParams params;
            params = new LinearLayout.LayoutParams(pageItemWidth, pageItemWidth);
            params.setMargins(pageItemWidth, 0, 0, 0);
            //默认选中第一个
            if (i > 0) {
                view.setSelected(false);
            } else {
                view.setSelected(true);
            }
            view.setLayoutParams(params);
            carouselLayout.addView(view);
        }
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setCurrentItem(0);

        //让手指触碰到的时候自动轮播不起效
        this.viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isUserTouched = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        isUserTouched = false;
                        break;
                }
                return false;
            }
        });
        mTimer.schedule(mTask, 3000, 3000);
    }

    class CarouseTask extends TimerTask {
        Handler handler;

        public CarouseTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        position = position % showCount;
        carouselLayout.getChildAt(position).setSelected(true);
        carouselLayout.getChildAt(lastPosition).setSelected(false);
        lastPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
