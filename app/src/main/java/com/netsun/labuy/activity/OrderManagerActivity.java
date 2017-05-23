package com.netsun.labuy.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.adapter.MyFragmentPageAdapter;
import com.netsun.labuy.fragment.OrderManagerFragment;

import java.util.ArrayList;

public class OrderManagerActivity extends AppCompatActivity {
    public static final int ORDER_ALL=0;
    public static final int ORDER_UNPAY=1;
    public static final int ORDER_PAYED=2;
    private Toolbar toolBar;
    TextView titleTextView;
    TabLayout tabLayout;
    ViewPager viewPager;

    ArrayList<Fragment> fragmentList = null;
    OrderManagerFragment totalOrderFragment;
    OrderManagerFragment unPayOrderFragment;
    OrderManagerFragment payedOrderFragment;
    ArrayList<String> titles = new ArrayList<>();
    int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);
        mode = getIntent().getIntExtra("mode",0);
        toolBar = (Toolbar)findViewById(R.id.toolbar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleTextView = (TextView)findViewById(R.id.tool_bar_title_text);
        titleTextView.setText("我的订单");
        viewPager = (ViewPager) findViewById(R.id.view_page);
        fragmentList = new ArrayList<Fragment>();
        totalOrderFragment = OrderManagerFragment.newInstance("全部",ORDER_ALL);
        titles.add("全部");
        unPayOrderFragment = OrderManagerFragment.newInstance("未支付",ORDER_UNPAY);
        titles.add("未支付");
        payedOrderFragment = OrderManagerFragment.newInstance("已支付",ORDER_PAYED);
        titles.add("已支付");
        fragmentList.add(totalOrderFragment);
        fragmentList.add(unPayOrderFragment);
        fragmentList.add(payedOrderFragment);
        MyFragmentPageAdapter adapter = new MyFragmentPageAdapter(getSupportFragmentManager(), fragmentList,titles);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.id_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(mode);
    }
}
