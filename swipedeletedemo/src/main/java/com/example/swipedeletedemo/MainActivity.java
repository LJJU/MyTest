package com.example.swipedeletedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicDefaultHeader;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.loadmore.DefaultLoadMoreViewFooter;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PtrClassicFrameLayout pcfl;
    private RecyclerView rv;
    private List<String> mdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pcfl = (PtrClassicFrameLayout) findViewById(R.id.test_recycler_view_frame);
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        mdata = getData();

        //侧滑删除功能在Item项的View中实现
        setAdapter();

        //下拉刷新与加载更多功能
        setPtrClassicFragmeLayout();
    }

    private List<String> getData() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add("name：" + i);
        }
        return result;
    }

    private void setAdapter() {
        BaseRvAdapter adapter = new RvAdapter(this, mdata);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setPtrClassicFragmeLayout() {
        //刷新
        pcfl.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                showToast("onRefreshBegin");
                pcfl.refreshComplete();
            }
        });

        //加载更多
        pcfl.setLoadMoreEnable(true);
        pcfl.setFooterView(new DefaultLoadMoreViewFooter());
        pcfl.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                showToast("loadMore");
                pcfl.loadMoreComplete(true);
            }
        });
    }


    /**
     * 显示Toast
     *
     * @param msg
     */
    public void showToast(String msg) {
        Tool.show(getApplicationContext(), msg);
    }
}
