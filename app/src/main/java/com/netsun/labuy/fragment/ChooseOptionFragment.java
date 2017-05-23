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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.netsun.labuy.activity.MerchandiseDisplayActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.gson.Merchandise;
import com.netsun.labuy.utils.OptionPop;
import com.netsun.labuy.utils.PublicFunc;

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
    private Merchandise merchandise;
    private String unSelect;

    public static ChooseOptionFragment newInstance(Merchandise merchandise) {

        Bundle args = new Bundle();

        ChooseOptionFragment fragment = new ChooseOptionFragment();
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
        String picUrl = null;
        if (merchandise != null) {
            if (merchandise.caterory_type.equals(PublicFunc.DEVICER))
                picUrl = PublicFunc.host + "Public/Uploads/device/" + merchandise.pic;
            else if (merchandise.caterory_type.equals(PublicFunc.PART))
                picUrl = PublicFunc.host + "Public/Uploads/parts/" + merchandise.pic;
            else picUrl = "";
            pic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(getActivity()).load(R.drawable.loading).asGif().into(pic);
            pic.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide
                    .with(getActivity())
                    .load(picUrl)
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .into(pic);
            name.setText(merchandise.name);
            posterTV.setText(merchandise.poster);
            countryText.setText(merchandise.country);
            cateTextView.setText(merchandise.category_name);
            price.setText("¥ " + merchandise.price);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MerchandiseDisplayActivity) getActivity()).showOptionPop(OptionPop.STYLE_SELECT);
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
