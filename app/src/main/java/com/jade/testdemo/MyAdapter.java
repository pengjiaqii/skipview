package com.jade.testdemo;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static List<String> data = new ArrayList<>();
    private View mHeadView;
    private int TYPE_HEADER = 1001;

    int[] iconBgRes = {R.drawable.icon_orange_bg, R.drawable.icon_green_bg, R.drawable.icon_blue_bg,
            R.drawable.icon_red_bg, R.drawable.icon_purple_bg, R.drawable.icon_cblue_bg};


    static {
        for (int i = 1; i <= 15; i++) {
            data.add(i + "");
        }
        data.add(0, "xxx");
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
            Log.d("Adapter", "onBindViewHolder---HeaderViewHolder--->" + holder);
            Log.d("Adapter", "onBindViewHolder---position--->" + position);

        } else if (holder instanceof MyViewHolder) {
            Log.d("Adapter", "onBindViewHolder---MyViewHolder--->" + holder);
            Log.d("Adapter", "onBindViewHolder---position--->" + position);
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            final String title = data.get(position - 1);
            myViewHolder.tv_title.setText(title);
            int iconIndex = position % 6;
            Log.d("Adapter", "onBindViewHolder---第几张图--iconIndex->" + iconIndex);

            myViewHolder.itemView.setBackgroundResource(iconBgRes[iconIndex]);

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "item" + position + " 被点击了", Toast.LENGTH_SHORT).show();
//                    myViewHolder.tv_title.setText("G " + position);
//                    notifyItemChanged(position);
                }
            });
            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(view.getContext(), "item" + position + " 长按了", Toast.LENGTH_SHORT).show();
                    return false;
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
        RelativeLayout rv_header;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            rv_header = (RelativeLayout) itemView.findViewById(R.id.rv_header);
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
