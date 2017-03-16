package com.netsun.labuy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ConfirmOrderActivity extends AppCompatActivity {

    TextView consigneeTextView;//收件人
    TextView mobileTextView;//手机
    TextView addressTextView;
    TextView countTextView;
    Button submitOrderBtn;
    ImageView nextImageView;
    RecyclerView orderListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        initView();
    }

    private void initView() {
        consigneeTextView = (TextView)findViewById(R.id.id_consignee_text_view);
        mobileTextView = (TextView)findViewById(R.id.id_mobile_text_view);
        addressTextView = (TextView)findViewById(R.id.id_address_text_view);
        countTextView = (TextView)findViewById(R.id.id_order_count_text_view);
        submitOrderBtn = (Button)findViewById(R.id.id_submit_order_button);
        nextImageView = (ImageView)findViewById(R.id.id_next_pic);
        orderListView = (RecyclerView)findViewById(R.id.id_order_list_view);
    }
}
