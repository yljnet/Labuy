package com.netsun.labuy.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/1/23.
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private String cityCode;
    private String provinceCode;

    public int getId() {
        return id;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
