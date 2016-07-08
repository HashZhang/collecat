package com.sf.collecat.manager.sql;

import com.alibaba.druid.sql.parser.ParserException;
import com.sf.collecat.common.model.Job;
import com.sf.collecat.common.model.Subtask;
import com.sf.collecat.common.model.Task;

import java.sql.SQLSyntaxErrorException;
import java.util.Date;
import java.util.List;

/**
 * SQL解析器
 * @author 862911 Hash Zhang
 * @version 1.0.0
 * @time 2016/6/28
 */
public interface SQLParser {
    /**
     * 将task拆分成job
     * @param subtask 传入task
     * @return
     * @throws SQLSyntaxErrorException
     * @throws ParserException
     */
    public Job parse(Subtask subtask) throws SQLSyntaxErrorException, ParserException;

    public List<Subtask> parse(Task task) throws SQLSyntaxErrorException;

    public List<Job> parse(Task task, Date date) throws SQLSyntaxErrorException;
}
