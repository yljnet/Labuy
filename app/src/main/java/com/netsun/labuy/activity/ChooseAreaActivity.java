package com.netsun.labuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netsun.labuy.R;
import com.netsun.labuy.db.City;
import com.netsun.labuy.db.County;
import com.netsun.labuy.db.Province;
import com.netsun.labuy.utils.HttpUtils;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaActivity extends AppCompatActivity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    ListView listView;
    ImageView backImage;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;
    private Toolbar toolBar;
    private TextView titleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        toolBar = (Toolbar)findViewById(R.id.toolbar);
        titleTV = (TextView)findViewById(R.id.tool_bar_title_text);
        titleTV.setText("选择地区");
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                } else {
                    finish();
                }
            }
        });
        listView = (ListView) findViewById(R.id.id_area_list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    County county = countyList.get(i);
                    Intent intent = new Intent(getBaseContext(), EditAddressActivity.class);
                    intent.putExtra("area_name", county.getCountyName());
                    intent.putExtra("area_code", county.getCountyCode());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }


    /**
     * 查询全国所有的省，优先从数据库查，如果没有查询到再到服务器上查询
     */
    private void queryProvinces() {
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetInvalidated();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String url = "http://www.dev.labuy.cn/app.php/Address?type=regional";
            queryFromServer(url, "province");
        }
    }

    /**
     * 查询选中省的所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        cityList = DataSupport.where("provinceCode=?", String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            String provinceCode = selectedProvince.getProvinceCode();
            String address = PublicFunc.host + "app.php/Address?type=regional&cateId=" + provinceCode + "&level=1";
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有再去服务器上查询
     */
    private void queryCounties() {
        countyList = DataSupport.where("cityCode = ?", String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            String cityCode = selectedCity.getCityCode();
            String address = PublicFunc.host + "app.php/Address?type=regional&cateId=" + cityCode + "&level=2";
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     *
     * @param address 请求的地址
     * @param type    请求的类型
     */
    private void queryFromServer(String address, final String type) {
        PublicFunc.showProgress(ChooseAreaActivity.this);
        HttpUtils.get(address, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //通过runOnUiThread()方法返回主线程处理
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PublicFunc.closeProgress();
                                Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        LogUtils.d(MyApplication.TAG, responseText);
                        boolean result = false;
                        if ("province".equals(type)) {
                            result = Utility.handleProvinceResponse(responseText);
                        } else if ("city".equals(type)) {
                            result = Utility.handleCityResponse(responseText, selectedProvince);
                        } else if ("county".equals(type)) {
                            result = Utility.handleCountyResponse(responseText, selectedCity);
                        }
                        final boolean isSuccess = result;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PublicFunc.closeProgress();
                                if (isSuccess) {
                                    if ("province".equals(type)) {
                                        queryProvinces();
                                    } else if ("city".equals(type)) {
                                        queryCities();
                                    } else if ("county".equals(type)) {
                                        queryCounties();
                                    }
                                }
                            }
                        });
                    }
                }

        );
    }

}
