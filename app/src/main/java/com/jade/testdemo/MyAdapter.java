package com.jade.testdemo;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static List<String> data = new ArrayList<>();
    private View mHeadView;
    private int TYPE_HEADER = 1001;

    static {
        for (int i = 1; i <= 15; i++) {
            data.add(i + "");
        }
    }


    //头部的set方法
    public void setHeadView(View headView) {
        mHeadView = headView;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            //找的头布局
            View headerView = inflater.inflate(R.layout.launcher_rv_header, parent, false);
            return new HeaderViewHolder(headerView);
        }

        View view = inflater.inflate(R.layout.layout_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {

        } else if (holder instanceof MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            final String title = data.get(position - 1);
            myViewHolder.tv_title.setText(title);
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "item" + title + " 被点击了", Toast.LENGTH_SHORT).show();
                    myViewHolder.tv_title.setText("G " + title);
                    notifyItemChanged(position);
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        //在第一个位置添加头
        if (position == 0) {
            return TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    /**
     * 头布局的viewholder
     */
    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
