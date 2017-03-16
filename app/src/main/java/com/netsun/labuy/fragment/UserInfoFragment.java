package com.netsun.labuy.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.netsun.labuy.MainActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.db.ShoppingItem;
import com.netsun.labuy.utils.LogoutListener;
import com.netsun.labuy.utils.PublicFunc;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/2/28.
 */

public class UserInfoFragment extends Fragment {
    private View view;

    private Button buttonLogout;

    public static UserInfoFragment newInstance() {

        Bundle args = new Bundle();

        UserInfoFragment fragment = new UserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_user_info, container, false);
        buttonLogout = (Button) view.findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicFunc.logout(new LogoutListener() {
                    @Override
                    public void onLogoutResult(boolean result) {
                        if (result) {
                            DataSupport.deleteAll(ShoppingItem.class);//清除购物车信息
                            DataSupport.deleteAll(ReceiveAddress.class);//清除收件地址信息
                            ((MainActivity)getActivity()).setFragment(0);
                            PublicFunc.toLogin(getActivity(),0);
                        }

                    }
                });
            }
        });
        return view;
    }
}
