package com.netsun.labuy.utils;

import com.netsun.labuy.db.City;
import com.netsun.labuy.db.County;
import com.netsun.labuy.db.Province;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.db.Goods;

import org.litepal.crud.DataSupport;

import java.util.List;

import static org.litepal.crud.DataSupport.where;

/**
 * Created by Administrator on 2017/3/22.
 * 数据库操作
 */

public class HandleDataBase {

    public static ReceiveAddress getAddrInfoById(int addrId) {
        List<ReceiveAddress> addressList = DataSupport
                .where("addrId=?", String.valueOf(addrId))
                .find(ReceiveAddress.class);
        if (addressList.size() > 0)
            return addressList.get(0);
        else return null;
    }

    /**
     * 获取默认收件地址
     *
     * @return
     */
    public static ReceiveAddress getDefaultReceiveAddress() {
        ReceiveAddress result = null;
        List<ReceiveAddress> allAddrs = DataSupport.findAll(ReceiveAddress.class);
        for (ReceiveAddress address : allAddrs) {
            if (1 == address.getDefaulted())
                return address;
        }
        if (allAddrs.size() > 0) {
            result = allAddrs.get(0);
            result.setDefaulted(1);
        }
        return result;
    }

    /**
     * 获取地区信息
     *
     * @param area_id
     * @return
     */
    public static String getAreaById(String area_id) {
        String result = null;
        County county;
        City city;
        Province province;
        if ((county = where("countyCode=?", area_id).findFirst(County.class)) != null) {
            result = county.getCountyName();
        }else if ((city = where("cityCode=?", area_id).findFirst(City.class)) != null) {
            result = city.getCityName();
        }else if ((province = where("provinceCode=?", area_id).findFirst(Province.class))!= null)
            result = province.getProvinceName();
        return result;
    }

    public static String getAreaCodeByName(String name) {
        String result = null;
        final List<County> countries;
        List<City> cities;
        List<Province> provinces;
        if ((countries = where("countyName=?", name).find(County.class)).size() > 0) {
            result = countries.get(0).getCountyCode();
        } else if ((cities = where("cityName=?", name).find(City.class)).size() > 0) {
            result = cities.get(0).getCityCode();
        } else if ((provinces = where("provinceName=?", name).find(Province.class)).size() > 0) {
            result = provinces.get(0).getProvinceCode();
        }
        return result;
    }

    public static void deleteShoppingCartItem(String goodsid){
        List<Goods> items = DataSupport.where("goodsId=?", goodsid).find(Goods.class);
        for (Goods item:items){
            item.delete();
        }
    }
}
