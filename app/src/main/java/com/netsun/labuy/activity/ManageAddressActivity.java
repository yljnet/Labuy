package com.netsun.labuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.netsun.labuy.R;
import com.netsun.labuy.fragment.AddressManageFragment;

public class ManageAddressActivity extends AppCompatActivity {
    AddressManageFragment fragment = AddressManageFragment.newInstance();
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);

        mode = getIntent().getIntExtra("mode", 0);
        if (mode == AddressManageFragment.MODE_MANAGE) {
            fragment.setMode(AddressManageFragment.MODE_MANAGE);
        } else if (mode == AddressManageFragment.MODE_SELECT) {
            fragment.setMode(AddressManageFragment.MODE_SELECT);
        } else {
            fragment.setMode(AddressManageFragment.MODE_VIEW);
        }
        getFragmentManager().beginTransaction().replace(R.id.fragment_layout, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mode == AddressManageFragment.MODE_MANAGE) {
                Intent intent = new Intent(getBaseContext(), ManageAddressActivity.class);
                setResult(RESULT_OK, intent);
            } else {
                Intent intent = new Intent(getBaseContext(), ConfirmOrderActivity.class);
                setResult(RESULT_OK, intent);
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
