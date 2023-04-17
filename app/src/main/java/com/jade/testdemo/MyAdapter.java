package com.jade.testdemo;


import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jade.testdemo.util.LunarCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static List<AddAppListModel> data = new ArrayList<>();

    private int TYPE_HEADER = 1001;

    private LauncherListCallback mCallback;

    private boolean mDeleteVisible = false;

    int[] iconBgRes = {R.drawable.icon_orange_bg, R.drawable.icon_green_bg, R.drawable.icon_blue_bg,
            R.drawable.icon_red_bg, R.drawable.icon_purple_bg, R.drawable.icon_cblue_bg};

    /**
     * 点击，长按事件的一些回调，可选用
     *
     * @param callback
     */
    public void setItemCallback(LauncherListCallback callback){
        mCallback = callback;
    }

    /**
     * 设置删除按钮是否可见
     */
    public void setDeleteVisible(boolean deleteVisible){
        mDeleteVisible = deleteVisible;
    }

    public boolean ismDeleteVisible(){
        return mDeleteVisible;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_HEADER){
            //找的头布局
            View headerView = inflater.inflate(R.layout.launcher_rv_header, parent, false);
            return new HeaderViewHolder(headerView);
        }

        View view = inflater.inflate(R.layout.launcher_rv_layout_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position){

        if(holder instanceof HeaderViewHolder){
            Log.d("MyAdapter", "onBindViewHolder---HeaderViewHolder--->" + holder);
            Log.d("MyAdapter", "onBindViewHolder---position--->" + position);
            final Locale l = Locale.getDefault();
            Date now = new Date();
            ((HeaderViewHolder) holder).the_clock.setText(new SimpleDateFormat(DateFormat.getBestDateTimePattern(l, "Hm"), l).format(now));
            ((HeaderViewHolder) holder).date.setText(new SimpleDateFormat(DateFormat.getBestDateTimePattern(l, "MMMdd EEEE"), l).format(now));

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int monthOfYear = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            LunarCalendar lunarCalendar = LunarCalendar.getInstance();
            lunarCalendar.set(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日!");
            ((HeaderViewHolder) holder).lunar_date.setText(String.format("%s年 %s月%s", lunarCalendar.getAnimalsYear(),
                    lunarCalendar.getLunarMonth(), lunarCalendar.getLunarDate()));

        } else if(holder instanceof MyViewHolder){
            int pos = holder.getAdapterPosition();
            Log.d("MyAdapter", "onBindViewHolder---MyViewHolder--->" + holder);
            Log.d("MyAdapter", "onBindViewHolder---pos--->" + pos);
            Log.d("MyAdapter", "onBindViewHolder---data--->" + data.size());
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            final AddAppListModel model = data.get(pos - 1);
            Log.d("MyAdapter", "onBindViewHolder---model--->" + model);
            myViewHolder.tv_title.setText(model.getCurrentAppName());
            myViewHolder.app_icon.setImageDrawable(model.getIcon());

            int iconIndex = pos % 6;
            Log.d("MyAdapter", "onBindViewHolder---第几张图--iconIndex->" + iconIndex);
            myViewHolder.itemView.setBackgroundResource(iconBgRes[iconIndex]);

            if(mDeleteVisible){
                if(TextUtils.equals(model.getCurrentAppName(), "添加应用")
                        || TextUtils.equals(model.getCurrentAppName(), "设置")){
                    myViewHolder.delete_item.setVisibility(View.INVISIBLE);
                } else{
                    myViewHolder.delete_item.setVisibility(View.VISIBLE);
                }
            } else{
                myViewHolder.delete_item.setVisibility(View.INVISIBLE);
            }

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mCallback.onItemClick(pos);
                    Toast.makeText(view.getContext(), data.get(pos - 1).getCurrentAppName() + " 被点击了",
                            Toast.LENGTH_SHORT).show();
                    //                    myViewHolder.tv_title.setText("G " + pos);
                    //                    notifyItemChanged(pos);
                }
            });
            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view){
                    mCallback.onItemLongClick();
                    Toast.makeText(view.getContext(), data.get(pos - 1).getCurrentAppName() + " 长按了", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            myViewHolder.delete_item.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mCallback.onDeleteClick();
                    Toast.makeText(view.getContext(), data.get(pos - 1).getCurrentAppName() + " 删除了", Toast.LENGTH_SHORT).show();
                    MyAdapter.this.data.remove(pos - 1);
                    MyAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position){
        //在第一个位置添加头
        if(position == 0){
            return TYPE_HEADER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount(){
        return data.size() + 1;
    }


    /**
     * 头布局的viewholder
     */
    class HeaderViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout rv_header;

        TextView the_clock;
        TextView date;
        TextView lunar_date;

        public HeaderViewHolder(View itemView){
            super(itemView);
            rv_header = (RelativeLayout) itemView.findViewById(R.id.rv_header);

            the_clock = (TextView) itemView.findViewById(R.id.the_clock);
            date = (TextView) itemView.findViewById(R.id.date);
            lunar_date = (TextView) itemView.findViewById(R.id.lunar_date);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        ImageView app_icon;
        ImageView delete_item;

        public MyViewHolder(View itemView){
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            app_icon = (ImageView) itemView.findViewById(R.id.app_icon);
            delete_item = (ImageView) itemView.findViewById(R.id.delete_item);
        }
    }
}
