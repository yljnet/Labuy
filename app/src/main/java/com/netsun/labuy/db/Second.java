package com.netsun.labuy.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/1.
 */

public class Second extends DataSupport {
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
}
