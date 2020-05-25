package com.example.heroku.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heroku.Common.Common;
import com.example.heroku.Interface.IRecyclerItemSelectedListener;
import com.example.heroku.models.ModelOffice;
import com.example.heroku.R;


import java.util.ArrayList;
import java.util.List;

public class MyOfficeAdapter extends RecyclerView.Adapter<MyOfficeAdapter.MyViewHolder> {

    Context context;
    List<ModelOffice> officeList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;


    public MyOfficeAdapter(Context context, List<ModelOffice> officeList) {
        this.context = context;
        this.officeList = officeList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_office, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.txt_office_name.setText(officeList.get(position).getName());
        holder.txt_office_address.setText(officeList.get(position).getAddress());
        if (!cardViewList.contains(holder.card_office))cardViewList.add(holder.card_office);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for (CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                //set background for selected item
                holder.card_office.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));

                //Send Broadcast to tell BookingActivity enable the next button
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_OFFICE_PLACE, officeList.get(pos));// check bug
                intent.putExtra(Common.KEY_STEP, 1);
                localBroadcastManager.sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return officeList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_office_name, txt_office_address;
        CardView card_office;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_office = itemView.findViewById(R.id.card_office);
            //txt_office_name = (TextView)itemView.findViewById(R.id.txt_office_name);
            //txt_office_address = (TextView)itemView.findViewById(R.id.txt_office_address);
            txt_office_name = itemView.findViewById(R.id.txt_office_name);
            txt_office_address = itemView.findViewById(R.id.txt_office_address);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }


    }
}
