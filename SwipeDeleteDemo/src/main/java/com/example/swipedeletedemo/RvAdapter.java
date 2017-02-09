package com.example.swipedeletedemo;

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

    public RvAdapter(List<String> list) {
        super(list);
    }

    @Override
    public BaseRvAdapter.MYViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        SwipeMenuLayout layout = (SwipeMenuLayout) view;
        layout.setIos(false);
        return new BaseRvAdapter.MYViewHolder(view);
    }

    @Override
    public void bindData(MYViewHolder holder, int position, List<String> data) {
        TextView tv = holder.getView(R.id.item_textView);
        tv.setText(data.get(position));
    }
}
