package com.netsun.labuy.utils;

import android.os.AsyncTask;

import com.netsun.labuy.db.City;
import com.netsun.labuy.db.Province;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/7.
 */

public class DownLoadAreaTask extends AsyncTask<Void, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {
        String url = PublicFunc.host + "app.php/Address?type=regional";
        OkHttpClient client = new OkHttpClient();
        Request provinceRequest = new Request.Builder().url(url).build();
        try {
            Response provincerResponse = client.newCall(provinceRequest).execute();
            if (provincerResponse != null) {
                Utility.handleProvinceResponse(provincerResponse.body().string());
            }
            List<Province> provinces = DataSupport.findAll(Province.class);
            for (Province province : provinces) {
                String cityUrl = PublicFunc.host + "app.php/Address?type=regional&cateId="
                        + province.getProvinceCode() + "&level=1";
                Request cityRequest = new Request.Builder().url(cityUrl).build();
                Response cityreResponse = client.newCall(cityRequest).execute();
                Utility.handleCityResponse(cityreResponse.body().string(), province);
            }
            List<City> cities = DataSupport.findAll(City.class);
            for (City city : cities) {
                String countyUrl = PublicFunc.host + "app.php/Address?type=regional&cateId="
                        + city.getCityCode() + "&level=2";
                Request countyRequest = new Request.Builder().url(countyUrl).build();
                Response countyResponse = client.newCall(countyRequest).execute();
                Utility.handleCountyResponse(countyResponse.body().string(),city);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
