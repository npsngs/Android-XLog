package com.forthe.xlog.panel.filters;

import android.text.TextUtils;

import com.forthe.xlog.core.ItemFilter;

import java.util.ArrayList;
import java.util.List;


public abstract class TagFilter implements ItemFilter<String> {
    private List<String> tags;
    public TagFilter() {
        this.tags = new ArrayList<>();
    }

    public boolean addTag(String tag){
        if(TextUtils.isEmpty(tag)){
            return false;
        }

        for(String tagStr:tags){
            if(tagStr.equals(tag)){
                return false;
            }
        }
        tags.add(tag);
        return true;
    }

    public boolean removeTag(String tag){
        if(TextUtils.isEmpty(tag)){
            return false;
        }
        return tags.remove(tag);
    }

    @Override
    public boolean filter(String item) {
        if(tags.isEmpty()){
            return true;
        }

        for(String tag:tags){
            if(TextUtils.isEmpty(item)){
                continue;
            }
            if(onFilter(tag, item)){
                return true;
            }
        }
        return false;
    }

    protected abstract boolean onFilter(String tag, String item);
}