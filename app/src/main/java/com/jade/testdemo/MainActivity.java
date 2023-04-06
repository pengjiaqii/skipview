package com.jade.testdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements PagerGridLayoutManager
        .PageListener, RadioGroup.OnCheckedChangeListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("GCS", "onCreate");

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
                Log.d("shay", "registerAdapterDataObserver count = " + count);
            }
        });
        mAdapter.setItemCallback(new LauncherListCallback() {
            @Override
            public void onItemClick() {

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
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(mAdapter.ismDeleteVisible()){
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
        List<String> data = new ArrayList<>();
        data.add("加一个");
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
        List<String> data = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            data.add("加五个" + i);
        }
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
