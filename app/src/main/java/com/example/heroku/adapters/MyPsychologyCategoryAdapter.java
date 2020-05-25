package com.example.heroku.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heroku.Common.Common;
import com.example.heroku.Interface.IRecyclerItemSelectedListener;
import com.example.heroku.R;
import com.example.heroku.models.PsychologyCategory;

import java.util.ArrayList;
import java.util.List;

public class MyPsychologyCategoryAdapter extends RecyclerView.Adapter<MyPsychologyCategoryAdapter.MyViewHolder> {

    Context context;
    List<PsychologyCategory> psychologyCategoryList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyPsychologyCategoryAdapter(Context context, List<PsychologyCategory> psychologyCategoryList) {
        this.context = context;
        this.psychologyCategoryList = psychologyCategoryList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_psychologycategory, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.txt_psychologist_name.setText(psychologyCategoryList.get(position).getName());
        holder.ratingBar.setRating((float)psychologyCategoryList.get(position).getRating());
        if(!cardViewList.contains(holder.card_psychologycategory))
            cardViewList.add(holder.card_psychologycategory);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //Set background for all item not choice
                for (CardView cardView : cardViewList)
                {
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));
                }

                //Set background for choice
                holder.card_psychologycategory.setCardBackgroundColor(
                        context.getResources()
                        .getColor(android.R.color.holo_orange_dark)
                );

                //Send local broadcast to enable button next
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_PSYCHOLOGYCATEGORY_SELECTED,psychologyCategoryList.get(pos));
                intent.putExtra(Common.KEY_STEP, 2);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
     }

    @Override
    public int getItemCount() {
        return psychologyCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_psychologist_name;
        RatingBar ratingBar;
        CardView card_psychologycategory;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_psychologycategory = itemView.findViewById(R.id.card_psychologycategory);
            txt_psychologist_name = itemView.findViewById(R.id.txt_psychologist_name);
            ratingBar = itemView.findViewById(R.id.rtb_psychologist);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }

}
