package com.netsun.labuy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.netsun.labuy.CommodityInfoActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.gson.ProductInfo;
import com.netsun.labuy.gson.ProductOption;
import com.netsun.labuy.utils.OptionPop;
import com.netsun.labuy.utils.PublicFunc;

import java.util.List;

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
    private TextView posterTV;
    private TextView countryText;
    private TextView cateTextView;
    private ProductInfo info;
    private String unSelect;

    public ChooseOptionFragment(ProductInfo info) {
        this.info = info;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String picUrl;
        if (info != null) {
            picUrl = PublicFunc.host + "Public/Uploads/device/" + info.pic;
            pic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(getActivity()).load(R.drawable.loading).asGif().into(pic);
            pic.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide
                    .with(getActivity())
                    .load(picUrl)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            pic.setImageDrawable(resource);
                        }
                    });
            name.setText(info.name);
            posterTV.setText(info.poster);
            countryText.setText(info.country);
            cateTextView.setText(info.category);
            price.setText("¥ " + info.price);
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
        posterTV = (TextView)view.findViewById(R.id.id_poster_tv);
        countryText = (TextView) view.findViewById(R.id.id_country_text);
        cateTextView = (TextView) view.findViewById(R.id.id_cate_text);
        return view;
    }

    public void setOptions(String str) {
        options.setText(str);
    }
}
