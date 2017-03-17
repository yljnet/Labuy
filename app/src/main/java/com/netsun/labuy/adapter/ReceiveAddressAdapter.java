package com.netsun.labuy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.utils.OnAddressSelectLinstener;

import java.util.List;

/**
 * Created by Administrator on 2017/3/17.
 */

public class ReceiveAddressAdapter extends RecyclerView.Adapter<ReceiveAddressAdapter.AddrViewHolder> {
    private List<ReceiveAddress> addressList;
    private OnAddressSelectLinstener onAddressSelectLinstener;

    public ReceiveAddressAdapter(List<ReceiveAddress> addresses) {
        this.addressList = addresses;
    }

    public void setOnAddressSelectLinstener(OnAddressSelectLinstener linstener) {
        this.onAddressSelectLinstener = linstener;
    }

    @Override
    public AddrViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address, parent, false);
        final AddrViewHolder holder = new AddrViewHolder(view);
        holder.addrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAddressSelectLinstener != null)
                    onAddressSelectLinstener.SelectAddr(addressList.get(holder.getAdapterPosition()));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(AddrViewHolder holder, int position) {
        if (holder == null) return;
        ReceiveAddress addr = addressList.get(position);
        if (addr == null) return;
        holder.nameTV.setText(addr.getConsignee());
        if (addr.getMobile() != null && !addr.getMobile().isEmpty())
            holder.phoneTV.setText(addr.getMobile());
        else holder.phoneTV.setText(addr.getTel());
        holder.addrTV.setText(addr.getAddress());
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

        public AddrViewHolder(View itemView) {
            super(itemView);
            addrLayout = (RelativeLayout) itemView.findViewById(R.id.addr_layout);
            nameTV = (TextView) itemView.findViewById(R.id.id_consignee_text_view);
            phoneTV = (TextView) itemView.findViewById(R.id.id_mobile_text_view);
            addrTV = (TextView) itemView.findViewById(R.id.id_address_text_view);
        }
    }
}
