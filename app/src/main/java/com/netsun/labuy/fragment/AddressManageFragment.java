package com.netsun.labuy.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.netsun.labuy.ConfirmOrderActivity;
import com.netsun.labuy.ManageAddressActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.adapter.ReceiveAddressAdapter;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.utils.OnAddressSelectLinstener;
import com.netsun.labuy.utils.SpaceItemDecoration;

import org.litepal.crud.DataSupport;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/3/18.
 */

public class AddressManageFragment extends Fragment implements View.OnClickListener {
    public static final int MODE_MANAGE = 1;
    public static final int MODE_VIEW = 2;
    View view;
    TextView titleTextView;
    Button manageButton;
    Button addButton;
    RecyclerView addressRV;
    List<ReceiveAddress> addressList;
    int mode = MODE_VIEW;
    OnAddressSelectLinstener onAddressSelectLinstener = new OnAddressSelectLinstener() {
        @Override
        public void SelectAddr(ReceiveAddress address) {
            if (mode != MODE_VIEW) return;
            Intent intent = new Intent(getActivity(), ConfirmOrderActivity.class);
            intent.putExtra("address", address);
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
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
        if (mode == MODE_VIEW) {
            manageButton.setOnClickListener(this);
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
        titleTextView = (TextView) view.findViewById(R.id.tool_bar_title_text);
        manageButton = (Button) view.findViewById(R.id.id_manage_button);
        addButton = (Button) view.findViewById(R.id.id_add_address_button);
        addButton.setOnClickListener(this);
        addressRV = (RecyclerView) view.findViewById(R.id.id_address_rv);
        if (mode == MODE_VIEW) {
            manageButton.setVisibility(View.VISIBLE);
            titleTextView.setText("选择收货地址");
        } else {
            manageButton.setVisibility(View.GONE);
            titleTextView.setText("管理收货地址");
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        addressRV.setLayoutManager(layoutManager);
        addressList = DataSupport.findAll(ReceiveAddress.class);
        ReceiveAddressAdapter addressAdapter = new ReceiveAddressAdapter(addressList);

        addressRV.setAdapter(addressAdapter);
        addressAdapter.setOnAddressSelectLinstener(onAddressSelectLinstener);
        if (mode == MODE_MANAGE) {
            addressAdapter.setMode(ReceiveAddressAdapter.EDIT_MODE);
            addressRV.setBackgroundResource(R.color.secondary_text_dark);
            int space = getResources().getDimensionPixelSize(R.dimen.space_two);
            addressRV.addItemDecoration(new SpaceItemDecoration(space));
        }else {
            addressAdapter.setMode(ReceiveAddressAdapter.VIEW_MODE);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_manage_button:
                Intent intent = new Intent(getActivity(), ManageAddressActivity.class);
                getActivity().startActivity(intent);
                break;

        }

    }
}
