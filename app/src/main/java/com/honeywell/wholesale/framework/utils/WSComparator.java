package com.honeywell.wholesale.framework.utils;


import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Created by xiaofei on 7/19/16.
 *
 */
public class WSComparator implements Comparator<Object> {
    private String property;
    private boolean isUp = true;

    public WSComparator(String property) {
        this.property = property;
    }

    public WSComparator(String property, boolean isUp) {
        this.property = property;
        this.isUp = isUp;
    }

    @Override
    public int compare(Object lhs, Object rhs) {
        String lhsClsName = getClassName(lhs);
        String rhsClsName = getClassName(rhs);

        if (!lhsClsName.equals(rhsClsName)){
            throw new RuntimeException("Compare object is not same");
        }

        Class cls = lhs.getClass();

        Field f;

        try {
            f = cls.getDeclaredField(property);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return 0;
        }

        if (f == null){
            return 0;
        }


        Object value1 = null;
        Object value2 = null;

        f.setAccessible(true);
        try {
            value1 = f.get(lhs);
            value2 = f.get(rhs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (value1 == null || value2 == null){
            return 0;
        }

        String v1Str = (String)value1;
        String v2Str = (String)value2;

        Float v1Number, v2Number;
        try{
            v1Number = Float.parseFloat(v1Str);
            v2Number = Float.parseFloat(v2Str);
        }catch (NumberFormatException e){
            if (isUp){
                return v1Str.compareTo(v2Str);
            }
            return v2Str.compareTo(v1Str);
        }

        if (isUp){
            return v1Number.compareTo(v2Number);
        }
        return v2Number.compareTo(v1Number);

    }

    private String getClassName(Object object){
        return object.getClass().getName();
    }

}
