package com.netsun.labuy.adapter;

/**
 * Created by Administrator on 2017/3/9.
 */
public class DetialItem {
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_LIST = 3;
    public static final int TYPE_HTML = 4;

    public DetialItem(int type, String cont) {
        this.type = type;
        switch (type) {
            case TYPE_TITLE:
                title = cont;
                break;
            case TYPE_TEXT:
                content = cont;
                break;
            case TYPE_HTML:
                content = cont;
                break;
            default:
                break;
        }
    }

    public DetialItem(Object object) {
        type = TYPE_LIST;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    private int type;
    private String title;
    private String content;
    private Object object;
}
