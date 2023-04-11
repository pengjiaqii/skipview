package com.jade.testdemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v4.os.UserManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jade.testdemo.util.AddAppListDB;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AppListActivity extends Activity {

    private RecyclerView mRecyclerView;
    private List<LauncherActivityInfo> mApps = new ArrayList<>();
    private List<AddAppListModel> mLastDatas = new ArrayList();
    private ArrayList<AddAppListModel> mInitialData = new ArrayList();
    private AppListAdapter mAdapter;
    private LauncherApps mLauncherApps;
    private UserManager mUserManager;

    private AddAppListDB mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        findViewById(R.id.back).setOnClickListener(v -> finish());

        mLauncherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        mUserManager = (UserManager) getSystemService(Context.USER_SERVICE);

        mDb = new AddAppListDB(this);

        initData();

        mRecyclerView = (RecyclerView) findViewById(R.id.add_app_list_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new AppListAdapter());
        // DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        // dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.recyclerview_divider));
        // mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    protected void initData() {
        ArrayList<AddAppListModel> models = AddAppListModel.getAllAppFromDB(this);
        Log.d("launcher3j06", "AppListActivity---initData---models:" + models.size());

        final List<UserHandle> profiles = mUserManager.getUserProfiles();
        for (UserHandle user : profiles) {
            mApps = mLauncherApps.getActivityList(null, user);
            for (LauncherActivityInfo app : mApps) {
                Log.d("launcher3j06", "AppListActivity---initData---app.getLabel:" + app.getLabel());
                Log.d("launcher3j06", "AppListActivity---initData---app.getPackageName:" + app.getComponentName().getPackageName());
                Log.d("launcher3j06", "AppListActivity---initData---app.getClassName:" + app.getComponentName().getClassName());
            }
        }

        for (AddAppListModel model : models) {
            for (LauncherActivityInfo app : mApps) {
                String packageName = app.getComponentName().getPackageName();
                if(TextUtils.equals(packageName + app.getComponentName().getClassName(),model.getCurrentPackage() + model.getCurrentClassName())){
                    if(!TextUtils.equals("com.test.addapp",packageName)){
                        Drawable appIcon = app.getIcon(DisplayMetrics.DENSITY_DEFAULT);
                        model.setIcon(appIcon);
                        if (model.getCurrentIsChecked() == 0) {
                            //unchecked
                            mLastDatas.add(model);
                        }else {
                            //checked
                            mLastDatas.add(model);
                        }
                    }
                }
            }
        }

        //remove duplicate elements from the list
        mLastDatas = mLastDatas.stream().distinct().collect(Collectors.toList());

        mInitialData.clear();
        for (AddAppListModel model : mLastDatas) {
            Log.d("launcher3j06", "AppListActivity---mLastDatas:model---" + model.toString());
            if (model.getCurrentIsChecked() == 1) {
                //is checked
                mInitialData.add(model);
            }
        }
    }

    @Override
    protected void onStop() {
        Log.i("launcher3j06", "onStop");
        super.onStop();
        ArrayList<AddAppListModel> lastCheckedDatas = new ArrayList();
        for (AddAppListModel model : mLastDatas) {
            if (model.getCurrentIsChecked() == 1) {
                //is checked
                lastCheckedDatas.add(model);
            }
        }

        boolean isEqual = equalList(mInitialData,lastCheckedDatas);
        Log.d("launcher3j06", "---equalList---相等吗:" + isEqual);

        if(!isEqual){
            for (AddAppListModel model : mLastDatas) {
                Log.d("launcher3j06", "---onStop---model.getCurrentPackage:" + model.getCurrentPackage());
                Log.d("launcher3j06", "---onStop---model.getCurrentAppName:" + model.getCurrentAppName());
                Log.d("launcher3j06", "---onStop---model.getCurrentClassName:" + model.getCurrentClassName());
                Log.d("launcher3j06", "---onStop---model.getCurrentIsChecked:" + model.getCurrentIsChecked());

                ContentValues values = new ContentValues();
                values.put(AddAppListDB.COLUMN_PACKAGE,model.getCurrentPackage());
                values.put(AddAppListDB.COLUMN_APP_NAME,model.getCurrentAppName());
                values.put(AddAppListDB.COLUMN_CLASS_NAME,model.getCurrentClassName());
                //0 means unchecked , 1 means checked。default unchecked
                values.put(AddAppListDB.COLUMN_IS_CHECKED,model.getCurrentIsChecked());
                // values.put(AddAppListDB.COLUMN_PACKAGE, key.componentName.getPackageName());
//                mDb.insertOrReplace(values);
            }
        }

        //reload launcher apps
        Log.i("launcher3j06", "发送广播");
        Intent intent = new Intent();
        intent.setAction("com.android.launcher3j06.reload");
        sendBroadcast(intent);
    }


    private boolean equalList(ArrayList list1, ArrayList list2) {
        Log.d("launcher3j06", "---equalList---list1.size:" + list1.size());
        Log.d("launcher3j06", "---equalList---list2.size:" + list2.size());
        if (list1.size() != list2.size()) {
            Log.d("launcher3j06", "---equalList---不相等");
            return false;
        }
        if (list2.containsAll(list1)) {
            Log.d("launcher3j06", "---equalList---相等");
            return true;
        }
        return false;
    }

    class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(
                    LayoutInflater.from(AppListActivity.this).inflate(R.layout.layout_app_list_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Drawable icon = mLastDatas.get(position).getIcon();
            String label = mLastDatas.get(position).getCurrentAppName();
            holder.add_app_list_name.setText(label);

//
//            String positionPackageName = mLastDatas.get(position).getCurrentPackage();
//            String positionAppName = mLastDatas.get(position).getCurrentAppName();
//            String positionClassName = mLastDatas.get(position).getCurrentClassName();
//
//            Bitmap iconBitmap = getLocalIconBitmap(positionPackageName,positionClassName);
//            if(null != iconBitmap){
//                holder.add_app_list_icon.setImageBitmap(iconBitmap);
//            }else{
//                holder.add_app_list_icon.setImageDrawable(icon);
//            }
//
//            if(
//                    TextUtils.equals("com.android.settings",positionPackageName)
//
//            ){
//                //can not change
//                holder.add_app_list_button.setImageResource(R.drawable.icon_un_add);
//                holder.add_app_list_item.setEnabled(false);
//                holder.add_app_list_item.setClickable(false);
//            }else{
//                if(mLastDatas.get(position).getCurrentIsChecked() == 0){
//                    // unchecked
//                    holder.add_app_list_button.setImageResource(R.drawable.icon_add);
//                }else{
//                    // checked
//                    holder.add_app_list_button.setImageResource(R.drawable.icon_del);
//                }
//                holder.add_app_list_item.setEnabled(true);
//                holder.add_app_list_item.setClickable(true);
//            }
//
//
//            holder.add_app_list_item.setOnClickListener(v -> {
//                if(mLastDatas.get(position).getCurrentIsChecked() == 0){
//                    //change unchecked to checked
//                    mLastDatas.get(position).setCurrentIsChecked(1);
//                    holder.add_app_list_button.setImageResource(R.drawable.icon_del);
//                }else{
//                    //change checked to unchecked
//                    mLastDatas.get(position).setCurrentIsChecked(0);
//                    holder.add_app_list_button.setImageResource(R.drawable.icon_add);
//                }
//                Log.d("launcher3j06", "add_app_list_item---click---positionPackageName:" + positionPackageName);
//                Log.d("launcher3j06", "add_app_list_item---click---positionAppName:" + positionAppName);
//                Log.d("launcher3j06", "add_app_list_item---click---currentIsChecked:" + mLastDatas.get(position).getCurrentIsChecked());
//            });
        }

        @Override
        public int getItemCount() {
            return mLastDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout add_app_list_item;
            ImageView add_app_list_icon;
            TextView add_app_list_name;
            ImageView add_app_list_button;

            public MyViewHolder(View view) {
                super(view);
                add_app_list_item = (RelativeLayout) view.findViewById(R.id.add_app_list_item);
                add_app_list_icon = (ImageView) view.findViewById(R.id.add_app_list_icon);
                add_app_list_name = (TextView) view.findViewById(R.id.add_app_list_name);
                add_app_list_button = (ImageView) view.findViewById(R.id.add_app_list_button);
            }
        }
    }
}