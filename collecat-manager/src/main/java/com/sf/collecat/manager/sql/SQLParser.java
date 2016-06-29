package com.sf.collecat.manager.sql;

import com.alibaba.druid.sql.parser.ParserException;
import com.sf.collecat.common.model.Job;
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
     * @param task 传入task
     * @param lastTT task截止时间
     * @return
     * @throws SQLSyntaxErrorException
     * @throws ParserException
     */
    public List<Job> parse(Task task, Date lastTT) throws SQLSyntaxErrorException, ParserException;
}
