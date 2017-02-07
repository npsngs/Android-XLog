package com.forthe.xlog.parser;

import android.text.TextUtils;
import android.util.SparseIntArray;
import com.forthe.xlog.core.LogParser;

public class JsonParser implements LogParser {

    @Override
    public SparseIntArray parse(String input) {
        if(TextUtils.isEmpty(input)){
            return null;
        }

        SparseIntArray index = new SparseIntArray();
        int level = 0;
        int start = 0;
        int arrayLevel = 0;
        int arrayStart = 0;
        for(int i = 0; i < input.length(); i++){
            char c = input.charAt(i);
            switch (c){
                case '{':
                    level++;
                    if(1 == level){
                        start = i;
                    }
                    break;
                case '}':
                    if(level > 0){
                        level--;

                        if(0 == level){
                            index.put(start, i+1);
                        }
                    }
                    break;
                case '[':
                    arrayLevel++;
                    if(1 == arrayLevel){
                        arrayStart = i;
                    }
                    break;
                case ']':
                    if(arrayLevel > 0){
                        arrayLevel--;

                        if(0 == arrayLevel){
                            index.put(arrayStart, i+1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        level = 0;
        arrayLevel = 0;
        int end = input.length()-1;
        int arrayEnd = end;
        for(int i = end; i >= 0; i--){
            char c = input.charAt(i);
            switch (c){
                case '}':
                    level++;
                    if(1 == level){
                        end = i;
                    }
                    break;
                case '{':
                    if(level > 0){
                        level--;

                        if(0 == level){
                            index.put(i, end+1);
                        }
                    }
                    break;
                case '[':
                    arrayLevel++;
                    if(1 == arrayLevel){
                        arrayEnd = i;
                    }
                    break;
                case ']':
                    if(arrayLevel > 0){
                        arrayLevel--;

                        if(0 == arrayLevel){
                            index.put(i, arrayEnd+1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        //remove overlap
        /*
        for(int i = 0;i< index.size();i++){
            int startI = index.keyAt(i);
            int endI = index.get(startI);
            for(int j = i+1;j< index.size();j++){
                int startJ = index.keyAt(j);
                int endJ = index.get(startJ);
                if(endI < startJ || startI > endJ){//互相独立
                    continue;
                }else if(endI >= endJ && startI < startJ){//I 包含 J
                    index.removeAt(j);
                    j--;
                }else if(endI <= endJ && startI > startJ){//J 包含 I
                    index.removeAt(i);
                    i--;
                    break;
                }else if(endI > startJ || endJ > startI){//互相交叉
                    index.removeAt(j);
                    index.removeAt(i);
                    i--;
                    break;
                }
            }
        }
        */

        return index;
    }
}
