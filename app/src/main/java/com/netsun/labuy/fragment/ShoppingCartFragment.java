package com.netsun.labuy.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.netsun.labuy.ConfirmOrderActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.adapter.ShoppingCartItemAdapter;
import com.netsun.labuy.db.ShoppingItem;
import com.netsun.labuy.utils.OnRemoveShoppingCartItemListener;
import com.netsun.labuy.utils.OnShoppingCartItemSelectedistener;
import com.netsun.labuy.utils.SpaceItemDecoration;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ShoppingCartFragment extends Fragment {
    View view;
    TextView editAll;
    RecyclerView shoppingList;
    CheckBox selectAll;
    Button toBuyBtn;

    private ArrayList<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();
    private ShoppingCartItemAdapter adapter;
    private int selectCount = 0;

    public static ShoppingCartFragment newInstance() {

        Bundle args = new Bundle();

        ShoppingCartFragment fragment = new ShoppingCartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String viewText = ((TextView) view).getText().toString();
                if (TextUtils.equals(viewText, "编辑")) {
                    adapter.setStyle(ShoppingCartItemAdapter.STYLE_EDIT);
                    adapter.notifyDataSetChanged();
                    ((TextView) view).setText("完成");

                } else if (TextUtils.equals(viewText, "完成")) {
                    ((TextView) view).setText("编辑");
                    adapter.setStyle(ShoppingCartItemAdapter.STYLE_VIEW);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (ShoppingItem item : shoppingItems) {
                    item.setSelected(b);
                }
                adapter.notifyDataSetChanged();
                selectCount = changeButtonState();
            }
        });
        toBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectCount > 0){
                    Bundle bundle = new Bundle();
                    ArrayList<ShoppingItem> selects = new ArrayList<ShoppingItem>();
                    for (ShoppingItem item:shoppingItems){
                        if (item.isSelected()) {
                            selects.add(item);
                        }
                    }
                    bundle.putParcelableArrayList("shoppinglist",selects);
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
        editAll = (TextView) view.findViewById(R.id.id_edit_all);
        shoppingList = (RecyclerView) view.findViewById(R.id.id_shopping_list);
        selectAll = (CheckBox) view.findViewById(R.id.id_select_all);
        toBuyBtn = (Button) view.findViewById(R.id.id_to_buy);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        shoppingList.setLayoutManager(layoutManager);
        getShoppingList();
        adapter = new ShoppingCartItemAdapter(shoppingItems);
        adapter.setOnShoppingCartItemSelectedistener(new OnShoppingCartItemSelectedistener() {
            @Override
            public void onSelected(int id, boolean isSelect) {
                ShoppingItem item = shoppingItems.get(id);
                item.setSelected(isSelect);
                selectCount = changeButtonState();
            }
        });
        adapter.setOnRemoveShoppingCartItemListener(new OnRemoveShoppingCartItemListener() {
            @Override
            public void onRemoveItem(final int id) {
                ShoppingItem item = shoppingItems.get(id);
                item.delete();
                shoppingItems.remove(id);
                adapter.notifyDataSetChanged();
            }
        });
        shoppingList.setAdapter(adapter);
        int space = getResources().getDimensionPixelSize(R.dimen.space_two);
        shoppingList.addItemDecoration(new SpaceItemDecoration(space));
        return view;
    }

    private void getShoppingList() {
        List<ShoppingItem> items = DataSupport.findAll(ShoppingItem.class);
        if (items.size() > 0) {
            shoppingItems.clear();
            for (ShoppingItem item : items)
                shoppingItems.add(item);
        }
    }

    private int changeButtonState() {
        int count = 0;
        for (ShoppingItem item : shoppingItems) {
            if (item.isSelected()) {
                count++;
            }
        }
        toBuyBtn.setText("结算(" + count + ")");
        if (count > 0)
            toBuyBtn.setBackgroundResource(R.color.colorPrimary);
        else
            toBuyBtn.setBackgroundResource(R.color.secondary_text_dark);
        return count;
    }
}
