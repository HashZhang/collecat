package com.sf.collecat.common.mapper;

import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Node;

import java.util.List;

public interface NodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Node record);

    int insertSelective(Node record);

    Node selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Node record);

    int updateByPrimaryKey(Node record);

}