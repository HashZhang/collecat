package com.sf.collecat.common.utils;

import com.sf.collecat.common.Constants;
import lombok.NonNull;

/**
 * Created by 862911 on 2016/6/25.
 */
public class StrUtils {
    public static String makeString(@NonNull Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : args) {
            stringBuilder.append(arg);
        }
        return stringBuilder.toString();
    }

    public static String getZKJobPath(int id) {
        return makeString(Constants.JOB_PATH,Constants.ZK_SEPARATOR,id);
    }

    public static String getZKJobDetailPath(int id) {
        return makeString(Constants.JOB_DETAIL_PATH,Constants.ZK_SEPARATOR,id);
    }

    public static String getZKJobPath(String id) {
        return makeString(Constants.JOB_PATH,Constants.ZK_SEPARATOR,id);
    }

    public static String getZKJobDetailPath(String id) {
        return makeString(Constants.JOB_DETAIL_PATH,Constants.ZK_SEPARATOR,id);
    }
}
