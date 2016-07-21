package com.sf.collecat.common.mapper;

import com.sf.collecat.common.model.Job;

import java.util.List;

public interface JobMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Job record);

    int insertSelective(Job record);

    Job selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Job record);

    int updateByPrimaryKey(Job record);

    List<Job> selectAllExceptionJob();

    List<Job> selectAllJob();

    int countJobsWithSubtaskId(int subtaskId);

    int countExceptionJobsWithSubtaskId(int subtaskId);
}