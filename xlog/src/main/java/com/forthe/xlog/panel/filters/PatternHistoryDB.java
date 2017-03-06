package com.forthe.xlog.panel.filters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PatternHistoryDB {
    private PatternDBHelper dbHelper;

    public PatternHistoryDB(Context context) {
        this.dbHelper = new PatternDBHelper(context);
    }

    public List<String> searchByPrefix(String prefix, int limitCount){
        String sql = "SELECT * FROM xlog_pattern_history WHERE key LIKE '"+prefix+"%'"
                +" LIMIT "+limitCount;
        List<String> ret = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            ret.add(cursor.getString(0));
        }
        return ret;
    }

    public List<String> getMostRecentlyUsed(int limitCount){
        String sql = "SELECT * FROM xlog_pattern_history ORDER BY use_count DESC, last_time ASC "
                +" LIMIT "+limitCount;
        List<String> ret = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            ret.add(cursor.getString(0));
        }
        return ret;
    }

    /**
     * 返回使用次数
     */
    public int recordUsePattern(String pattern){
        String sqlR = "SELECT * FROM xlog_pattern_history WHERE key='"+pattern+"';";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlR, null);
        if(cursor.moveToNext()){
            int useCount = cursor.getInt(1)+1;
            long current = System.currentTimeMillis();
            String sqlW = "UPDATE xlog_pattern_history SET use_count="
                    +useCount+" ,last_time="+current+" WHERE key='"+pattern+"';";
            db.execSQL(sqlW);
            return useCount;
        }else{
            long current = System.currentTimeMillis();
            String sqlW = "INSERT OR FAIL INTO xlog_pattern_history(key, use_count, last_time) VALUES('"
                    +pattern+"',1,"+current+");";
            db.execSQL(sqlW);
            return 1;
        }
    }

    class PatternDBHelper extends SQLiteOpenHelper{
        public PatternDBHelper(Context context) {
            super(context, "XLogDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS xlog_pattern_history(" +
                    "key VARCHAR PRIMARY KEY," +
                    "use_count INTEGER NOT NULL," +
                    "last_time LONG NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
