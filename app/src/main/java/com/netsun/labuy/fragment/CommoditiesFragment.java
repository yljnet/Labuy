package com.netsun.labuy.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netsun.labuy.R;
import com.netsun.labuy.SearchYQByKeyActivity;
import com.netsun.labuy.adapter.CommodityAdapter;
import com.netsun.labuy.adapter.MenuItemAdapter;
import com.netsun.labuy.db.First;
import com.netsun.labuy.db.Second;
import com.netsun.labuy.db.Third;
import com.netsun.labuy.gson.Commodity;
import com.netsun.labuy.gson.YQList;
import com.netsun.labuy.utils.HttpUtils;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.UpRefreshLayout;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.netsun.labuy.utils.PublicFunc.closeProgress;

/**
 * Created by Administrator on 2017/2/27.
 */

public class CommoditiesFragment extends Fragment {
    public static final int LEVEL_FIRST = 0;
    public static final int LEVEL_SECOND = 1;
    public static final int LEVEL_THIRD = 2;

    private View view;
    private DrawerLayout drawerLayout;
    private RecyclerView commoditiesView;
    private ImageView checkImage;
    private TextView searchTextView;
    private UpRefreshLayout refreshLayout;
    private RelativeLayout failLayout;
    private ListView mLeftMenu;
    private View mLeftHeadView;
    private ImageView mLeftMenuHeadImage;
    private TextView mLeftMenuHeadTitleTextView;
    private TextView toTopButton;
    GridLayoutManager gridLayoutManager;

    /**
     * 这里是商品列表属性
     */
    private String cate_id;
    private String product;
    private List<Commodity> commodityList = new ArrayList<Commodity>();
    private CommodityAdapter adapter;
    private int currentPage = 1;

    /**
     * 以下是分类菜单属性
     */
    MenuItemAdapter leftMenuAdapter;
    private int currentLevel = LEVEL_FIRST;
    private ArrayList<String> dataList = new ArrayList<String>();
    //一级类列表
    private List<First> firstTypes;
    //二级类列表
    private List<Second> secondTypes;
    //三级类列表
    private List<Third> thirdTypes;
    //选中的一级类
    private First selectedFirst;
    //选中的二级类
    private Second selectedSecond;
    //选中的三级类
    private Third selectedThird;

