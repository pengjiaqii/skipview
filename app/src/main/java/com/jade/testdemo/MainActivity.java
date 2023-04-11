package com.jade.testdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.pm.LauncherApps;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements PagerGridLayoutManager
        .PageListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int mRows = 3;
    private int mColumns = 2;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private PagerGridLayoutManager mLayoutManager;
    //    private RadioGroup mRadioGroup;
    private TextView mPageTotal;        // 总页数
    private TextView mPageCurrent;      // 当前页数

    private int mTotal = 0;
    private int mCurrent = 0;

    protected LauncherApps mLauncherApps;
    protected  UserManager mUserManager;
    private  PackageManager mPm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        mLauncherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        mUserManager = (UserManager) getSystemService(Context.USER_SERVICE);
        mPm = getPackageManager();
//        mRadioGroup = (RadioGroup) findViewById(R.id.orientation_type);
//        mRadioGroup.setOnCheckedChangeListener(this);

        mPageTotal = (TextView) findViewById(R.id.page_total);
        mPageCurrent = (TextView) findViewById(R.id.page_current);

        mLayoutManager = new PagerGridLayoutManager(mRows, mColumns, PagerGridLayoutManager
                .HORIZONTAL);


        // 系统带的 RecyclerView，无需自定义
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // 水平分页布局管理器
        mLayoutManager.setPageListener(this);    // 设置页面变化监听器
        mRecyclerView.setLayoutManager(mLayoutManager);

        // 设置滚动辅助工具
        PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
        pageSnapHelper.attachToRecyclerView(mRecyclerView);

        // 如果需要查看调试日志可以设置为true，一般情况忽略即可
        PagerConfig.setShowLog(true);

        // 使用原生的 Adapter 即可
        mAdapter = new MyAdapter();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                int count = mAdapter.getItemCount();
                Log.d(TAG, "registerAdapterDataObserver count = " + count);
            }
        });
        mAdapter.setItemCallback(new LauncherListCallback() {
            @Override
            public void onItemClick(int position) {
                AddAppListModel model = mAdapter.data.get(position-1);
                Log.d(TAG, "点击 model = " + model.getCurrentPackage());
                Log.d(TAG, "点击 model = " + model.getCurrentClassName());
                ComponentName component = new ComponentName(model.getCurrentPackage(), model.getCurrentClassName());
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(component);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick() {
                //显示所有删除按钮
                mAdapter.setDeleteVisible(true);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDeleteClick() {

            }
        });
        mRecyclerView.setAdapter(mAdapter);

        getAllActivity();
    }


    private void getAllActivity(){
        final List<UserHandle> profiles = mUserManager.getUserProfiles();
        for (UserHandle user : profiles) {
            final List<LauncherActivityInfo> apps = mLauncherApps.getActivityList(null, user);
            ArrayList<AddAppListModel> models = new ArrayList<>();
            synchronized (this) {
                for (LauncherActivityInfo app : apps) {
                    Log.d(TAG, "verifyApplications---LauncherActivityInfo---app.getLabel:" + app.getLabel());
                    Log.d(TAG, "verifyApplications---LauncherActivityInfo---app.getPackageName:" + app.getComponentName().getPackageName());
                    Log.d(TAG, "verifyApplications---LauncherActivityInfo---app.getClassName:" + app.getComponentName().getClassName());
                    AddAppListModel model = new AddAppListModel();
                    model.setCurrentAppName(app.getLabel().toString());
                    Drawable appIcon = app.getIcon(DisplayMetrics.DENSITY_DEFAULT);
                    model.setIcon(appIcon);
                    String className = app.getComponentName().getClassName();
                    model.setCurrentClassName(className);
                    String packageName = app.getComponentName().getPackageName();
                    model.setCurrentPackage(packageName);
                    models.add(model);
                }
            }
            mAdapter.data.addAll(models);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mAdapter.ismDeleteVisible()) {
            mAdapter.setDeleteVisible(false);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPageSizeChanged(int pageSize) {
        mTotal = pageSize;
        Log.e("shay", "总页数 = " + pageSize);
        mPageTotal.setText("共 " + pageSize + " 页");
    }

    @Override
    public void onPageSelect(int pageIndex) {
        mCurrent = pageIndex;
        Log.e("shay", "选中页码 = " + pageIndex);
        mPageCurrent.setText("第 " + (pageIndex + 1) + " 页");
    }

    /**
     * 往尾部加一个
     *
     * @param view
     */
    public void addOne(View view) {
        List<AddAppListModel> data = new ArrayList<>();

        AddAppListModel model = new AddAppListModel();
        model.setCurrentAppName("加一");
        data.add(model);

        mAdapter.data.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 往尾部减一个
     *
     * @param view
     */
    public void removeOne(View view) {
        if (mAdapter.data.size() > 0) {
            mAdapter.data.remove(mAdapter.data.size() - 1);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 加五个
     *
     * @param view
     */
    public void addMore(View view) {
        List<AddAppListModel> data = new ArrayList<>();
//        for (int i = 1; i <= 5; i++) {
//            data.add("加五个" + i);
//        }
        mAdapter.data.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 减五个
     *
     * @param view
     */
    public void removeMore(View view) {
        for (int i = 1; i <= 5; i++) {
            mAdapter.data.remove(mAdapter.data.size() - i);
        }
        mAdapter.notifyDataSetChanged();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//        int type = -1;
//        if (checkedId == R.id.type_horizontal) {
//            type = mLayoutManager.setOrientationType(PagerGridLayoutManager.HORIZONTAL);
//        } else if (checkedId == R.id.type_vertical) {
//            type = mLayoutManager.setOrientationType(PagerGridLayoutManager.VERTICAL);
//        } else {
//            throw new RuntimeException("不支持的方向类型");
//        }

//        Log.i("shay", "type == " + type);
    }

    public void prePage(View view) {
        mLayoutManager.prePage();
    }

    public void nextPage(View view) {
        mLayoutManager.nextPage();
    }

    public void smoothPrePage(View view) {
        mLayoutManager.smoothPrePage();
    }

    public void smoothNextPage(View view) {
        mLayoutManager.smoothNextPage();
    }

    public void firstPage(View view) {
        mRecyclerView.smoothScrollToPosition(0);
    }

    public void lastPage(View view) {
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }
}
