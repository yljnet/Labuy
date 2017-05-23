package com.netsun.labuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.netsun.labuy.activity.SearchResultActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.bean.Category;
import com.netsun.labuy.utils.CategoryView;
import com.netsun.labuy.utils.PublicFunc;

import java.util.List;

/**
 * Created by Administrator on 2017/4/13.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context mContext;
    private List<Category> categoryList;

    public CategoryAdapter(List<Category> categories) {
        categoryList = categories;
    }


    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);
        CategoryViewHolder holder = new CategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        if (categoryList != null && categoryList.size() > position) {
            Category category = categoryList.get(position);
            if (holder != null && category != null) {
                holder.categoryView.setMode(category.getMode());
                holder.categoryView.setCaption(category.getName());
                holder.categoryView.setGoodsId(category.getGoods_id());

                if (category.getLogo() != null && !category.getLogo().isEmpty()) {
                    holder.categoryView.setImage(category.getLogo());
                }
                holder.categoryView.setHandler(new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1){
                            int mode = msg.arg1;
                            String cateId = (String)msg.obj;
                            Intent intent = new Intent(mContext, SearchResultActivity.class);
                            intent.putExtra("mode",mode);
                            intent.putExtra("type", PublicFunc.SEARCH_BY_ID);
                            intent.putExtra("cate_id", cateId);
                            mContext.startActivity(intent);
                            Toast.makeText(mContext,cateId,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (categoryList != null)
            return categoryList.size();
        else
            return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        CategoryView categoryView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryView = (CategoryView) itemView.findViewById(R.id.category_view);
        }
    }
}
