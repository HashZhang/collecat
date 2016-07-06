package com.sf.collecat.common.mapper;

import com.sf.collecat.common.model.Subtask;

import java.util.List;

public interface SubtaskMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Subtask record);

    int insertSelective(Subtask record);

    Subtask selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Subtask record);

    int updateByPrimaryKey(Subtask record);

    Subtask selectByTaskIDAndDbUrl(Subtask record);
}