package com.netsun.labuy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netsun.labuy.OrderInfoActivity;
import com.netsun.labuy.OrderManagerActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.adapter.OrderManagerItemAdapter;
import com.netsun.labuy.utils.HttpUtils;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.OrderInfo;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.SpaceItemDecoration;
import com.netsun.labuy.utils.UpRefreshLayout;
import com.netsun.labuy.utils.Utility;

import org.litepal.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/24.
 */

public class OrderManagerFragment extends android.support.v4.app.Fragment {
    View view;
    UpRefreshLayout orderManagerRefreshLayout;
    RecyclerView orderRecyclerView;
    List<OrderInfo> orderInfos;
    OrderManagerItemAdapter adapter;
    String title;
    int mode;
    int currentPage = 1;
    Handler ItemClickHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int position = msg.arg1;
                if (position < orderInfos.size()) {
                    OrderInfo info = orderInfos.get(position);
                    if (info.getOrderId() != null && !info.getOrderId().isEmpty()) {
                        Intent intent = new Intent(getActivity(), OrderInfoActivity.class);
                        intent.putExtra("order_id", info.getOrderId());
                        startActivity(intent);
                    }
                }
            }
        }
    };

    public String getTitle() {
        return title;
    }

    public OrderManagerFragment(String title, int mode) {
        this.title = title;
        this.mode = mode;
        orderInfos = new ArrayList<OrderInfo>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_order_list, container, false);
        orderManagerRefreshLayout = (UpRefreshLayout) view.findViewById(R.id.order_manager_refresh_layout);
        orderRecyclerView = (RecyclerView) view.findViewById(R.id.id_order_recycle);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new OrderManagerItemAdapter(orderInfos);
        adapter.setHandler(ItemClickHandler);
        orderRecyclerView.setAdapter(adapter);
        int space = getResources().getDimensionPixelSize(R.dimen.verital_space);
        orderRecyclerView.addItemDecoration(new SpaceItemDecoration(space));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getOrderInfos(currentPage);
        orderManagerRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                getOrderInfos(currentPage);
            }
        });
    }

    public void getOrderInfos(int page) {
        PublicFunc.showProgress(getActivity());
        String url = null;
        if (mode == OrderManagerActivity.ORDER_ALL) {
            url = PublicFunc.host + "app.php/Order?token=" + MyApplication.token + "&page=" + page;
        } else if (mode == OrderManagerActivity.ORDER_UNPAY) {
            url = PublicFunc.host + "app.php/Order?token=" + MyApplication.token + "&page=" + page + "&payStatus=0";
        } else if (mode == OrderManagerActivity.ORDER_PAYED) {
            url = PublicFunc.host + "app.php/Order?token=" + MyApplication.token + "&page=" + page + "&payStatus=1";
        } else {
            url = PublicFunc.host + "app.php/Order?token=" + MyApplication.token + "&page=" + page;
        }
        LogUtil.d(MyApplication.TAG, url);
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                orderManagerRefreshLayout.refreshFinish(UpRefreshLayout.REFRESH_FAIL);
                if (currentPage > 1)
                    currentPage--;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PublicFunc.closeProgress();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body().string();
                ArrayList<OrderInfo> result = Utility.handleOrderListResponse(resStr);
                if (result != null) {
                    for (OrderInfo orderInfo : result) {
                        orderInfos.add(orderInfo);
                    }
                } else {
                    currentPage--;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PublicFunc.closeProgress();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
