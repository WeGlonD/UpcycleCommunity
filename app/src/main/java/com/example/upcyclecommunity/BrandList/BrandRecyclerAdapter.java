package com.example.upcyclecommunity.BrandList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.upcyclecommunity.R;
import com.example.upcyclecommunity.database.Brand;

import java.util.ArrayList;

public class BrandRecyclerAdapter extends RecyclerView.Adapter<BrandRecyclerAdapter.BrandViewHolder> {

    private ArrayList<Brand> BrandArrayList;
    private Context context;

    public BrandRecyclerAdapter(ArrayList<Brand> BrandArrayList, Context context){
        this.BrandArrayList=BrandArrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_item,parent,false);
        BrandViewHolder holder = new BrandViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(BrandArrayList.get(position).getPic())
                .into(holder.iv_pic);

        holder.tv_tags.setText(BrandArrayList.get(position).getTags());
        holder.tv_name.setText(BrandArrayList.get(position).getName());
        holder.tv_url.setText(BrandArrayList.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        return (BrandArrayList != null ? BrandArrayList.size() : 0);
    }


    public class BrandViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_pic;
        TextView tv_name;
        TextView tv_tags;
        TextView tv_url;
        public BrandViewHolder(View itemView){
            super(itemView);
            this.iv_pic = itemView.findViewById(R.id.iv_pic);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_tags = itemView.findViewById(R.id.tv_tags);
            this.tv_url = itemView.findViewById(R.id.tv_url);
            tv_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView now = (TextView)view;
                    String url = (String) now.getText();
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    it.setData(Uri.parse(url));
                    context.startActivity(it);
                }
            });
        }
    }
}
