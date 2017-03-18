package com.netsun.labuy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ChangeDefaultAddrActivity extends AppCompatActivity{
//    TextView titleTextView;
//    Button manageButton;
//    Button addButton;
//    RecyclerView addressRV;

//    List<ReceiveAddress> addressList;
//    OnAddressSelectLinstener onAddressSelectLinstener = new OnAddressSelectLinstener() {
//        @Override
//        public void SelectAddr(ReceiveAddress address) {
//            Intent intent = new Intent(getBaseContext(), ConfirmOrderActivity.class);
//            intent.putExtra("address", address);
//            setResult(RESULT_OK, intent);
//            finish();
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_default_addr);
//        titleTextView = (TextView)findViewById(R.id.tool_bar_title_text);
//        manageButton = (Button)findViewById(R.id.id_manage_button);
//        addButton = (Button)findViewById(R.id.id_add_address_button);
//        addButton.setOnClickListener(this);
//        addressRV = (RecyclerView)findViewById(R.id.id_address_rv);
//
//
//        titleTextView.setText("选择收货地址");
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        addressRV.setLayoutManager(layoutManager);
//
//        addressList = DataSupport.findAll(ReceiveAddress.class);
//        ReceiveAddressAdapter addressAdapter = new ReceiveAddressAdapter(addressList);
//        addressRV.setAdapter(addressAdapter);
//        addressAdapter.setOnAddressSelectLinstener(onAddressSelectLinstener);

    }
}