    public static CommoditiesFragment newInstance() {

        Bundle args = new Bundle();

        CommoditiesFragment fragment = new CommoditiesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryFirst();
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        searchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchYQByKeyActivity.class);
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtils.d(MyApplication.TAG, PublicFunc.work);
                currentPage++;
                if (TextUtils.equals(PublicFunc.work, "search_yq_by_cate_id")) {
                    searchYQByCateId(currentPage, cate_id);
                } else if (TextUtils.equals("search_qy_by_key", PublicFunc.work)) {
                    searchYQByName(currentPage, product);
                }
            }
        });

        leftMenuAdapter = new MenuItemAdapter(drawerLayout.getContext(), dataList);
        mLeftMenu.setAdapter(leftMenuAdapter);
        mLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (0 == i) return;
                switch (currentLevel) {
                    case LEVEL_FIRST:
                        selectedFirst = firstTypes.get(i - 1);
                        querySecond();
                        break;
                    case LEVEL_SECOND:
                        selectedSecond = secondTypes.get(i - 1);
                        queryThird();
                        break;
                    case LEVEL_THIRD:
                        selectedThird = thirdTypes.get(i - 1);
                        cate_id = selectedThird.getId();
                        commodityList.clear();
                        currentPage = 1;
                        searchYQByCateId(currentPage, cate_id);
                        drawerLayout.closeDrawers();
                        break;
                    default:
                        break;
                }
            }
        });
        mLeftMenuHeadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentLevel) {
                    case LEVEL_THIRD:
                        querySecond();
                        break;
                    case LEVEL_SECOND:
                        queryFirst();
                        break;
                    default:
                        break;
                }
            }
        });
        toTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commoditiesView.getLayoutManager().smoothScrollToPosition(commoditiesView, null, 0);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.layout_commodities, container, false);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        mLeftMenu = (ListView) view.findViewById(R.id.id_lv_left_menu);
        mLeftHeadView = inflater.inflate(R.layout.layout_left_menu_head, mLeftMenu, false);
        mLeftMenuHeadImage = (ImageView) mLeftHeadView.findViewById(R.id.id_head_img_left);
        mLeftMenuHeadTitleTextView = (TextView) mLeftHeadView.findViewById(R.id.id_head_text_view);
        mLeftMenuHeadImage.setVisibility(View.GONE);
        mLeftMenu.addHeaderView(mLeftHeadView);
        commoditiesView = (RecyclerView) view.findViewById(R.id.recycle_view);
        checkImage = (ImageView) view.findViewById(R.id.check_image);
        searchTextView = (TextView) view.findViewById(R.id.search_edit);
        refreshLayout = (UpRefreshLayout) view.findViewById(R.id.refresh_layout);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        commoditiesView.setLayoutManager(gridLayoutManager);
        adapter = new CommodityAdapter(commodityList);
        commoditiesView.setAdapter(adapter);
        toTopButton = (TextView) view.findViewById(R.id.id_button_to_top);
        failLayout = (RelativeLayout) view.findViewById(R.id.id_search_fail_layout);
        if (TextUtils.equals(PublicFunc.work, "search_yq_by_cate_id")) {
            searchYQByCateId(1, "11");
        } else if (TextUtils.equals(PublicFunc.work, "search_qy_by_key")) {
            Intent intent = getActivity().getIntent();
            if (null != intent) {
                String name = intent.getStringExtra("name");
                searchYQByName(1, name);
            }
        }
        return view;
    }

    public void searchYQByCateId(final int pageId, String cate) {
        currentPage = pageId;
        cate_id = cate;
        String url = "http://www.dev.labuy.cn/app.php/Yq?model=device&page=" +
                pageId + "&fid=" + cate;
        LogUtils.d(MyApplication.TAG, url);
        PublicFunc.showProgress(getActivity());
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PublicFunc.closeProgress();
                refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_FAIL);
                if (pageId == 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setVisibility(View.GONE);
                            failLayout.setVisibility(View.VISIBLE);
                        }
                    });
                } else
                    currentPage--;
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                PublicFunc.closeProgress();
                String restext = response.body().string();
                LogUtils.d(MyApplication.TAG, restext);
                final YQList yqList = PublicFunc.handleYQListResponse(restext);
                if (200 == yqList.code && yqList.commodities != null) {
                    LogUtils.d(MyApplication.TAG, "" + yqList.commodities.size());
                    final int index = commodityList.size();
                    for (int i = 0; i < yqList.commodities.size(); i++) {
                        commodityList.add(yqList.commodities.get(i));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setVisibility(View.VISIBLE);
                            failLayout.setVisibility(View.GONE);
                            commoditiesView.getLayoutManager().smoothScrollToPosition(commoditiesView, null, index);
                            adapter.notifyDataSetChanged();
                            refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_SUCCESS);

                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pageId == 1) {
                                refreshLayout.setVisibility(View.GONE);
                                failLayout.setVisibility(View.VISIBLE);
                            } else {
                                currentPage--;
                                refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_FAIL);
                                Toast.makeText(getActivity(), "没有更多商品了", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void searchYQByName(final int pageId, String yqName) {
        product = yqName;
        currentPage = pageId;
        String url = "http://www.dev.labuy.cn/app.php/Yq?model=device&page=" + pageId + "&product=" + yqName;
        LogUtils.d(MyApplication.TAG, url);
        PublicFunc.showProgress(getActivity());
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PublicFunc.closeProgress();
                if (pageId == 1) {
                    refreshLayout.setVisibility(View.GONE);
                    failLayout.setVisibility(View.VISIBLE);
                } else {
                    currentPage--;
                }
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                PublicFunc.closeProgress();
                String restext = response.body().string();
                LogUtils.d(MyApplication.TAG, restext);
                final YQList yqList = PublicFunc.handleYQListResponse(restext);
                if (200 == yqList.code && yqList.commodities != null) {
                    LogUtils.d(MyApplication.TAG, "" + yqList.commodities.size());
                    final int index = commodityList.size();
                    for (int i = 0; i < yqList.commodities.size(); i++) {
                        commodityList.add(yqList.commodities.get(i));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setVisibility(View.VISIBLE);
                            failLayout.setVisibility(View.GONE);
                            commoditiesView.getLayoutManager().smoothScrollToPosition(commoditiesView, null, index);
                            adapter.notifyDataSetChanged();
                            refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_SUCCESS);
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pageId == 1) {
                                refreshLayout.setVisibility(View.GONE);
                                failLayout.setVisibility(View.VISIBLE);
                            } else {
                                currentPage--;
                                refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_FAIL);
                                Toast.makeText(getActivity(), "没有更多商品了", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    /**
     * 查询一级类，优先从数据库查询，如果没有再到服务器上查询
     */
    private void queryFirst() {
        currentLevel = LEVEL_FIRST;
        firstTypes = DataSupport.findAll(First.class);
        if (firstTypes.size() > 0) {
            dataList.clear();
            for (First first : firstTypes) {
                dataList.add(first.getName());
            }
            mLeftMenuHeadImage.setVisibility(View.GONE);
            mLeftMenuHeadTitleTextView.setText("分类检索");
            leftMenuAdapter.notifyDataSetChanged();
        } else {
            String url = "http://www.dev.labuy.cn/app.php/Yq?&model=type&fid=0";
            queryFromServer(url, LEVEL_FIRST);
        }
    }

    /**
     * 加载二级类，优先从数据库查询，如果没有再到服务器获取
     */
    private void querySecond() {
        currentLevel = LEVEL_SECOND;
        secondTypes = DataSupport.where("parentId=?", String.valueOf(selectedFirst.getId())).find(Second.class);
        if (secondTypes.size() > 0) {
            dataList.clear();
            for (Second second : secondTypes) {
                dataList.add(second.getName());
            }
            mLeftMenuHeadImage.setVisibility(View.VISIBLE);
            mLeftMenuHeadTitleTextView.setText(selectedFirst.getName());
            leftMenuAdapter.notifyDataSetChanged();
        } else {
            String url = "http://www.dev.labuy.cn/app.php/Yq?&model=type&fid=" + selectedFirst.getId();
            queryFromServer(url, LEVEL_SECOND);
        }
    }

    private void queryThird() {
        currentLevel = LEVEL_THIRD;
        thirdTypes = DataSupport.where("parentId=?", String.valueOf(selectedSecond.getId())).find(Third.class);
        if (thirdTypes.size() > 0) {
            dataList.clear();
            for (Third third : thirdTypes) {
                dataList.add(third.getName());
            }
            mLeftMenuHeadImage.setVisibility(View.VISIBLE);
            mLeftMenuHeadTitleTextView.setText(selectedSecond.getName());
            leftMenuAdapter.notifyDataSetChanged();
        } else {
            String url = "http://www.dev.labuy.cn/app.php/Yq?&model=type&fid=" + selectedSecond.getId();
            queryFromServer(url, LEVEL_THIRD);
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询分类数据
     *
     * @param url
     * @param type
     */
    private void queryFromServer(String url, final int type) {
        PublicFunc.showProgress(getActivity());
        LogUtils.d(MyApplication.TAG, url);
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (currentLevel > LEVEL_FIRST)
                    currentLevel--;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgress();
                        Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resText = response.body().string();
                LogUtils.d(MyApplication.TAG, resText);
                boolean handleResult = false;
                if (LEVEL_FIRST == type) {
                    handleResult = PublicFunc.handleFirstResponse(resText);
                } else if (LEVEL_SECOND == type) {
                    handleResult = PublicFunc.handleSecondResponse(resText, selectedFirst.getId());
                } else if (LEVEL_THIRD == type) {
                    handleResult = PublicFunc.handleThirdResponse(resText, selectedSecond.getId());
                }
                if (handleResult) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PublicFunc.closeProgress();
                            if (LEVEL_FIRST == type) {
                                queryFirst();
                            } else if (LEVEL_SECOND == type) {
                                querySecond();
                            } else if (LEVEL_THIRD == type) {
                                queryThird();
                            }
                        }
                    });
                }
            }
        });
    }

}
