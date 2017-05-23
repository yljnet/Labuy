package com.netsun.labuy.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.netsun.labuy.R;

/**
 * Created by Administrator on 2017/4/14.
 */

public class CategoryView extends CardView implements View.OnClickListener {
    private Context context;
    private ImageView image;
    private TextView caption;
    private String goodsId;
    private String mode;
    private Handler handler;

    public CategoryView(Context context) {
        super(context);
        this.context = context;
    }

    public CategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_category, null);
        image = (ImageView) view.findViewById(R.id.goods_image);
        caption = (TextView) view.findViewById(R.id.goods_name);
        image.setOnClickListener(this);
        caption.setOnClickListener(this);
        addView(view);
    }

    public void setImage(String url) {
        if (url != null && !url.isEmpty()) {
            image.setVisibility(VISIBLE);
            Glide.with(context).load(url).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    if (resource != null && image != null&&image.getHeight() != 0 && image.getWidth() != 0) {
                        float scaleX = resource.getBounds().width() / image.getWidth();
                        float scaleY = resource.getBounds().height() / image.getHeight();
                        int newWidth = 0;
                        int newHeight = 0;
                        if (scaleX > scaleY) {
                            newWidth = (int) (image.getWidth() * scaleX);
                            newHeight = (int) (image.getHeight() * scaleX);
                        } else {
                            newWidth = (int) (image.getWidth() * scaleY);
                            newHeight = (int) (image.getHeight() * scaleY);
                        }
                        resource.setBounds(0, 0, newWidth, newHeight);
                    }
                    image.setImageDrawable(resource);
                }
            });
        }
    }

    public void setCaption(String name) {
        caption.setText(name);
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onClick(View view) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = 1;
            String[] goods = {mode,goodsId};
            msg.obj =goods;
            handler.sendMessage(msg);
        }
    }
}
