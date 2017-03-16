package com.netsun.labuy.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/1.
 */

public class Third extends DataSupport implements Parcelable{
    private long id;//数据表主键
    private String cate_id;
    private String parentId;
    private String name;

    public String getId() {
        return cate_id;
    }

    public void setId(String cate_id) {
        this.cate_id = cate_id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cate_id);
        parcel.writeString(parentId);
        parcel.writeString(name);
    }
    public static final Parcelable.Creator<Third> CREATOR = new Parcelable.Creator<Third>() {

        @Override
        public Third createFromParcel(Parcel parcel) {
            Third third = new Third();
            third.cate_id = parcel.readString();
            third.parentId = parcel.readString();
            third.name = parcel.readString();
            return third;
        }

        @Override
        public Third[] newArray(int i) {
            return new Third[i];
        }
    };
}
