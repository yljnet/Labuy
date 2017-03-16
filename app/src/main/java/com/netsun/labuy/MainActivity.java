package com.netsun.labuy;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.netsun.labuy.fragment.CommoditiesFragment;
import com.netsun.labuy.fragment.ShoppingCartFragment;
import com.netsun.labuy.fragment.UserInfoFragment;
import com.netsun.labuy.utils.PublicFunc;

import java.util.ArrayList;

import static com.netsun.labuy.utils.MyApplication.isLogon;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ArrayList<Fragment> fragments;
    private CommoditiesFragment commoditiesFragment;
    private UserInfoFragment userInfoFragment;
    private ShoppingCartFragment shoppingCartFragment;
    private BottomNavigationView bottomView;
    public String name;
    private long  firstTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomView = (BottomNavigationView) findViewById(R.id.bottom_view);
        bottomView.setOnNavigationItemSelectedListener(this);
        fragments = getFragments();
        Intent intent = getIntent();
        if (null != intent) {
            if (null != intent.getStringExtra("work")) {
                PublicFunc.work = intent.getStringExtra("work");
            }
        }
        if (TextUtils.equals(PublicFunc.work, "search_yq_by_cate_id") || TextUtils.equals(PublicFunc.work, "search_qy_by_key")) {
            setFragment(0);
        }

    }


    public void setFragment(int fragmentId) {
        Fragment fragment = fragments.get(fragmentId);
        if (fragment == null) return;
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_content, fragment).commit();
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<Fragment>();
        commoditiesFragment = CommoditiesFragment.newInstance();
        fragmentArrayList.add(commoditiesFragment);
        shoppingCartFragment = ShoppingCartFragment.newInstance();
        fragmentArrayList.add(shoppingCartFragment);
        userInfoFragment = UserInfoFragment.newInstance();
        fragmentArrayList.add(userInfoFragment);
        return fragmentArrayList;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                setFragment(0);
                break;
            case R.id.shopping_cart:
                if (!isLogon)
                    PublicFunc.toLogin(this,1);
                else
                    setFragment(1);
                break;
            case R.id.mine:
                if (!isLogon)
                    PublicFunc.toLogin(this,2);
                else {
                    setFragment(2);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int index = data.getIntExtra("index",0);
            setFragment(index);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 800) {//如果两次按键时间间隔大于800毫秒，则不退出
                Toast.makeText(MainActivity.this, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                firstTime = secondTime;//更新firstTime
                return true;
            } else {
                System.exit(0);//否则退出程序
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
