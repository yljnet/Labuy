package com.netsun.labuy.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.netsun.labuy.activity.ConfirmOrderActivity;
import com.netsun.labuy.activity.EditAddressActivity;
import com.netsun.labuy.activity.ManageAddressActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.adapter.ReceiveAddressAdapter;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.OnAddressRemove;
import com.netsun.labuy.utils.OnAddressSelectLinstener;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.SpaceItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/3/18.
 */

public class AddressManageFragment extends Fragment implements View.OnClickListener {
    public static final int MODE_MANAGE = 1;
    public static final int MODE_VIEW = 2;
    public static final int MODE_SELECT = 3;
    View view;
    Toolbar toolbar;
    TextView titleTextView;
    Button rightButton;
    Button addButton;
    RecyclerView addressRV;
    List<ReceiveAddress> addressList = null;
    ReceiveAddressAdapter addressAdapter = null;
    int mode = MODE_VIEW;
    OnAddressSelectLinstener onAddressSelectLinstener = new OnAddressSelectLinstener() {
        @Override
        public void SelectAddr(ReceiveAddress address) {
            if (mode != MODE_SELECT) return;
            Intent intent = new Intent(getActivity(), ConfirmOrderActivity.class);
            intent.putExtra("address", address);
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }
    };
    OnAddressRemove onAddressRemove = new OnAddressRemove() {
        @Override
        public void onRemove(final ReceiveAddress address) {
            if (addressList.contains(address)) {
                PublicFunc.deleteAddressById(address.getAddrId(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "删除地址失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resStr = response.body().string();
                        LogUtils.d(MyApplication.TAG, resStr);
                        try {
                            final JSONObject root = new JSONObject(resStr);
                            if (root.getInt("code") == 200) {
                                address.delete();
                                addressList.remove(address);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addressAdapter.notifyDataSetChanged();
                                        Toast.makeText(getActivity(), "删除地址成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                final String info = root.getString("info");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };
    Handler addressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (msg.obj != null) {
                    final ReceiveAddress address = (ReceiveAddress) msg.obj;
                    address.setDefaulted(1);
                    PublicFunc.editReceiverAddr(address, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String resStr = response.body().string();
                            LogUtils.d(MyApplication.TAG, resStr);
                            try {
                                JSONObject root = new JSONObject(resStr);
                                if (root.getInt("code") == 200) {
                                    String addrId = root.getJSONObject("data").getString("id");
                                    for (ReceiveAddress addr : addressList) {
                                        if (addr.getAddrId().equals(addrId))
                                            addr.setDefaulted(1);
                                        else
                                            addr.setDefaulted(0);
                                        addr.update(addr.getId());
                                    }
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            addressAdapter.notifyDataSetChanged();
                                            Toast.makeText(getActivity(), "设置默认地址成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            addressAdapter.notifyDataSetChanged();
                                            Toast.makeText(getActivity(), "设置默认地址失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addressAdapter.notifyDataSetChanged();
                                        Toast.makeText(getActivity(), "设置默认地址失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    };

    public static AddressManageFragment newInstance() {

        Bundle args = new Bundle();

        AddressManageFragment fragment = new AddressManageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        if (mode != MODE_MANAGE) {
            rightButton.setOnClickListener(this);
        }
        addButton.setOnClickListener(this);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_all_addresss, container, false);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        titleTextView = (TextView) view.findViewById(R.id.tool_bar_title_text);
        rightButton = (Button) view.findViewById(R.id.id_tool_bar_ritht_button);
        addButton = (Button) view.findViewById(R.id.id_add_address_button);
        addButton.setOnClickListener(this);
        addressRV = (RecyclerView) view.findViewById(R.id.id_address_rv);
        if (mode == MODE_MANAGE) {
            rightButton.setVisibility(View.GONE);
            titleTextView.setText("管理收货地址");
        } else {
            rightButton.setText("管理");
            rightButton.setVisibility(View.VISIBLE);
            titleTextView.setText("选择收货地址");
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        addressRV.setLayoutManager(layoutManager);
        addressList = DataSupport.findAll(ReceiveAddress.class);
        addressAdapter = new ReceiveAddressAdapter(addressList);
        addressRV.setAdapter(addressAdapter);
        addressAdapter.setOnAddressSelectLinstener(onAddressSelectLinstener);
        addressAdapter.setOnAddressRemove(onAddressRemove);
        addressAdapter.setHandler(addressHandler);
        if (mode == MODE_MANAGE) {
            addressAdapter.setMode(ReceiveAddressAdapter.EDIT_MODE);
        } else if (mode == MODE_VIEW) {
            addressAdapter.setMode(ReceiveAddressAdapter.VIEW_MODE);
        } else {
            addressAdapter.setMode(ReceiveAddressAdapter.SELECT_MODE);
        }
        addressRV.setBackgroundResource(R.color.secondary_text_dark);
        int space = getResources().getDimensionPixelSize(R.dimen.space_two);
        addressRV.addItemDecoration(new SpaceItemDecoration(space));
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_tool_bar_ritht_button:
                Intent manageiIntent = new Intent(getActivity(), ManageAddressActivity.class);
                manageiIntent.putExtra("mode", MODE_MANAGE);
                getActivity().startActivityForResult(manageiIntent, 1);
                break;
            case R.id.id_add_address_button:
                Intent addIntent = new Intent(getActivity(), EditAddressActivity.class);
                getActivity().startActivityForResult(addIntent, 2);
        }

    }
}
