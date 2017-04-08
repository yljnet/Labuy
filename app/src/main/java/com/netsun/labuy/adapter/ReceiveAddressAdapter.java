package com.netsun.labuy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.utils.HandleDataBase;
import com.netsun.labuy.utils.OnAddressRemove;
import com.netsun.labuy.utils.OnAddressSelectLinstener;
import com.netsun.labuy.utils.PublicFunc;

import java.util.List;

import static com.netsun.labuy.R.layout.address;

/**
 * Created by Administrator on 2017/3/17.
 */

public class ReceiveAddressAdapter extends RecyclerView.Adapter<ReceiveAddressAdapter.AddrViewHolder> {
    public static final int VIEW_MODE = 1;
    public static final int EDIT_MODE = 2;
    public static final int SELECT_MODE = 3;
    private int mode = VIEW_MODE;
    Context mContext;
    private List<ReceiveAddress> addressList;
    private OnAddressSelectLinstener onAddressSelectLinstener;
    private OnAddressRemove onAddressRemove;
    private Handler handler;

    public ReceiveAddressAdapter(List<ReceiveAddress> addresses) {
        this.addressList = addresses;
    }

    public void setOnAddressSelectLinstener(OnAddressSelectLinstener linstener) {
        this.onAddressSelectLinstener = linstener;
    }

    public void setOnAddressRemove(OnAddressRemove onAddressRemove) {
        this.onAddressRemove = onAddressRemove;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public AddrViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(address, parent, false);
        final AddrViewHolder holder = new AddrViewHolder(view);
        if (SELECT_MODE == mode) {
            holder.addrLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onAddressSelectLinstener != null)
                        onAddressSelectLinstener.SelectAddr(addressList.get(holder.getAdapterPosition()));
                }
            });
        }
        if (mode == EDIT_MODE) {
            holder.defaultCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ReceiveAddress address = addressList.get(holder.getAdapterPosition());
                    if (address.getDefaulted() == 0) {
                        if (address != null) {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = address;
                            if (handler != null)
                                handler.sendMessage(msg);
                        }
                    }
                }
            });

            holder.editTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.netsun.intent.ACTION_EDIT_ADDRESS");
                    ReceiveAddress address = addressList.get(holder.getAdapterPosition());
                    intent.putExtra("address", address);
                    ((Activity) mContext).startActivityForResult(intent, 1);
                }
            });

            holder.deleteTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(holder.addrLayout, "确定要删除这个地址吗？", Snackbar.LENGTH_LONG)
                            .setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ReceiveAddress address = addressList.get(holder.getAdapterPosition());
                                    if (onAddressRemove != null) {
                                        onAddressRemove.onRemove(address);
                                    }
                                }
                            }).show();
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final AddrViewHolder holder, int position) {
        if (holder == null) return;
        final ReceiveAddress addr = addressList.get(position);
        if (addr == null) return;
        holder.nameTV.setText("收件人：" + addr.getConsignee());
        if (addr.getMobile() != null && !addr.getMobile().isEmpty())
            holder.phoneTV.setText(addr.getMobile());
        else holder.phoneTV.setText(addr.getTel());
        String scount = null;
        while ((scount = HandleDataBase.getAreaById(addr.getRegional())) == null) {
            PublicFunc.getAreaInfoFromServer(addr.getRegional());
        }
        holder.addrTV.setText(scount + addr.getAddress());
        if (mode == VIEW_MODE || mode == SELECT_MODE) {
            holder.manageLayout.setVisibility(View.GONE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            if (addr.getDefaulted() == 1) {
                holder.defaultCB.setChecked(true);
                holder.defaultCB.setClickable(false);
            } else {
                holder.defaultCB.setChecked(false);
                holder.defaultCB.setClickable(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (addressList != null)
            return addressList.size();
        else
            return 0;
    }

    class AddrViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout addrLayout;
        TextView nameTV;
        TextView phoneTV;
        TextView addrTV;
        LinearLayout manageLayout;
        CheckBox defaultCB;
        TextView editTV;
        TextView deleteTV;

        public AddrViewHolder(View itemView) {
            super(itemView);
            addrLayout = (RelativeLayout) itemView.findViewById(R.id.addr_layout);
            nameTV = (TextView) itemView.findViewById(R.id.id_consignee_text_view);
            phoneTV = (TextView) itemView.findViewById(R.id.id_mobile_text_view);
            addrTV = (TextView) itemView.findViewById(R.id.id_address_text_view);
            manageLayout = (LinearLayout) itemView.findViewById(R.id.id_address_manage_ll);
            defaultCB = (CheckBox) itemView.findViewById(R.id.id_address_manage_default_cb);
            editTV = (TextView) itemView.findViewById(R.id.id_address_manage_edit_tv);
            deleteTV = (TextView) itemView.findViewById(R.id.id_address_manage_delete_tv);
        }
    }
}
