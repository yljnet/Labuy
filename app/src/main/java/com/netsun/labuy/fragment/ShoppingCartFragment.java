package com.netsun.labuy.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netsun.labuy.activity.ConfirmOrderActivity;
import com.netsun.labuy.activity.MainActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.adapter.ShoppingCartItemAdapter;
import com.netsun.labuy.db.Goods;
import com.netsun.labuy.gson.Merchandise;
import com.netsun.labuy.gson.ProductOption;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.OnRemoveShoppingCartItemListener;
import com.netsun.labuy.utils.OnShoppingCartItemSelectedistener;
import com.netsun.labuy.utils.OnValueItemClickListener;
import com.netsun.labuy.utils.OptionPop;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.SpaceItemDecoration;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ShoppingCartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    View view;
    Toolbar toolbar;
    TextView titleTV;
    Button rightBtn;
    RelativeLayout emptyView;
    RecyclerView shoppingList;
    CheckBox selectAll;
    Button toBuyBtn;

    int mode;
    private ArrayList<Goods> goodsList = new ArrayList<Goods>();
    private ShoppingCartItemAdapter adapter;
    private int selectCount = 0;
    OptionPop optionPop;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PublicFunc.HANDLER_CODE_EDIT_OPTION:
                    final Goods goods = (Goods) msg.obj;
                    if (goods == null) return;
                    PublicFunc.getMerchandise(goods.getGoods_type(), goods.getGoodsId(), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                Merchandise merchandise = (Merchandise) msg.obj;
                                String[] optionArr = goods.getOptions().split("\\|");
                                if (optionArr.length > 0) {
                                    for (int i = 0; i < optionArr.length; i++) {
                                        String[] optionDetial = optionArr[i].split(":");
                                        for (ProductOption option : merchandise.options) {
                                            if (option.name.equals(optionDetial[0])) {
                                                for (int j = 0; j < option.values.size(); j++) {
                                                    String value = option.values.get(j);
                                                    if (value.equals(optionDetial[1])) {
                                                        option.selected = j;
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (optionPop != null)
                                    optionPop.show(null);
                                optionPop = new OptionPop(getActivity(), merchandise, new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        switch (msg.what) {
                                            case PublicFunc.HANDLER_CODE_REFRESH_SHOPPINGCART:
                                                goodsList.clear();
                                                goodsList.addAll(DataSupport.findAll(Goods.class));
                                                adapter.notifyDataSetChanged();
                                                break;
                                        }
                                    }
                                }, OptionPop.STYLE_KEEP,
                                        new OnValueItemClickListener() {
                                            @Override
                                            public void onClick(int optionIndex, int valueIndex) {

                                            }
                                        });
                                optionPop.setStyle(optionPop.STYLE_KEEP);
                                optionPop.show(shoppingList);
                            }
                        }
                    });
                    break;
            }
        }
    };

    public static ShoppingCartFragment newInstance() {

        Bundle args = new Bundle();

        ShoppingCartFragment fragment = new ShoppingCartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectAll.isChecked())
                    selectAll.performClick();
                ((MainActivity) getActivity()).onNavigationItemSelected(((MainActivity) getActivity()).bottomView.getMenu().getItem(0));
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String viewText = ((Button) view).getText().toString();
                if (TextUtils.equals(viewText, "编辑")) {
                    adapter.setStyle(ShoppingCartItemAdapter.STYLE_EDIT);
                    adapter.notifyDataSetChanged();
                    ((Button) view).setText("完成");

                } else if (TextUtils.equals(viewText, "完成")) {
                    ((Button) view).setText("编辑");
                    adapter.setStyle(ShoppingCartItemAdapter.STYLE_VIEW);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = selectAll.isChecked();
                for (Goods item : goodsList) {
                    item.setSelected(isSelected);
                }
                adapter.notifyDataSetChanged();
                selectCount = changeButtonState();
            }
        });
        toBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectCount > 0) {
                    Bundle bundle = new Bundle();
                    ArrayList<Goods> selects = new ArrayList<Goods>();
                    for (Goods item : goodsList) {
                        if (item.isSelected()) {
                            selects.add(item);
                        }
                    }
                    bundle.putParcelableArrayList("shoppinglist", selects);
                    Intent intent = new Intent(getActivity(), ConfirmOrderActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_shopping_cart, container, false);
        emptyView = (RelativeLayout) view.findViewById(R.id.id_empty_view);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        titleTV = (TextView) view.findViewById(R.id.tool_bar_title_text);
        rightBtn = (Button) view.findViewById(R.id.id_tool_bar_ritht_button);
        shoppingList = (RecyclerView) view.findViewById(R.id.id_shopping_list);
        selectAll = (CheckBox) view.findViewById(R.id.id_select_all);
        toBuyBtn = (Button) view.findViewById(R.id.id_to_buy);
        titleTV.setText("购物车");
        rightBtn.setText("编辑");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        shoppingList.setLayoutManager(layoutManager);
        getShoppingList();
        changeView();
        selectCount = changeButtonState();
        adapter = new ShoppingCartItemAdapter(goodsList);
        adapter.setOnShoppingCartItemSelectedistener(new OnShoppingCartItemSelectedistener() {
            @Override
            public void onSelected(int id, boolean isSelect) {
                changeCheckBoxState();
                selectCount = changeButtonState();
            }
        });
        adapter.setHandler(handler);
        adapter.setOnRemoveShoppingCartItemListener(new OnRemoveShoppingCartItemListener() {
            @Override
            public void onRemoveItem(final int id) {
                Goods item = goodsList.get(id);
                item.delete();
                goodsList.remove(id);
                adapter.notifyDataSetChanged();
                changeView();
                changeButtonState();
            }
        });
        shoppingList.setAdapter(adapter);
        int space = getResources().getDimensionPixelSize(R.dimen.space_two);
        shoppingList.addItemDecoration(new SpaceItemDecoration(space));
        return view;
    }

    private void changeCheckBoxState() {
        boolean isAllSelected = true;
        if (goodsList.size() == 0) isAllSelected = false;
        else
            for (Goods item : goodsList) {
                if (!item.isSelected()) {
                    isAllSelected = false;
                    break;
                }
            }
        if (isAllSelected)
            selectAll.setChecked(true);
        else
            selectAll.setChecked(false);
    }

    private void changeView() {
        if (goodsList.size() == 0) {
            shoppingList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            selectAll.setChecked(false);
            selectAll.setClickable(false);
        } else {
            shoppingList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            selectAll.setClickable(true);
        }
    }

    private void getShoppingList() {
        goodsList.clear();
        goodsList.addAll(DataSupport.findAll(Goods.class));
        LogUtils.d(MyApplication.TAG, "ShoppingCartCount:" + goodsList.size());
    }

    private int changeButtonState() {
        int count = 0;
        for (Goods item : goodsList) {
            if (item.isSelected()) {
                count += item.getNum();
            }
        }
        toBuyBtn.setText("结算(" + count + ")");
        if (count > 0)
            toBuyBtn.setBackgroundResource(R.color.colorPrimary);
        else
            toBuyBtn.setBackgroundResource(R.color.secondary_text_dark);
        return count;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            if (selectAll.isChecked())
                selectAll.performClick();
            if (("完成").equals(rightBtn.getText().toString())) {
                rightBtn.performClick();
            }
        } else {
            getShoppingList();
            changeView();
            adapter.notifyDataSetChanged();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
