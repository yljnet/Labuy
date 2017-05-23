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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.netsun.labuy.activity.CategoryActivity;
import com.netsun.labuy.activity.MerchandiseDisplayActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.activity.SearchResultActivity;
import com.netsun.labuy.activity.SearchYQByKeyActivity;
import com.netsun.labuy.adapter.ShowAdapter;
import com.netsun.labuy.bean.Category;
import com.netsun.labuy.utils.CarouselView;
import com.netsun.labuy.utils.PublicFunc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.netsun.labuy.utils.PublicFunc.DEVICER;
import static com.netsun.labuy.utils.PublicFunc.PART;

/**
 * Created by Administrator on 2017/4/12.
 */

public class HomeFragment extends Fragment {
    private Button categoryButton;
    private TextView searchTextView;
    private CarouselView carouselView;
    RecyclerView CategoryRecyclerView;

    String[] imageUrls = {PublicFunc.host + "Public/images/top/pic_1.jpg",
            PublicFunc.host + "/Public/images/top/pic_2.jpg",
            PublicFunc.host + "/Public/images/top/pic_3.jpg",
            PublicFunc.host + "/Public/images/top/pic_4.jpg"};
    String[] captions = {"气相色谱法", "液相色谱法", "分光光度法", "原子吸收光谱法", "质谱法", "滴定法"};

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_home, container, false);
        categoryButton = (Button) view.findViewById(R.id.check_button);
        carouselView = (CarouselView) view.findViewById(R.id.id_carousel);
        searchTextView = (TextView) view.findViewById(R.id.search_edit);
        CategoryRecyclerView = (RecyclerView) view.findViewById(R.id.id_class_list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                startActivity(intent);
            }
        });
        searchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchYQByKeyActivity.class);
                startActivity(intent);
            }
        });
        carouselView.setAdapter(new CarouselView.Adapter() {
            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public View getView(int position) {
                 View view = LayoutInflater.from(getActivity()).inflate(R.layout.carousel_item, null);
                ImageView img = (ImageView) view.findViewById(R.id.id_carousel_image);
                Glide
                        .with(getActivity())
                        .load(imageUrls[position])
                        .into(img);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String imageUrl = (String) view.getTag();
                        Intent intent;
                        Toast.makeText(getActivity(), "url=" + imageUrl, Toast.LENGTH_SHORT).show();
                        if (imageUrl.contains("pic_1.jpg")) {
                            //-----点击事件----
                            intent = new Intent(getActivity(), MerchandiseDisplayActivity.class);
                            intent.putExtra("mode",PublicFunc.DEVICER);
                            intent.putExtra("id", "60");
                            getActivity().startActivity(intent);
                        } else if (imageUrl.contains("pic_2.jpg")) {
                            intent = new Intent(getActivity(), MerchandiseDisplayActivity.class);
                            intent.putExtra("mode",PublicFunc.DEVICER);
                            intent.putExtra("id", "124");
                            getActivity().startActivity(intent);
                        } else if (imageUrl.contains("pic_3.jpg")) {
                            intent = new Intent(getActivity(), MerchandiseDisplayActivity.class);
                            intent.putExtra("mode",PublicFunc.DEVICER);
                            intent.putExtra("id", "9");
                            getActivity().startActivity(intent);
                        } else if (imageUrl.contains("pic_4.jpg")) {
                            intent = new Intent(getActivity(), SearchResultActivity.class);
                            intent.putExtra("mode",PublicFunc.PART);
                            intent.putExtra("type", PublicFunc.SEARCH_BY_ID);
                            intent.putExtra("cate_id", "1112");
                            getActivity().startActivity(intent);
                        }
                    }
                });
                img.setTag(imageUrls[position]);
                return view;
            }

            @Override
            public int getCount() {
                return imageUrls.length;
            }
        });
        CategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<List<Category>> categoryLists = new ArrayList<List<Category>>();
        List<Category> qixiang = new ArrayList<Category>();
        qixiang.add(new Category("气相色谱仪", PublicFunc.host + "Public/shop/images/yqmall/qi-1.jpg", "111211", DEVICER));
        qixiang.add(new Category("毛细管柱", PublicFunc.host + "Public/shop/images/yqmall/qi-2.jpg", "111111", PART));
        qixiang.add(new Category("气体发生器", PublicFunc.host + "Public/shop/images/yqmall/qi-3.jpg", "1222", DEVICER));
        qixiang.add(new Category("填充柱", PublicFunc.host + "Public/shop/images/yqmall/qi-4.jpg", "111112", PART));
        qixiang.add(new Category("进样针", PublicFunc.host + "Public/shop/images/yqmall/qi-5.jpg", "121316", PART));
        categoryLists.add(qixiang);
        List<Category> ye = new ArrayList<Category>();
        ye.add(new Category("液相色谱仪", PublicFunc.host + "Public/shop/images/yqmall/ye-1.jpg", "111212", DEVICER));
        ye.add(new Category("液相色谱柱", PublicFunc.host + "Public/shop/images/yqmall/ye-2.jpg", "1112", PART));
        ye.add(new Category("手动进样器", PublicFunc.host + "Public/shop/images/yqmall/ye-3.jpg", "121322", PART));
        ye.add(new Category("脱气机", PublicFunc.host + "Public/shop/images/yqmall/ye-4.jpg", "122222", DEVICER));
        ye.add(new Category("自动进样器", PublicFunc.host + "Public/shop/images/yqmall/ye-5.jpg", "121322", PART));
        categoryLists.add(ye);
        List<Category> light = new ArrayList<Category>();
        light.add(new Category("分光光度计", PublicFunc.host + "Public/shop/images/yqmall/light-1.jpg", "111111", DEVICER));
        light.add(new Category("样品池/比色皿", PublicFunc.host + "Public/shop/images/yqmall/light-2.jpg", "141711", PART));
        light.add(new Category("恒温水浴", PublicFunc.host + "Public/shop/images/yqmall/light-3.jpg", "121613", DEVICER));
        light.add(new Category("超声清洗机", PublicFunc.host + "Public/shop/images/yqmall/light-4.jpg", "121113", DEVICER));
        light.add(new Category("氘灯", PublicFunc.host + "Public/shop/images/yqmall/light-5.jpg", "121233", PART));
        categoryLists.add(light);
        List<Category> yuan = new ArrayList<Category>();
        yuan.add(new Category("原子吸收光谱仪", PublicFunc.host + "Public/shop/images/yqmall/yuan-1.jpg", "1111", DEVICER));
        yuan.add(new Category("微波消解", PublicFunc.host + "Public/shop/images/yqmall/yuan-2.jpg", "121216", DEVICER));
        yuan.add(new Category("元素灯", PublicFunc.host + "Public/shop/images/yqmall/yuan-3.jpg", "1425", PART));
        yuan.add(new Category("石墨管", PublicFunc.host + "Public/shop/images/yqmall/yuan-4.jpg", "142711", PART));
        yuan.add(new Category("雾化器", PublicFunc.host + "Public/shop/images/yqmall/yuan-5.jpg", "121236", PART));
        categoryLists.add(yuan);
        List<Category> zhi = new ArrayList<Category>();
        zhi.add(new Category("气相色谱-质谱仪", PublicFunc.host + "Public/shop/images/yqmall/zhi-1.jpg", "111318", DEVICER));
        zhi.add(new Category("液相色谱-质谱仪", PublicFunc.host + "Public/shop/images/yqmall/zhi-2.jpg", "111319", DEVICER));
        zhi.add(new Category("电感耦合等离子体质谱仪", PublicFunc.host + "Public/shop/images/yqmall/zhi-3.jpg", "111317", DEVICER));
        zhi.add(new Category("前级泵", PublicFunc.host + "Public/shop/images/yqmall/zhi-4.jpg", "131411", PART));
        zhi.add(new Category("灯丝", PublicFunc.host + "Public/shop/images/yqmall/zhi-5.jpg", "1311", PART));
        categoryLists.add(zhi);
        List<Category> di = new ArrayList<Category>();
        di.add(new Category("滴定仪", PublicFunc.host + "Public/shop/images/yqmall/di-1.jpg", "111811", DEVICER));
        di.add(new Category("天平", PublicFunc.host + "Public/shop/images/yqmall/di-2.jpg", "171111", DEVICER));
        di.add(new Category("干燥箱", PublicFunc.host + "Public/shop/images/yqmall/di-3.jpg", "121611", DEVICER));
        di.add(new Category("马弗炉", PublicFunc.host + "Public/shop/images/yqmall/di-4.jpg", "121612", DEVICER));
        di.add(new Category("振荡", PublicFunc.host + "Public/shop/images/yqmall/di-5.jpg", "1215", DEVICER));
        categoryLists.add(di);
        ShowAdapter adapter = new ShowAdapter(Arrays.asList(captions), categoryLists);
        CategoryRecyclerView.setAdapter(adapter);
    }
}
