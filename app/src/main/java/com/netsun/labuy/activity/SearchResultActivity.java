package com.netsun.labuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.netsun.labuy.R;
import com.netsun.labuy.adapter.MerchandiseAdapter;
import com.netsun.labuy.gson.Merchandise;
import com.netsun.labuy.utils.HttpUtils;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.SpaceItemDecoration;
import com.netsun.labuy.utils.UpRefreshLayout;
import com.netsun.labuy.utils.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.netsun.labuy.utils.PublicFunc.DEVICER;
import static com.netsun.labuy.utils.PublicFunc.PART;
import static com.netsun.labuy.utils.PublicFunc.SEARCH_BY_ID;
import static com.netsun.labuy.utils.PublicFunc.SEARCH_BY_KEY;

public class SearchResultActivity extends AppCompatActivity implements View.OnTouchListener {
    private Toolbar toolbar;
    private TextView titleTV;
    private UpRefreshLayout refreshLayout;
    private RecyclerView goodsListView;
    private TextView toTop;
    private RelativeLayout failLayout;
    /**
     * 这里是商品列表属性
     */
    private String mode; //商品类型，仪器 |配件耗材
    private String cate_id;//商品类型编号
    private String product;//商品关键字

    //商品信息列表
    private List<Merchandise> merchandiseList = new ArrayList<Merchandise>();

    private MerchandiseAdapter adapter;

    private int currentPage = 1;
    /**
     * 查询模式
     * SEARCH_BY_ID 按商品所属类别查询
     * SEARCH_BY_KEY 按关键字查询
     */
    private int type = SEARCH_BY_ID;

    GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initView();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleTV.setText("搜索结果");
        goodsListView.setOnTouchListener(this);
        layoutManager = new GridLayoutManager(this, 2);
        goodsListView.setLayoutManager(layoutManager);
        adapter = new MerchandiseAdapter(merchandiseList);
        goodsListView.setAdapter(adapter);
        goodsListView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.grid_space)));

        Intent intent = getIntent();
        if (null != intent) {
            mode = intent.getStringExtra("mode");
            type = intent.getIntExtra("type", -1);
            if (SEARCH_BY_ID == type) {
                String cate_id = intent.getStringExtra("cate_id");
                searchByCateId(1, mode, cate_id);
            } else if (SEARCH_BY_KEY == type) {
                String name = intent.getStringExtra("name");
                searchByName(1, mode, name);
            }
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                if (type == SEARCH_BY_ID) {
                    searchByCateId(currentPage, mode, cate_id);
                } else if (SEARCH_BY_KEY == type) {
                    searchByName(currentPage, mode, product);
                }
            }
        });
        toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (merchandiseList.size() > 20)
                    goodsListView.scrollToPosition(19);//先定位到第20项
                goodsListView.getLayoutManager().smoothScrollToPosition(goodsListView, null, 0);//滑动到顶部
            }
        });
        goodsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Glide.with(getBaseContext()).resumeRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        Glide.with(getBaseContext()).pauseRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Glide.with(getBaseContext()).pauseRequests();
                }
            }
        });
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTV = (TextView) findViewById(R.id.tool_bar_title_text);
        refreshLayout = (UpRefreshLayout) findViewById(R.id.refresh_layout);
        goodsListView = (RecyclerView) findViewById(R.id.recycle_view);
        toTop = (TextView) findViewById(R.id.id_button_to_top);
        failLayout = (RelativeLayout) findViewById(R.id.id_search_fail_layout);
    }

    public void searchByCateId(final int pageId, final String mode, String cate) {
        currentPage = pageId;
        cate_id = cate;
        String url = "";
        if (mode.equals(DEVICER)) {
            url = PublicFunc.host + "app.php/Yq?model=device&page=" +
                    pageId + "&fid=" + cate;
        } else if (mode.equals(PART)) {
            url = PublicFunc.host + "app.php/Parts?model=parts&page=" +
                    pageId + "&fid=" + cate;
        } else
            return;
        LogUtils.d(MyApplication.TAG, url);
        PublicFunc.showProgress(this);
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PublicFunc.closeProgress();
                refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_FAIL);
                if (pageId == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setVisibility(View.GONE);
                            toTop.setVisibility(View.GONE);
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
                final List<Merchandise> result = Utility.handleMerchandiseListResponse(mode,restext);
                if (result != null) {
                    final int index = merchandiseList.size();
                    merchandiseList.addAll(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setVisibility(View.VISIBLE);
                            toTop.setVisibility(View.VISIBLE);
                            failLayout.setVisibility(View.GONE);
                            goodsListView.getLayoutManager().smoothScrollToPosition(goodsListView, null, index);
                            adapter.notifyDataSetChanged();
                            refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_SUCCESS);

                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pageId == 1) {
                                refreshLayout.setVisibility(View.GONE);
                                toTop.setVisibility(View.GONE);
                                failLayout.setVisibility(View.VISIBLE);
                            } else {
                                currentPage--;
                                refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_FAIL);
                                Toast.makeText(getBaseContext(), "没有更多商品了", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void searchByName(final int pageId, final String mode, String yqName) {
        product = yqName;
        currentPage = pageId;
        String url = null;
        if (mode.equals(DEVICER))
            url = "http://www.dev.labuy.cn/app.php/Yq?model=device&page=" + pageId + "&product=" + yqName;
        else if (mode.equals(PART)) {
            url = "http://www.dev.labuy.cn/app.php/Parts?model=parts&page=" + pageId + "&product=" + yqName;
        } else
            return;
        LogUtils.d(MyApplication.TAG, url);
        PublicFunc.showProgress(this);
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PublicFunc.closeProgress();
                if (pageId == 1) {
                    refreshLayout.setVisibility(View.GONE);
                    toTop.setVisibility(View.GONE);
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
                final List<Merchandise> result = Utility.handleMerchandiseListResponse(mode,restext);
                if (result != null) {
                    final int index = merchandiseList.size();
                    merchandiseList.addAll(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setVisibility(View.VISIBLE);
                            toTop.setVisibility(View.VISIBLE);
                            failLayout.setVisibility(View.GONE);
                            goodsListView.getLayoutManager().smoothScrollToPosition(goodsListView, null, index);
                            adapter.notifyDataSetChanged();
                            refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_SUCCESS);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pageId == 1) {
                                refreshLayout.setVisibility(View.GONE);
                                toTop.setVisibility(View.GONE);
                                failLayout.setVisibility(View.VISIBLE);
                            } else {
                                currentPage--;
                                refreshLayout.refreshFinish(UpRefreshLayout.REFRESH_FAIL);
                                Toast.makeText(getBaseContext(), "没有更多商品了", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }
}
