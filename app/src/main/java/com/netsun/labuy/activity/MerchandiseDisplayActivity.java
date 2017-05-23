package com.netsun.labuy.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.adapter.MyFragmentPageAdapter;
import com.netsun.labuy.fragment.ChooseOptionFragment;
import com.netsun.labuy.fragment.ProductInfoDetailsFragment;
import com.netsun.labuy.gson.Merchandise;
import com.netsun.labuy.gson.ProductOption;
import com.netsun.labuy.utils.OnValueItemClickListener;
import com.netsun.labuy.utils.OptionPop;
import com.netsun.labuy.utils.PublicFunc;

import java.util.ArrayList;
import java.util.List;


public class MerchandiseDisplayActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView backImageView;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView addShoppingCart, buy;
    RelativeLayout loadingLayout;

    Merchandise merchandise;
    List<ProductOption> validOptions;
    ArrayList<Fragment> fragmentList;
    ArrayList<String> titles = new ArrayList<String>();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise_display);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        viewPager = (ViewPager) findViewById(R.id.view_page);
        backImageView = (ImageView) findViewById(R.id.back);
        backImageView.setOnClickListener(this);
        addShoppingCart = (TextView) findViewById(R.id.id_add_shopping_cart);
        buy = (TextView) findViewById(R.id.id_buy);
        addShoppingCart.setOnClickListener(this);
        buy.setOnClickListener(this);
        if (getIntent().getStringExtra("id") != null) {
            String mode = getIntent().getStringExtra("mode");
            String id = getIntent().getStringExtra("id");
            PublicFunc.getMerchandise(mode, id, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        merchandise = (Merchandise) msg.obj;
                        validOptions = getValidOptions();
                        if (validOptions != null)
                            unSelect = getUnSelect();
                        initViewPage();
                    }

                }
            });
        }
    }


    private void initViewPage() {

        loadingLayout.setVisibility(View.GONE);
        fragmentList = new ArrayList<Fragment>();
        optionFragment = ChooseOptionFragment.newInstance(merchandise);
        titles.add("商品");
        infoFragment = ProductInfoDetailsFragment.newInstance(merchandise);
        titles.add("详情");
        fragmentList.add(optionFragment);
        fragmentList.add(infoFragment);
        MyFragmentPageAdapter adapter = new MyFragmentPageAdapter(getSupportFragmentManager(), fragmentList, titles);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.id_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);
        optionPop = new OptionPop(this, merchandise, handler, OptionPop.STYLE_KEEP, new OnValueItemClickListener() {

            @Override
            public void onClick(int optionIndex, int valueIndex) {
                setOptionText();
            }
        });
        setOptionText();
    }

    public List<ProductOption> getValidOptions() {
        if (optionPop != null) return
                optionPop.getValidOptions();
        List<ProductOption> result = new ArrayList<ProductOption>();
        if (merchandise.options == null) return null;
        for (ProductOption option : merchandise.options) {
            if (option.values != null && option.values.size() > 0) {
                result.add(option);
            }
        }
        return result;
    }

    public String getUnSelect() {
        if (optionPop != null)
            return optionPop.getUnSelect();
        StringBuilder builder = new StringBuilder("");
        for (ProductOption option : validOptions) {
//            if (option.selected == -1) {
            if (builder.length() == 0)
                builder.append(option.name);
            else
                builder.append("  ").append(option.name);
//            }
        }
        return builder.toString();
    }

    public void setOptionText() {
        if (validOptions != null) {
            unSelect = getUnSelect();
            if (!unSelect.isEmpty())
                optionFragment.setOptions(unSelect);
            else {
                if (optionPop != null)
                    optionFragment.setOptions("已选" + optionPop.getOptionSelected());
                else
                    optionFragment.setOptions("已选 数量:1件");
            }
        } else {
            if (optionPop != null)
                optionFragment.setOptions("已选 数量:" + optionPop.getNum() + "件");
            else
                optionFragment.setOptions("已选 数量:1件");
        }
    }

    public void showOptionPop(int style) {
        optionPop.setStyle(style);
        optionPop.show(viewPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.id_add_shopping_cart:
                showOptionPop(OptionPop.STYLE_KEEP);
                break;
            case R.id.id_buy:
                showOptionPop(OptionPop.STYLE_BUY);
                break;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }
}
