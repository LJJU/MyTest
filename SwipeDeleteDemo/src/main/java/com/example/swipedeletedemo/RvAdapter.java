package com.example.swipedeletedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swipedeletedemo.view.SwipeMenuLayout;

import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */

public class RvAdapter extends BaseRvAdapter<String> {

    public RvAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public BaseRvAdapter.MYViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        SwipeMenuLayout layout = (SwipeMenuLayout) view;
        layout.setIos(false);
        return new BaseRvAdapter.MYViewHolder(view);
    }

    @Override
    public void bindData(MYViewHolder holder, int position) {
        String data = getData(position);
        if (data != null) {
            TextView tv = holder.getView(R.id.item_textView);
            tv.setText(data);
        }
        setItemListener(holder, position);
    }

    /**
     * 设置Item监听器
     *
     * @param holder
     * @param position
     */
    private void setItemListener(MYViewHolder holder, final int position) {
        holder.getView(R.id.item_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("item_btn1");
            }
        });
        holder.getView(R.id.item_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("item_btn2");
            }
        });
        holder.getView(R.id.item_contentView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = getData(position);
                if (data != null) {
                    showToast(data);
                }
            }
        });
    }
}
