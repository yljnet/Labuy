package com.netsun.labuy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.netsun.labuy.R;
import com.netsun.labuy.adapter.DetialItem;
import com.netsun.labuy.adapter.ProductDetialAdapter;
import com.netsun.labuy.gson.Merchandise;
import com.netsun.labuy.gson.ProductMethod;
import com.netsun.labuy.gson.ProductParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/8.
 */

public class ProductInfoDetailsFragment extends Fragment {
    View view;
    ListView detialView;

    Merchandise merchandise;
    ArrayList<String> paramlist = new ArrayList<String>();
    String[] names = {"名称", "适用行业领域", "检测样品", "目标检测物", "参考标准名称"};

    public static ProductInfoDetailsFragment newInstance(Merchandise merchandise) {

        Bundle args = new Bundle();

        ProductInfoDetailsFragment fragment = new ProductInfoDetailsFragment();
        args.putParcelable("goods", merchandise);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
            merchandise = bundle.getParcelable("goods");
        else
            Toast.makeText(getContext(), "界面初始化失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<DetialItem> detials = new ArrayList<DetialItem>();
        if (merchandise != null) {
            if (merchandise.params != null) {
                detials.add(new DetialItem(DetialItem.TYPE_TITLE, "产品主要参数"));
                for (ProductParam param : merchandise.params) {
                    String s = param.name + ":" + param.value;
                    detials.add(new DetialItem(DetialItem.TYPE_TEXT, s));
                    paramlist.add(s);
                }
            }
            if (merchandise.intro != null) {
                detials.add(new DetialItem(DetialItem.TYPE_TITLE, "产品介绍"));
                detials.add(new DetialItem(DetialItem.TYPE_HTML, merchandise.intro));
            }

            if(merchandise.appMethods != null) {
                detials.add(new DetialItem(DetialItem.TYPE_TITLE, "应用方法"));
                ProductMethod head = new ProductMethod();
                head.name = "名称";
                head.cate_hy = "适用行业领域";
                head.sample = "检测样品";
                head.target = "目标检测物";
                head.standard = "参考标准名称";
                detials.add(new DetialItem(head));
                for (ProductMethod method : merchandise.appMethods) {
                    detials.add(new DetialItem(method));
                }
            }
        }
        ProductDetialAdapter adapter = new ProductDetialAdapter(getContext(),detials);
        detialView.setAdapter(adapter);
    }

    /**
     * 重新计算ListView的高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_product_info_details, container, false);
        detialView = (ListView)view.findViewById(R.id.list_detial);
        return view;
    }
}
