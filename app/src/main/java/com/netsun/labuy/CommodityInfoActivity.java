package com.netsun.labuy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.netsun.labuy.adapter.MyFragmentPageAdapter;
import com.netsun.labuy.fragment.ChooseOptionFragment;
import com.netsun.labuy.fragment.ProductInfoDetailsFragment;
import com.netsun.labuy.gson.ProductInfo;
import com.netsun.labuy.gson.ProductOption;
import com.netsun.labuy.utils.HttpUtils;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.OnValueItemClickListener;
import com.netsun.labuy.utils.OptionPop;
import com.netsun.labuy.utils.PublicFunc;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CommodityInfoActivity extends AppCompatActivity implements View.OnClickListener {
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView addShoppingCart, buy;

    ProductInfo productInfo;
    ArrayList<Fragment> fragmentList;
    ChooseOptionFragment optionFragment;
    ProductInfoDetailsFragment infoFragment;
    private int num = 1;
    private String unSelect;
    private OptionPop optionPop = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //处理选项返回信息
            if (msg.what == 2) {
                num = msg.arg1;
            }
        }
    };


    public void showOptionPop(int style) {
        optionPop = new OptionPop(this, productInfo, handler, style, new OnValueItemClickListener() {

            @Override
            public void onClick(int optionIndex, int valueIndex) {
                if (productInfo.options != null) {
                    unSelect = getUnSelect();
                    if (!unSelect.isEmpty())
                        optionFragment.setOptions("请选择 " + unSelect);
                    else
                        optionFragment.setOptions("已选 " + optionPop.getOptionSelected());
                }
                else {
                    optionFragment.setOptions("已选 数量:" + optionPop.getNum() + "件");
                }
            }
        });
        optionPop.show(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_info);
        addShoppingCart = (TextView) findViewById(R.id.id_add_shopping_cart);
        buy = (TextView) findViewById(R.id.id_buy);
        addShoppingCart.setOnClickListener(this);
        buy.setOnClickListener(this);
        if (getIntent().getStringExtra("id") != null) {
            String id=getIntent().getStringExtra("id");
            getYQInformation(id);
        }
    }

    public String getUnSelect() {
        StringBuilder builder = new StringBuilder("");
        if (productInfo.options != null) {
            for (ProductOption option : productInfo.options) {
                if (option.selected == -1) {
                    if (builder.length() == 0)
                        builder.append(option.name);
                    else
                        builder.append("  ").append(option.name);
                }
            }
        }
        return builder.toString();
    }

    private void initViewPage() {
        viewPager = (ViewPager) findViewById(R.id.view_page);
        fragmentList = new ArrayList<Fragment>();
        optionFragment = new ChooseOptionFragment(productInfo, handler);
        infoFragment = new ProductInfoDetailsFragment(productInfo);
        fragmentList.add(optionFragment);
        fragmentList.add(infoFragment);
        MyFragmentPageAdapter adapter = new MyFragmentPageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.id_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);
    }

    private void getYQInformation(final String yq_id) {
        String url = "http://www.dev.labuy.cn/app.php/Yq?model=device&id=" + yq_id;
        LogUtils.d(MyApplication.TAG, url);
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resText = response.body().string();
                productInfo = PublicFunc.handleYQInfoResponse(resText);
                productInfo.id = yq_id;
                if (productInfo.options != null)
                    unSelect = getUnSelect();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initViewPage();
                    }
                });
            }
        });
    }

    // 提交订单回调方法
    Callback submitOrderCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String resText = response.body().string();
            LogUtils.d(MyApplication.TAG, resText);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_add_shopping_cart:
                showOptionPop(OptionPop.STYLE_KEEP);
                break;
            case R.id.id_buy:
                showOptionPop(OptionPop.STYLE_BUY);
                break;
        }
    }
}
