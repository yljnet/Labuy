package com.netsun.labuy.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/6.
 */

public class SearchHistory extends DataSupport {
    private int id;
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
