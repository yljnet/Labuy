package com.netsun.labuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netsun.labuy.R;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.utils.HandleDataBase;
import com.netsun.labuy.utils.InputTextFifter;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.PublicFunc;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.widget.Toast.makeText;

public class EditAddressActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private Toolbar toolbar;
    TextView titleTV;
    EditText nameEditText;
    EditText mobileEditText;
    EditText telEditText;
    EditText emailEditText;
    EditText addrEditText;
    EditText zipEditText;
    TextView areaTextView;
    Button saveButton;

    String areaCode;
    ReceiveAddress address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        initView();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (getIntent() != null) {
            address = (ReceiveAddress) getIntent().getParcelableExtra("address");
            if (address != null) {
                nameEditText.setText(address.getConsignee());
                mobileEditText.setText(address.getMobile());
                telEditText.setText(address.getTel());
                emailEditText.setText(address.getEmail());
                addrEditText.setText(address.getAddress());
                zipEditText.setText(address.getZip());
                String area = null;
                while ((area = HandleDataBase.getAreaById(String.valueOf(address.getRegional()))) == null) {
                    PublicFunc.getAreaInfoFromServer(String.valueOf(address.getRegional()));
                }

                areaTextView.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                areaTextView.setText(area);
            }
        }
        areaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ChooseAreaActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder errBuilder = new StringBuilder();
                String name = nameEditText.getText().toString();
                String mobile = mobileEditText.getText().toString();
                String tel = telEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String addr = addrEditText.getText().toString();
                String regional = HandleDataBase.getAreaCodeByName(areaTextView.getText().toString());
                String zip = zipEditText.getText().toString();

                if (name.isEmpty()) {
                    errBuilder.append("未填写收件人姓名\n");
                }
                if (mobile.isEmpty()) {
                    errBuilder.append("未填写手机号码\n");
                }
                if (email.isEmpty()) {
                    errBuilder.append("未填写邮箱地址\n");
                }

                if (regional == null) {
                    errBuilder.append("未选择所在地区\n");
                }
                if (addr.isEmpty()) {
                    errBuilder.append("未填写详细地址\n");
                }
                if (zip.isEmpty()) {
                    errBuilder.append("未填写邮政编码");
                }
                if (errBuilder.length() != 0) {
                    Toast toast = makeText(getBaseContext(), errBuilder.toString(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if (address == null) {
                    address = new ReceiveAddress();
                    if (DataSupport.findAll(ReceiveAddress.class).size() == 0)
                        address.setDefaulted(1);
                    else
                        address.setDefaulted(0);
                }
                address.setConsignee(name);
                address.setMobile(mobile);
                address.setTel(tel);
                address.setEmail(email);
                address.setAddress(addr);
                address.setRegional(regional);
                address.setZip(zip);
                PublicFunc.editReceiverAddr(address, new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resStr = response.body().string();
                        LogUtils.d(MyApplication.TAG, resStr);
                        try {
                            JSONObject root = new JSONObject(resStr);
                            if (root.getInt("code") == 200) {
                                String addrId = root.getJSONObject("data").getString("id");
                                address.setAddrId(addrId);
                                address.saveOrUpdate("addrId=?", addrId);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getBaseContext(), ManageAddressActivity.class);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                });
                            } else {
                                final String info = root.getString("info");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        makeText(getBaseContext(), "保存失败：" + info, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTV = (TextView) findViewById(R.id.tool_bar_title_text);
        saveButton = (Button) findViewById(R.id.id_save_button);
        nameEditText = (EditText) findViewById(R.id.id_name_edit_text);
        mobileEditText = (EditText) findViewById(R.id.id_mobile_edit_text);
        telEditText = (EditText) findViewById(R.id.id_tel_edit_text);
        emailEditText = (EditText) findViewById(R.id.id_email_edit_text);
        addrEditText = (EditText) findViewById(R.id.id_addr_edit_text);
        zipEditText = (EditText) findViewById(R.id.id_zip_edit_text);
        areaTextView = (TextView) findViewById(R.id.id_area_text_view);
        titleTV.setText("添加收货地址");
        //设置默认输入法
        nameEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);//默认中文
        addrEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);//默认中文
        mobileEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);//默认数字
        telEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        zipEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        emailEditText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_URI);//默认英文
        InputTextFifter.inputFilter(this, mobileEditText, InputTextFifter.INPUT_TYPE_DIGIT, 11);
        InputTextFifter.inputFilter(this, telEditText, InputTextFifter.INPUT_TYPE_DIGIT, 12);
        InputTextFifter.inputFilter(this, zipEditText, InputTextFifter.INPUT_TYPE_DIGIT, 6);
        nameEditText.setOnEditorActionListener(this);
        mobileEditText.setOnEditorActionListener(this);
        telEditText.setOnEditorActionListener(this);
        emailEditText.setOnEditorActionListener(this);
        addrEditText.setOnEditorActionListener(this);
        zipEditText.setOnEditorActionListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String areaStr = data.getStringExtra("area_name");
            areaCode = data.getStringExtra("area_code");
            areaTextView.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            areaTextView.setText(areaStr);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        textView.clearFocus();
        switch (textView.getId()) {
            case R.id.id_name_edit_text:
                mobileEditText.requestFocus();
                break;
            case R.id.id_mobile_edit_text:
                telEditText.requestFocus();
                break;
            case R.id.id_tel_edit_text:
                emailEditText.requestFocus();
                break;
            case R.id.id_email_edit_text:
                addrEditText.requestFocus();
                break;
            case R.id.id_addr_edit_text:
                zipEditText.requestFocus();
                break;
            case R.id.id_zip_edit_text:
                PublicFunc.closeKeyBoard(EditAddressActivity.this);
                break;
        }
        return true;
    }
}
