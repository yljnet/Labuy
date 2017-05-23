package com.netsun.labuy.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.netsun.labuy.R;
import com.netsun.labuy.fragment.CommoditiesFragment;
import com.netsun.labuy.fragment.HomeFragment;
import com.netsun.labuy.fragment.ShoppingCartFragment;
import com.netsun.labuy.fragment.UserInfoFragment;
import com.netsun.labuy.utils.PublicFunc;

import java.util.ArrayList;

import static com.netsun.labuy.utils.MyApplication.isLogon;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ArrayList<Fragment> fragments;
    private HomeFragment homeFragment;
    private CommoditiesFragment commoditiesFragment;
    private UserInfoFragment userInfoFragment;
    private ShoppingCartFragment shoppingCartFragment;
    public BottomNavigationView bottomView;
    private Fragment currentFragment;
    public String name;
    private long firstTime = 0;
    FragmentManager manager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomView = (BottomNavigationView) findViewById(R.id.bottom_view);
        bottomView.setOnNavigationItemSelectedListener(this);
        fragments = getFragments();
        Intent intent = getIntent();
        if (null != intent) {
            int index = intent.getIntExtra("index", 0);
            setFragment(index);
        }
    }

    public void setFragment(int fragmentId) {
        Fragment fragment = fragments.get(fragmentId);
        if (fragment == null) return;
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            if (fragment.isAdded()) {
                transaction.hide(currentFragment).show(fragment).commit();
            } else
                transaction.hide(currentFragment).add(R.id.frame_content, fragment).commit();
        } else {
            if (fragment.isAdded()) {
                transaction.show(fragment).commit();
            } else
                transaction.add(R.id.frame_content, fragment).commit();
        }
        currentFragment = fragment;
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<Fragment>();
        homeFragment = HomeFragment.newInstance();
        fragmentArrayList.add(homeFragment);
        shoppingCartFragment = ShoppingCartFragment.newInstance();
        fragmentArrayList.add(shoppingCartFragment);
        userInfoFragment = UserInfoFragment.newInstance();
        fragmentArrayList.add(userInfoFragment);
        commoditiesFragment = CommoditiesFragment.newInstance();
        fragmentArrayList.add(commoditiesFragment);
        return fragmentArrayList;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                bottomView.getMenu().getItem(0).setChecked(true);
                bottomView.getMenu().getItem(1).setChecked(false);
                bottomView.getMenu().getItem(2).setChecked(false);
                setFragment(0);
                break;
            case R.id.shopping_cart:
                if (!isLogon)
                    PublicFunc.toLogin(this, 1);
                bottomView.getMenu().getItem(0).setChecked(false);
                bottomView.getMenu().getItem(1).setChecked(true);
                bottomView.getMenu().getItem(2).setChecked(false);
                setFragment(1);
                break;
            case R.id.mine:
                if (!isLogon)
                    PublicFunc.toLogin(this, 2);
                bottomView.getMenu().getItem(0).setChecked(false);
                bottomView.getMenu().getItem(1).setChecked(false);
                bottomView.getMenu().getItem(2).setChecked(true);
                setFragment(2);
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
            String action = data.getStringExtra("action");
            if (action != null && "logout".equals(action)) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, 1);
            } else {
                int index = data.getIntExtra("index", 0);
                onNavigationItemSelected(bottomView.getMenu().getItem(index));
            }
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
