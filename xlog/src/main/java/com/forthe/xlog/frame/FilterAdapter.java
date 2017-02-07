package com.forthe.xlog.frame;

import android.content.Context;
import android.widget.BaseAdapter;

import com.forthe.xlog.core.ItemFilter;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterAdapter<T> extends BaseAdapter{
    private Context mContext = null;
    private boolean isReverse = false;
    private List<ItemFilter<T>> itemFilters;
    private List<T> dataList;
    private List<T> filterList;

    public FilterAdapter(Context mContext) {
        this.mContext = mContext;
        dataList = new ArrayList<>();
        filterList = new ArrayList<>();
        itemFilters = new ArrayList<>();
    }

    private boolean filter(T t){
        if(!itemFilters.isEmpty()){
            for(ItemFilter filter:itemFilters){
                if(filter.filter(t)) return true;
            }
        }
        return false;
    }

    public void setData(List<T> data){
        dataList.clear();
        if(null != data){
            for(T t:data){
                dataList.add(t);
                if(!filter(t)){
                    filterList.add(t);
                }
            }
        }
        if(filterList.isEmpty()){
           onEmpty(); 
        }
        
        notifyDataSetChanged();
    }
    
    
    @Override
    public int getCount() {
        if(null == filterList){
            return 0;
        }else{
            return filterList.size();
        }
    }

    @Override
    public T getItem(int position) {
        if(null == filterList || 0 == filterList.size()){
            return null;
        }else{
            if(isReverse){
                return filterList.get(getCount()-1-position);
            }else{
                return filterList.get(position);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    
    public Context getContext(){
        return mContext;
    }

    public void addData(T t){
        dataList.add(t);
        if(!filter(t)){
            filterList.add(t);
        }
        notifyDataSetChanged();
    }
    
    public void addData(List<T> ts){
        dataList.addAll(ts);
        for(T t:ts){
            if(!filter(t))
                filterList.add(t);
        }
        notifyDataSetChanged();
    }

    public void clear(){
        dataList.clear();
        filterList.clear();
        onEmpty();
        notifyDataSetChanged();
    }

    public boolean isReverse() {
        return isReverse;
    }

    protected void setReverse(boolean isReverse) {
        this.isReverse = isReverse;
    }

    public List<T> getDataList() {
        return filterList;
    }
    
    void onEmpty() {
        
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void addItemFilter(ItemFilter<T> itemFilter) {
        itemFilters.add(itemFilter);
        filterList.clear();
        if(!dataList.isEmpty()){
            for(T t:dataList){
                if(!filter(t))
                    filterList.add(t);
            }
        }
        notifyDataSetChanged();
    }
    public void removeItemFilter(ItemFilter<T> itemFilter) {
        itemFilters.remove(itemFilter);
        filterList.clear();
        if(!dataList.isEmpty()){
            for(T t:dataList){
                if(!filter(t))
                    filterList.add(t);
            }
        }
        notifyDataSetChanged();
    }

    public void clearFilters() {
        itemFilters.clear();
        filterList.clear();
        if(!dataList.isEmpty()){
            for(T t:dataList){
                filterList.add(t);
            }
        }
        notifyDataSetChanged();
    }


    public void onFilterChange() {
        filterList.clear();
        if(!dataList.isEmpty()){
            for(T t:dataList){
                if(!filter(t))
                    filterList.add(t);
            }
        }
        notifyDataSetChanged();
    }

}
