package com.example.swipedeletedemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */

public abstract class BaseRvAdapter<T> extends RecyclerView.Adapter<BaseRvAdapter.MYViewHolder> {

    private Context mContext;

    //数据源
    private List<T> mData;

    public BaseRvAdapter(Context context, List<T> list) {
        mData = list;
        mContext = context.getApplicationContext();
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
        bindData(holder, position);
    }

    /**
     * 绑定数据
     *
     * @param holder   MYViewHolder
     * @param position position
     */
    public abstract void bindData(MYViewHolder holder, int position);

    /**
     * 获取数据源
     *
     * @return
     */
    public List<T> getData() {
        return mData;
    }

    /**
     * 获取数据源指定项
     *
     * @return
     */
    public T getData(int position) {
        if (position >= 0 && position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    /**
     * 弹出Toast
     *
     * @param msg
     */
    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

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
