package com.netsun.labuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.adapter.CategoryAdapter;
import com.netsun.labuy.bean.Category;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Button fenxiDeviceButton;
    Button propertyDeviceButton;
    Button labDeviceButton;
    Button partButton;
    Button consumableButton;
    RecyclerView recyclerView;
    TextView searchEdit;
    Button current;
    List<Category> dataList, fenxiList, propertyList, labList, partList, consumableList;
    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchEdit = (TextView) findViewById(R.id.search_edit);
        searchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchYQByKeyActivity.class);
                startActivity(intent);
            }
        });

        fenxiDeviceButton = (Button) findViewById(R.id.fenxi_device_button);
        propertyDeviceButton = (Button) findViewById(R.id.property_device_button);
        labDeviceButton = (Button) findViewById(R.id.lab_device_button);
        partButton = (Button) findViewById(R.id.part_button);
        consumableButton = (Button) findViewById(R.id.consumable_button);

        fenxiDeviceButton.setOnClickListener(this);
        propertyDeviceButton.setOnClickListener(this);
        labDeviceButton.setOnClickListener(this);
        partButton.setOnClickListener(this);
        consumableButton.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        dataList = new ArrayList<Category>();
        adapter = new CategoryAdapter(dataList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpaceItemDecoration(2));
        fenxiList = new ArrayList<Category>();
        fenxiList.add(new Category("气相色谱", "", "111211", PublicFunc.DEVICER));
        fenxiList.add(new Category("液相色谱", "", "111212", PublicFunc.DEVICER));
        fenxiList.add(new Category("水分测定", "", "1120", PublicFunc.DEVICER));
        fenxiList.add(new Category("紫外可见", "", "111111", PublicFunc.DEVICER));
        fenxiList.add(new Category("原子吸收", "", "111112", PublicFunc.DEVICER));
        fenxiList.add(new Category("定氮仪", "", "112112", PublicFunc.DEVICER));
        propertyList = new ArrayList<Category>();
        propertyList.add(new Category("PH计", "", "111815", PublicFunc.DEVICER));
        propertyList.add(new Category("酸度计", "", "111819", PublicFunc.DEVICER));
        propertyList.add(new Category("粘度计", "", "131116", PublicFunc.DEVICER));
        propertyList.add(new Category("密度计", "", "181712", PublicFunc.DEVICER));
        propertyList.add(new Category("粒度仪", "", "1520", PublicFunc.DEVICER));
        propertyList.add(new Category("熔点仪", "", "111918", PublicFunc.DEVICER));
        propertyList.add(new Category("滴定仪", "", "111811", PublicFunc.DEVICER));
        labList = new ArrayList<Category>();
        labList.add(new Category("天平", "", "171111", PublicFunc.DEVICER));
        labList.add(new Category("马弗炉", "", "121612", PublicFunc.DEVICER));
        labList.add(new Category("烘箱", "", "121611", PublicFunc.DEVICER));
        labList.add(new Category("气体发生器", "", "1222", PublicFunc.DEVICER));
        labList.add(new Category("水浴", "", "121616", PublicFunc.DEVICER));
        labList.add(new Category("振荡", "", "1215", PublicFunc.DEVICER));
        labList.add(new Category("消解", "", "1212", PublicFunc.DEVICER));
        labList.add(new Category("离心", "", "121314", PublicFunc.DEVICER));
        labList.add(new Category("通风柜", "", "122316", PublicFunc.DEVICER));
        partList = new ArrayList<Category>();
        partList.add(new Category("气相色谱柱", "", "1111", PublicFunc.PART));
        partList.add(new Category("液相色谱柱", "", "1112", PublicFunc.PART));
        partList.add(new Category("进样针", "", "121316", PublicFunc.PART));
        partList.add(new Category("氘灯", "", "121233", PublicFunc.PART));
        partList.add(new Category("元素灯", "", "1425", PublicFunc.PART));
        partList.add(new Category("样品池", "", "141211", PublicFunc.PART));
        partList.add(new Category("工作站", "", "121324", PublicFunc.PART));
        fenxiDeviceButton.performClick();
    }

    @Override
    public void onClick(View view) {
        if (current != null)
            current.setBackgroundColor(getResources().getColor(R.color.secondary_text_dark));
        view.setBackgroundColor(getResources().getColor(android.R.color.white));
        dataList.clear();
        switch (view.getId()) {
            case R.id.fenxi_device_button:
                dataList.addAll(fenxiList);
                break;
            case R.id.property_device_button:
                dataList.addAll(propertyList);
                break;
            case R.id.lab_device_button:
                dataList.addAll(labList);
                break;
            case R.id.part_button:
                dataList.addAll(partList);
                break;
            case R.id.consumable_button:
                break;
            default:
                break;
        }
        current = (Button) view;
        adapter.notifyDataSetChanged();
    }
}
