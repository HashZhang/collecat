package com.sf.collecat.common;

/**
 * Created by 862911 on 2016/6/16.
 */
public interface Constants {
    String NODE_PATH = "/collecat/node";
    String TEM_NODE_PATH = "/collecat/node/node";
    String JOB_PATH = "/collecat/job";
    String JOB_DETAIL_PATH = "/collecat/job-detail";
    String COMMON_SEPARATOR = ":";
    String STRING_ENCODING = "UTF-8";

    String JOB_INIT = "unallocated";
    String JOB_FINISHED = "finished";
    String JOB_EXCEPTION = "exception";

    int JOB_INIT_VALUE = 0;
    int JOB_FINISHED_VALUE = 1;
    int JOB_EXCEPTION_VALUE = 2;
}
