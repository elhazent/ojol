package com.elhazent.picodiploma.driveronline;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elhazent.picodiploma.driveronline.helper.MyContants;
import com.elhazent.picodiploma.driveronline.model.DataItem;

import java.util.List;


class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.Holder> {

    private List<com.elhazent.picodiploma.driveronline.model.DataItem> DataItem;
    private FragmentActivity context;
    private int i;


    public CustomRecyclerAdapter(List<DataItem> DataItem, FragmentActivity activity, int i) {
        this.DataItem = DataItem;
        this.context = activity;
        this.i = i;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(context).inflate(R.layout.custom_recyclerview, viewGroup,false);
       return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.texttgl.setText(DataItem.get(position).getBookingTanggal());
        holder.txtawal.setText(DataItem.get(position).getBookingFrom());
        holder.txtakhir.setText(DataItem.get(position).getBookingTujuan());
        holder.txtharga.setText(DataItem.get(position).getBookingBiayaUser());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 1){
                    Intent intent = new Intent(context, DetailOrderActivity.class);
                    intent.putExtra(MyContants.INDEX, position);
                    intent.putExtra(MyContants.STATUS, i);
                    context.startActivity(intent);
                } else if (i == 2){
                    Intent intent = new Intent(context, DetailOrderActivity.class);
                    intent.putExtra(MyContants.INDEX, position);
                    intent.putExtra(MyContants.STATUS, i);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, DetailOrderActivity.class);
                    intent.putExtra(MyContants.INDEX, position);
                    intent.putExtra(MyContants.STATUS, i);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return DataItem.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView texttgl;
        TextView txtawal;
        TextView txtakhir;
        TextView txtharga;
        public Holder(@NonNull View itemView) {
            super(itemView);
            texttgl = itemView.findViewById(R.id.texttgl);
            txtawal = itemView.findViewById(R.id.txtawal);
            txtakhir = itemView.findViewById(R.id.txtakhir);
            txtharga = itemView.findViewById(R.id.txtharga);
        }
    }
}
