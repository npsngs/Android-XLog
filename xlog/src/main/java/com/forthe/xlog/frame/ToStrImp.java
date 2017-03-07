package com.forthe.xlog.frame;
import android.util.Log;

import com.forthe.xlog.core.ToStr;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ToStrImp implements ToStr{
    @Override
    public String toStr(Object o){
        return toStr(o, 1, 0);
    }

    public String toStr(Object o, int maxDepth, int curDepth) {
        try {
            if(null == o){
                return "null";
            }

            if(o instanceof String){
                return printString((String) o);
            }else if(o instanceof Collection){
                return printCollection((Collection<?>) o, maxDepth, curDepth);
            }else if(o instanceof Map){
                return printMap((Map<?, ?>) o);
            }else if(o.getClass().isArray()) {
                return printArray(o, true);
            }else if(o instanceof Throwable){
                return Log.getStackTraceString((Throwable) o);
            }else{
                return printBean(o, maxDepth, curDepth);
            }
        }catch (StackOverflowError error){
            return "Too Large...";
        }
    }

    private String printString(String str){
        if("null".equals(str)){
            return "\"null\"";
        }
        return str;
    }

    private String printCollection(Collection<?> collection, int maxDepth, int curDepth){
        StringBuilder sb = new StringBuilder();
        sb.append(collection.getClass().getSimpleName()).append(" size=").append(collection.size());
        if(collection.size() > 0){
            sb.append(" [\n");
            for (Object item:collection){
                sb.append(" ").append(toStr(item,maxDepth,curDepth+1)).append(",\n");
            }
            if(sb.length()>2){
                sb.delete(sb.length()-2, sb.length());
            }
            sb.append("\n]");
        }else{
            sb.append("\n");
        }
        return sb.toString();
    }

    private String printMap(Map<?,?> map){
        StringBuilder sb = new StringBuilder();
        sb.append(map.getClass().getSimpleName()).append(" size=").append(map.size());
        if(map.size() > 0) {
            Set<?> keys = map.keySet();
            sb.append(" {\n");
            for (Object key:keys){
                Object value = map.get(key);
                sb.append(" ").append(toStr(key)).append("=").append(toStr(value)).append(",\n");
            }

            if(sb.length()>2){
                sb.delete(sb.length()-2, sb.length());
            }
            sb.append("\n}");
        }else{
            sb.append("\n");
        }

        return sb.toString();
    }

    private String printBean(Object bean, int maxDepth, int curDepth){
        if(curDepth >= maxDepth){
            return bean.getClass().getSimpleName();
        }

        Class<?> classObj = bean.getClass();
        String ts = bean.toString();
        if(ts.startsWith(classObj.getName()+'@')){
            StringBuilder sb = new StringBuilder();
            sb.append(classObj.getSimpleName()).append("{");
            Field[] fields = classObj.getDeclaredFields();
            if(fields == null){
                sb.append("empty");
            } else {
                for(Field field:fields){
                    if("this$0".equals(field.getName())){
                        continue;
                    }
                    field.setAccessible(true);
                    try {
                        Object value = field.get(bean);
                        String valueStr = bean.equals(value)?"@this":toStr(value,maxDepth,curDepth+1);
                        sb.append(field.getName()).append("=").append(valueStr).append(", ");
                    } catch (IllegalAccessException e) {
                    }
                }


                if(' ' == sb.charAt(sb.length()-1)){
                    sb.delete(sb.length()-2,sb.length());
                }
            }
            sb.append("}");
            return sb.toString();
        }else{
            return ts;
        }
    }

    private String printArray(Object array, boolean isNeedType){
        if(null == array){
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        String str = array.toString();
        char type = 'X';
        int dimension = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '[') {
                dimension++;
            } else {
                type = str.charAt(i);
                break;
            }
        }


        if(isNeedType){
            sb.append(getArrayTypeName(type));
            Object tmp = array;
            for(int i = 0; i < dimension; i++){
                int len = ((Object[]) tmp).length;
                sb.append('[').append(len).append(']');
                Object tmp2;
                for(int j=0;j<len;j++){
                    tmp2 = ((Object[]) tmp)[j];
                    if(null != tmp2){
                        tmp = tmp2;
                        break;
                    }
                }
            }
            sb.append(" ");
        }



        if(dimension > 1){
            if(dimension == 2){
                sb.append("[\n");
                for (int i = 0; i < ((Object[]) array).length; i++) {
                    sb.append("\t").append(printArray(((Object[]) array)[i],false)).append(",\n");
                }
                if('\n' == sb.charAt(sb.length()-1)){
                    sb.deleteCharAt(sb.length()-2);
                }
                sb.append("]");
            }else{
                sb.append("[");
                for (int i = 0; i < ((Object[]) array).length; i++) {
                    sb.append(printArray(((Object[]) array)[i],false)).append(",");
                }
                if(sb.length() > 1){
                    sb.deleteCharAt(sb.length()-1);
                }
                sb.append("]");
            }
        }else{
            switch (type) {
                case 'I':
                    sb.append(Arrays.toString((int[]) array));
                    break;
                case 'D':
                    sb.append(Arrays.toString((double[]) array));
                    break;
                case 'Z':
                    sb.append(Arrays.toString((boolean[]) array));
                    break;
                case 'B':
                    sb.append(Arrays.toString((byte[]) array));
                    break;
                case 'S':
                    sb.append(Arrays.toString((short[]) array));
                    break;
                case 'J':
                    sb.append(Arrays.toString((long[]) array));
                    break;
                case 'F':
                    sb.append(Arrays.toString((float[]) array));
                    break;
                case 'L':
                    Object[] objects = (Object[]) array;
                    sb.append("[");
                    for (int i = 0; i < objects.length; ++i) {
                        sb.append(printBean(objects[i], 1, 0));
                        if (i != objects.length - 1) {
                            sb.append(",");
                        }
                    }
                    sb.append("]");
                    break;
                default:
                    sb.append(Arrays.toString((Object[]) array));
                    break;
            }
        }
        return sb.toString();
    }


    private String getArrayTypeName(char type){
        switch (type){
            case 'I':
                return "int";
            case 'D':
                return "double";
            case 'Z':
                return "boolean";
            case 'B':
                return "byte";
            case 'S':
                return "short";
            case 'J':
                return "long";
            case 'F':
                return "float";
            case 'L':
                return "Object";
            default:
                return "Object";
        }
    }
}
