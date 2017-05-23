package com.netsun.labuy.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.activity.ManageAddressActivity;
import com.netsun.labuy.adapter.ShoppingCartItemAdapter;
import com.netsun.labuy.db.Goods;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.utils.HandleDataBase;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.SpaceItemDecoration;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/21.
 */

public class OrderInfoFragment extends Fragment {
    public static final int TYPE_CORFIRM_ORDER = 1;
    public static final int TYPE_VIEW_ORDER = 2;
    RelativeLayout defaultAddrLayout;
    LinearLayout remarkLayout;
    TextView consigneeTextView;//收件人
    TextView mobileTextView;//手机
    TextView addressTextView;
    ImageView nextImageView;
    RecyclerView orderListView;
    EditText remarkEDIT;

    View view;
    int currentType = TYPE_VIEW_ORDER;
    ReceiveAddress currentAddr;
    ArrayList<Goods> goodsList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_order_info, container, false);
        initView(view);
        if (currentAddr != null) {
            consigneeTextView.setText("收件人："+currentAddr.getConsignee());
            if (currentAddr.getMobile() != null && !currentAddr.getMobile().isEmpty())
                mobileTextView.setText(currentAddr.getMobile());
            else
                mobileTextView.setText(currentAddr.getTel());
            String sCountyName;
            while ((sCountyName = HandleDataBase.getAreaById(currentAddr.getRegional())) == null) {
                PublicFunc.getAreaInfoFromServer(currentAddr.getRegional());
            }
            addressTextView.setText(sCountyName + currentAddr.getAddress());
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderListView.setLayoutManager(layoutManager);
        ShoppingCartItemAdapter adapter = new ShoppingCartItemAdapter(goodsList);
        adapter.setStyle(ShoppingCartItemAdapter.STYLE_ORDER);
        orderListView.setAdapter(adapter);
        int space = getResources().getDimensionPixelSize(R.dimen.space_two);
        orderListView.addItemDecoration(new SpaceItemDecoration(space));
        if (currentType == TYPE_CORFIRM_ORDER) {
            defaultAddrLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //选择默认收件地址
                    Intent intent = new Intent(getActivity(), ManageAddressActivity.class);
                    intent.putExtra("mode", AddressManageFragment.MODE_SELECT);
                    getActivity().startActivityForResult(intent, 1);
                }
            });
        }
        return view;
    }

    private void initView(View view) {
        defaultAddrLayout = (RelativeLayout) view.findViewById(R.id.id_default_address_rl);
        consigneeTextView = (TextView) view.findViewById(R.id.id_consignee_text_view);
        mobileTextView = (TextView) view.findViewById(R.id.id_mobile_text_view);
        addressTextView = (TextView) view.findViewById(R.id.id_address_text_view);
        nextImageView = (ImageView) view.findViewById(R.id.id_next_pic);
        orderListView = (RecyclerView) view.findViewById(R.id.id_order_list_view);
        remarkEDIT = (EditText) view.findViewById(R.id.id_remark_edit_text);
        remarkLayout = (LinearLayout) view.findViewById(R.id.ll_remark);
        if (currentType == TYPE_CORFIRM_ORDER) {
            remarkLayout.setVisibility(View.VISIBLE);
            remarkEDIT.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return true;
                }
            });
        } else {
            nextImageView.setVisibility(View.GONE);
            remarkLayout.setVisibility(View.GONE);

        }
    }

    public static OrderInfoFragment newInstance() {

        Bundle args = new Bundle();

        OrderInfoFragment fragment = new OrderInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCurrentType(int type) {
        this.currentType = type;
    }

    public void setAddrInfo(ReceiveAddress address) {
        currentAddr = address;
    }

    public ReceiveAddress getCurrentAddress() {
        return currentAddr;
    }

    public String getRemark() {
        return remarkEDIT.getText().toString();
    }

    public void setShoppingList(ArrayList<Goods> list) {
        this.goodsList = list;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
