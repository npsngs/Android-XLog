package com.forthe.xlog.panel.filters;

import android.content.Context;
import android.content.SharedPreferences;

public class FilterContainer {
    private FilterItem[] items;
    private String pattern;
    private boolean isIgnoreCase = false;
    private SharedPreferences sp;
    public FilterContainer() {
        items = new FilterItem[]{
            new FilterItem("D", false, true),
            new FilterItem("I", false, true),
            new FilterItem("W", false, true),
            new FilterItem("E", false, true),
            new FilterItem("Find", false, false),
        };
    }

    public void initFromSp(Context context){
        sp = context.getSharedPreferences("x_log_config",Context.MODE_PRIVATE);
        isIgnoreCase = sp.getBoolean("ignore_case", true);
        pattern = sp.getString("filter_pattern", null);
        for(FilterItem item:items){
            item.isON = sp.getBoolean(item.getTitle(), false);
        }
    }

    public FilterItem[] getItems(){
        return items;
    }

    public boolean switchItem(String title){
        for (FilterItem item:items){
            if(item.title.equals(title)){
                item.isON = !item.isON;
                if(sp != null){
                    sp.edit().putBoolean(item.title, item.isON).apply();
                }

                if(onFilterChange != null){
                    onFilterChange.onFilterChange();
                }
                return item.isON;
            }
        }
        return false;
    }

    private OnFilterChange onFilterChange;
    public void setOnFilterChange(OnFilterChange onFilterChange) {
        this.onFilterChange = onFilterChange;
    }

    public boolean hasAnyFilterON(){
        for (FilterItem item:items){
            if(item.isON)return true;
        }
        return false;
    }

    public interface OnFilterChange{
        void onFilterChange();
    }

    public boolean isItemOn(String title){
        for (FilterItem item:items){
            if(item.title.equals(title)){
                return item.isON;
            }
        }
        return false;
    }

    public static class FilterItem{
        private String title;
        private boolean isON;
        private boolean isTagFilter;
        public FilterItem(String title, boolean isON, boolean isTagFilter) {
            this.title = title;
            this.isON = isON;
            this.isTagFilter = isTagFilter;
        }

        public boolean isON() {
            return isON;
        }

        public String getTitle() {
            return title;
        }

        public boolean isTagFilter() {
            return isTagFilter;
        }
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        if(sp != null){
            sp.edit().putString("filter_pattern", pattern).apply();
        }
    }

    public boolean isIgnoreCase() {
        return isIgnoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        isIgnoreCase = ignoreCase;
        if(sp != null){
            sp.edit().putBoolean("ignore_case", ignoreCase).apply();
        }
    }
}
