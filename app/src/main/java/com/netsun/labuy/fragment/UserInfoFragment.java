package com.netsun.labuy.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.netsun.labuy.OrderManagerActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.SettingsActivity;
import com.netsun.labuy.utils.PublicFunc;

/**
 * Created by Administrator on 2017/2/28.
 */

public class UserInfoFragment extends Fragment implements View.OnClickListener {
    private View view;
    TextView settingsButton;
    Button allOrdersButton;
    Button unPayOrderButton;
    Button payedOrderButton;

    public static UserInfoFragment newInstance() {

        Bundle args = new Bundle();

        UserInfoFragment fragment = new UserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settingsButton.setOnClickListener(this);
        allOrdersButton.setOnClickListener(this);
        unPayOrderButton.setOnClickListener(this);
        payedOrderButton.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_user_info, container, false);
        settingsButton = (TextView) view.findViewById(R.id.id_settings_button);
        allOrdersButton = (Button) view.findViewById(R.id.id_all_order_button);
        unPayOrderButton = (Button) view.findViewById(R.id.id_un_pay_button);
        payedOrderButton = (Button) view.findViewById(R.id.id_payed_button);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_settings_button:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivityForResult(intent, PublicFunc.REQUEST_SETTINGS);
                break;
            case R.id.id_all_order_button:
                Intent intentAll = new Intent(getActivity(), OrderManagerActivity.class);
                intentAll.putExtra("mode",OrderManagerActivity.ORDER_ALL);
                startActivity(intentAll);
                break;
            case R.id.id_un_pay_button:
                Intent intent_un_pay = new Intent(getActivity(), OrderManagerActivity.class);
                intent_un_pay.putExtra("mode",OrderManagerActivity.ORDER_UNPAY);
                startActivity(intent_un_pay);
                break;
            case R.id.id_payed_button:
                Intent intent_payed = new Intent(getActivity(), OrderManagerActivity.class);
                intent_payed.putExtra("mode",OrderManagerActivity.ORDER_PAYED);
                startActivity(intent_payed);
                break;
            default:
                break;
        }
    }
}
