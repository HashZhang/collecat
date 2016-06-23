package com.sf.collecat.manager.util;

import com.sf.collecat.common.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by 862911 on 2016/6/20.
 */
public class PropertyLoader {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<Task> getTasks(Properties properties) throws ParseException {
        String taskString = properties.getProperty("tasks");
        String taskStrings[] = taskString.split(",");
        List<Task> tasks = new LinkedList<>();
        for (int i = 0; i < taskStrings.length ; i++) {
            Task task = new Task();
            task.setSchemaUsed(properties.getProperty(taskStrings[i].concat(".schema.used")));
            task.setInitialSql(properties.getProperty(taskStrings[i].concat(".initial.sql")));
            task.setIsActive(Boolean.parseBoolean(properties.getProperty(taskStrings[i].concat(".is.active"))));
            task.setKafkaUrl(properties.getProperty(taskStrings[i].concat(".kafka.url")));
            task.setKafkaTopic(properties.getProperty(taskStrings[i].concat(".kafka.topic")));
            task.setKafkaTopicTokens(properties.getProperty(taskStrings[i].concat(".kafka.topic.token")));
            task.setKafkaMessageSize(Integer.parseInt(properties.getProperty(taskStrings[i].concat(".kafka.message.size"))));
            task.setKafkaClusterName(properties.getProperty(taskStrings[i].concat(".kafka.cluster.name")));
            task.setTimeField(properties.getProperty(taskStrings[i].concat(".time.field")));
            task.setLastTime(sdf.parse(properties.getProperty(taskStrings[i].concat(".last.time"))));
            task.setRoutineTime(Integer.parseInt(properties.getProperty(taskStrings[i].concat(".routine.time"))));
            task.setAllocateRoutine(properties.getProperty(taskStrings[i].concat(".allocate.routine")));
            task.setMessageFormat(properties.getProperty(taskStrings[i].concat(".message.format")));
            tasks.add(task);
        }
        return tasks;
    }
}
