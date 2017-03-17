package com.netsun.labuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.netsun.labuy.adapter.ReceiveAddressAdapter;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.utils.OnAddressSelectLinstener;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ChangeDefaultAddrActivity extends AppCompatActivity {
    TextView titleTextView;
    Button manageButton;
    Button addButton;
    RecyclerView addressRV;

    List<ReceiveAddress> addressList;
    OnAddressSelectLinstener onAddressSelectLinstener = new OnAddressSelectLinstener() {
        @Override
        public void SelectAddr(ReceiveAddress address) {
            Intent intent = new Intent(getBaseContext(), ConfirmOrderActivity.class);
            intent.putExtra("address", address);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_all_addresss);
        titleTextView = (TextView)findViewById(R.id.tool_bar_title_text);
        manageButton = (Button)findViewById(R.id.id_manage_button);
        addButton = (Button)findViewById(R.id.id_add_address_button);
        addressRV = (RecyclerView)findViewById(R.id.id_address_rv);


        titleTextView.setText("选择收货地址");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        addressRV.setLayoutManager(layoutManager);

        addressList = DataSupport.findAll(ReceiveAddress.class);
        ReceiveAddressAdapter addressAdapter = new ReceiveAddressAdapter(addressList);
        addressRV.setAdapter(addressAdapter);
        addressAdapter.setOnAddressSelectLinstener(onAddressSelectLinstener);
    }
}
