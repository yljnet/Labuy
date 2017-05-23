package com.netsun.labuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.activity.SearchResultActivity;
import com.netsun.labuy.bean.Category;
import com.netsun.labuy.utils.CategoryView;
import com.netsun.labuy.utils.PublicFunc;

import java.util.List;

/**
 * Created by Administrator on 2017/4/13.
 */

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ShowViewHolder> {
    private Context mContext;
    List<String> captionList;
    private List<List<Category>> categoryList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                String[] goods = (String[])msg.obj;
                Intent intent = new Intent(mContext, SearchResultActivity.class);
                intent.putExtra("mode",goods[0]);
                intent.putExtra("type", PublicFunc.SEARCH_BY_ID);
                intent.putExtra("cate_id", goods[1]);
                mContext.startActivity(intent);
            }
        }
    };
    public ShowAdapter(List<String> captionList, List<List<Category>> list) {
        this.captionList = captionList;
        categoryList = list;
    }

    @Override
    public ShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.method_category, parent, false);
        ShowViewHolder holder = new ShowViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ShowViewHolder holder, int position) {
        String caption = captionList.get(position);
        List<Category> categories = categoryList.get(position);

        if (holder != null && caption != null && categories != null) {
            holder.captionTV.setText(caption);

            holder.first.setCaption(categories.get(0).getName());
            holder.first.setImage(categories.get(0).getLogo());
            holder.first.setGoodsId(categories.get(0).getGoods_id());
            holder.first.setMode(categories.get(0).getMode());
            holder.first.setHandler(handler);

            holder.second.setCaption(categories.get(1).getName());
            holder.second.setImage(categories.get(1).getLogo());
            holder.second.setGoodsId(categories.get(1).getGoods_id());
            holder.second.setMode(categories.get(1).getMode());
            holder.second.setHandler(handler);

            holder.third.setCaption(categories.get(2).getName());
            holder.third.setImage(categories.get(2).getLogo());
            holder.third.setGoodsId(categories.get(2).getGoods_id());
            holder.third.setMode(categories.get(2).getMode());
            holder.third.setHandler(handler);

            holder.fourth.setCaption(categories.get(3).getName());
            holder.fourth.setImage(categories.get(3).getLogo());
            holder.fourth.setGoodsId(categories.get(3).getGoods_id());
            holder.fourth.setMode(categories.get(3).getMode());
            holder.fourth.setHandler(handler);

            holder.fifth.setCaption(categories.get(4).getName());
            holder.fifth.setImage(categories.get(4).getLogo());
            holder.fifth.setGoodsId(categories.get(4).getGoods_id());
            holder.fifth.setMode(categories.get(4).getMode());
            holder.fifth.setHandler(handler);

        }
    }

    @Override
    public int getItemCount() {
        if (captionList != null)
            return captionList.size();
        else
            return 0;
    }


    class ShowViewHolder extends RecyclerView.ViewHolder {
        private TextView captionTV;
        CategoryView first,second,third,fourth,fifth;

        public ShowViewHolder(View itemView) {
            super(itemView);
            captionTV = (TextView) itemView.findViewById(R.id.id_caption_tv);
            first = (CategoryView)itemView.findViewById(R.id.show_first);
            second = (CategoryView)itemView.findViewById(R.id.show_second);
            third = (CategoryView)itemView.findViewById(R.id.show_third);
            fourth = (CategoryView)itemView.findViewById(R.id.show_fourth);
            fifth = (CategoryView)itemView.findViewById(R.id.show_fifth);
        }
    }
}
