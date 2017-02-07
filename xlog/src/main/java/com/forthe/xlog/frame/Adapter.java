package com.forthe.xlog.frame;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class Adapter<T> extends BaseAdapter{
    private Context mContext = null;
    private boolean isReverse = false;
    public Adapter(Context mContext) {
        this.mContext = mContext;
        dataList = new ArrayList<>();
    }

    protected List<T> dataList = null;
    public void setData(List<T> data){
        dataList.clear();
        if(null != data){
            for(T t:data){
                dataList.add(t);
            }
        }
        if(dataList.isEmpty()){
           onEmpty(); 
        }
        
        notifyDataSetChanged();
    }
    
    
    @Override
    public int getCount() {
        if(null == dataList){
            return 0;
        }else{
            return dataList.size();
        }
    }
    
    public final int getDataSize() {
        if(null == dataList){
            return 0;
        }else{
            return dataList.size();
        }
    }

    @Override
    public T getItem(int position) {
        if(null == dataList || 0 == dataList.size()){
            return null;
        }else{
            if(isReverse){
                return dataList.get(getCount()-1-position);
            }else{
                return dataList.get(position);
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
    
    public void addData(int position, T t){
        dataList.add(position, t);
        notifyDataSetChanged();
    }
    
    public void addData(T t){
        dataList.add(t);
        notifyDataSetChanged();
    }
    
    public void addData(List<T> ts){
        dataList.addAll(ts);
        notifyDataSetChanged();
    }
    
    public void addData(int pos, List<T> ts){
        dataList.addAll(pos, ts);
        notifyDataSetChanged();
    }
    
    public void addData(T t, int positon){
        dataList.add(positon, t);
        notifyDataSetChanged();
    }
    
    protected void removeData(int positon){
        dataList.remove(positon);
        if(dataList.isEmpty()){
            onEmpty();
        }
        notifyDataSetChanged();
    }

    public void removeData(T t){
        dataList.remove(t);
        if(dataList.isEmpty()){
            onEmpty();
        }
        notifyDataSetChanged();
    }
    
    public void clear(){
        dataList.clear();
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
        return dataList;
    }
    
    public void onEmpty() {
        
    }

    public void setContext(Context context) {
        this.mContext = context;
    }
}
