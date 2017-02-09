package com.example.swipedeletedemo;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */

public abstract class BaseRvAdapter<T> extends RecyclerView.Adapter<BaseRvAdapter.MYViewHolder> {

    //数据源
    private List<T> mData;

    public BaseRvAdapter(List<T> list) {
        mData = list;
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public void onBindViewHolder(MYViewHolder holder, int position) {
        //数据绑定
        bindData(holder, position, mData);
    }

    /**
     * 绑定数据
     * @param holder MYViewHolder
     * @param position position
     * @param data data
     */
    public abstract void bindData(MYViewHolder holder, int position, List<T> data);

    public static class MYViewHolder extends RecyclerView.ViewHolder {

        public MYViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * 根据viewId获取View
         *
         * @param id
         * @param <T>
         * @return
         */
        public <T extends View> T getView(int id) {
            return MYViewHolder.getView(itemView, id);
        }

        public static <T extends View> T getView(View root, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) root.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                root.setTag(viewHolder);
            }

            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = root.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

}
