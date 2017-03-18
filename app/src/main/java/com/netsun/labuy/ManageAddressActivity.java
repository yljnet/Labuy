package com.netsun.labuy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.netsun.labuy.fragment.AddressManageFragment;

public class ManageAddressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);
        AddressManageFragment fragment = AddressManageFragment.newInstance();
        fragment.setMode(AddressManageFragment.MODE_MANAGE);
        getFragmentManager().beginTransaction().replace(R.id.fragment_layout,fragment).commit();
    }
}
