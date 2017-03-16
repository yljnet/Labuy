package com.netsun.labuy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.gson.ProductMethod;
import com.netsun.labuy.utils.LogUtils;

import java.util.List;

public class ProductDetialAdapter extends BaseAdapter {
    private List<DetialItem> detialItemList;
    Context mContext;

    public ProductDetialAdapter(Context context, List<DetialItem> list) {
        this.detialItemList = list;
        for (DetialItem item : detialItemList) {
            LogUtils.d("tag", String.valueOf(item.getType()));
        }
        mContext = context;
    }

    @Override
    public int getCount() {
        return detialItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return detialItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        DetialItem item = detialItemList.get(i);
        LogUtils.d("tag", String.valueOf(detialItemList.size()) + " " + String.valueOf(i)
                + " " + String.valueOf(item.getType()));
        switch (item.getType()) {
            case DetialItem.TYPE_TITLE:
                view = LayoutInflater.from(mContext).inflate(R.layout.detial_item_title, null);
                TextView titleView = (TextView) view.findViewById(R.id.text);
                titleView.setText(item.getTitle());
                titleView.setBackgroundResource(R.color.colorPrimary);
                break;
            case DetialItem.TYPE_TEXT:
                view = LayoutInflater.from(mContext).inflate(R.layout.detial_item_title, null);
                TextView contentView = (TextView) view.findViewById(R.id.text);
                contentView.setText(item.getContent());
                contentView.setBackgroundResource(R.color.colorWhiteSmoke);
                break;
            case DetialItem.TYPE_HTML:
                if (view == null) {
                    view = LayoutInflater.from(mContext).inflate(R.layout.detial_item_title, null);
                }
                TextView htmlView = (TextView) view.findViewById(R.id.text);
                htmlView.setText(Html.fromHtml(item.getContent()));
                htmlView.setBackgroundResource(R.color.colorWhiteSmoke);
                break;
            case DetialItem.TYPE_LIST:
                view = LayoutInflater.from(mContext).inflate(R.layout.method_item, null);
                holder = new ViewHolder(view);
                ProductMethod method = (ProductMethod) item.getObject();
                if (holder != null) {
                    holder.tvName.setText(method.name);
                    holder.tvCatehy.setText(method.cate_hy);
                    holder.tvSample.setText(method.sample);
                    holder.tvTarget.setText(method.target);
                    holder.tvCategory.setText(method.category);
                    holder.tvPoster.setText(method.poster);
                    holder.tvStandard.setText(method.standard);
                }
                break;
            default:
                break;
        }
        return view;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName; //名称
        private TextView tvCatehy;//适用行业领域
        private TextView tvSample; //检测样品
        private TextView tvTarget;//目标检测物
        private TextView tvCategory;//主要仪器
        private TextView tvPoster;//来源
        private TextView tvStandard;//参考标准

        public ViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.id_method_name_item);
            tvCatehy = (TextView) view.findViewById(R.id.id_method_cate_hy_item);
            tvSample = (TextView) view.findViewById(R.id.id_method_sample_item);
            tvTarget = (TextView) view.findViewById(R.id.id_method_target_item);
            tvCategory = (TextView) view.findViewById(R.id.id_method_category_item);
            tvPoster = (TextView) view.findViewById(R.id.id_method_poster_item);
            tvStandard = (TextView) view.findViewById(R.id.id_method_standard_item);

        }
    }
}
