package com.vegh.bletask.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.fitroad.android.utils.BaseHolder;import java.util.List;

/**
 * this adapter  custom for recyclerview  generic  adapter
 *
 * @param <T> item Binding
 * @param <B> list data
 */
public abstract class AdapterCustom<T extends ViewDataBinding, B> extends RecyclerView.Adapter<BaseHolder<T>> {
    public List<B> list;
public  int positionItems;
    public AdapterCustom(List<B> list) {
        this.list = list;

    }

    public AdapterCustom() {

    }


    /**
     * layout  for onCreateViewHolder
     *
     * @return
     */
    public abstract int layoutId();

    public abstract void onBindHolder(@NonNull BaseHolder<T> holder, int position);

    @NonNull
    @Override
    public BaseHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseHolder<>(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                layoutId(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder<T> holder, int position) {
        positionItems=position;
        onBindHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    /**
     * + append data  to list
     *
     * @param li
     */
    public void addData(List<B> li) {
        if (list != null && li != null) {


            this.list.addAll(li);
        } else {
            this.list = li;
        }
        notifyDataSetChanged();
    }

    public void addData(List<B> li,int state,int move ) {
        if (list != null && li != null) {


            this.list.addAll(positionItems,li);
        } else {
            this.list = li;
        }
        notifyItemInserted(positionItems);
    }
    int sizeInsert=0;
    public void addDataInsert(List<B> li) {

        if (list != null && li != null) {

            sizeInsert= list.size();
            this.list.addAll(sizeInsert,li);
        } else {
            this.list = li;
        }
        notifyItemInserted(sizeInsert+1);
    }
    /**
     * remove all Data
     */
    public void removeAll() {
        this.list.clear();
        notifyDataSetChanged();

    }

    public void clearList() {
        if (list != null) {
            if (list.size() != 0) {
                list.clear();
            }
        }
    }



    /**
     * replace all list and change view adapter
     */
    public void replaceAll(List<B> li) {
        if (AdapterCustom.this.list != null && li!=null&& AdapterCustom.this.list.size() != 0&&li.size()!=0) {
            list.
                    clear();
            list.addAll(li);
        } else {
            list = li;
        }
        notifyDataSetChanged();
    }

    public void setList(List<B> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (list != null) {
            if (list.size()!=0)
            this.list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }
}
