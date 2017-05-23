package com.netsun.labuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.fragment.AddressManageFragment;
import com.netsun.labuy.utils.LogoutListener;
import com.netsun.labuy.utils.PublicFunc;

import org.litepal.crud.DataSupport;

import static com.netsun.labuy.R.id.button_logout;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    TextView titleTextView;
    private Button buttonLogout;
    private Button recvAddrButton;
    private LogoutListener listener = new LogoutListener() {
        @Override
        public void onLogoutResult(boolean result) {
            if (result) {
                DataSupport.deleteAll(ReceiveAddress.class);//清除收件地址信息
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("action","logout");
                setResult(RESULT_OK,intent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.tool_bar_title_text);
        buttonLogout = (Button) findViewById(button_logout);
        recvAddrButton = (Button) findViewById(R.id.id_recv_addr_button);
        titleTextView.setText("设置");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonLogout.setOnClickListener(this);
        recvAddrButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_logout:
                PublicFunc.logout(new LogoutListener() {
                    @Override
                    public void onLogoutResult(boolean result) {
                        if (result) {
//                            DataSupport.deleteAll(ShoppingItem.class);//清除购物车信息
                            DataSupport.deleteAll(ReceiveAddress.class);//清除收件地址信息
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.putExtra("action","logout");
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                });
                break;
            case R.id.id_recv_addr_button:
                Intent intent = new Intent(getBaseContext(), ManageAddressActivity.class);
                intent.putExtra("mode", AddressManageFragment.MODE_MANAGE);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
