package com.netsun.labuy.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netsun.labuy.CommodityInfoActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.gson.ProductInfo;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.OptionPop;

/**
 * Created by Administrator on 2017/3/8.
 */

public class ChooseOptionFragment extends Fragment {
    private View view;
    LinearLayout linearLayout;
    private ImageView pic;
    private TextView name;
    private TextView price;
    private TextView options;
    private TextView countryText;
    private TextView cateTextView;
    private ProductInfo info;
    private String unSelect;

    public ChooseOptionFragment(ProductInfo info, Handler handler) {
        this.info = info;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String picUrl;
        if (info != null) {
            if (LogUtils.level == LogUtils.DEBUG) {
                picUrl = "http://www.dev.labuy.cn/Public/Uploads/565d4fed470a2.jpg";
            } else {
                picUrl = "http://www.dev.labuy.cn/Public/Uploads/" + info.pic;
            }
            Glide.with(getActivity()).load(picUrl).into(pic);
            name.setText(info.name);
            countryText.setText(info.country);
            cateTextView.setText(info.category);
            price.setText("¥ " + info.price);
            if (info.options != null) {
                unSelect = ((CommodityInfoActivity) getContext()).getUnSelect();
                if (unSelect.isEmpty())
                    setOptions("请选择 " + unSelect);
                else
                    setOptions("已选 " + unSelect);
            } else {
                setOptions("已选 数量: 1 件");
            }
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((CommodityInfoActivity) getActivity()).showOptionPop(OptionPop.STYLE_SELECT);
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_yq_info_main, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.id_choose_panel);
        pic = (ImageView) view.findViewById(R.id.id_yq_pic);
        name = (TextView) view.findViewById(R.id.id_yq_name);
        price = (TextView) view.findViewById(R.id.id_yq_price);
        options = (TextView) view.findViewById(R.id.id_yq_options);
        countryText = (TextView) view.findViewById(R.id.id_country_text);
        cateTextView = (TextView)view.findViewById(R.id.id_cate_text);
        return view;
    }

    public void setOptions(String str) {
        options.setText(str);
    }
}
