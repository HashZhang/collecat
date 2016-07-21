package com.sf.collecat.manager.task;

import com.sf.collecat.manager.schedule.DefaultScheduler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by 862911 on 2016/6/20.
 */
@ContextConfiguration(locations = {"/spring.xml"})
public class TaskTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private DefaultScheduler defaultScheduler;
    @Test
    public void testCreateTask() throws InterruptedException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        defaultScheduler.createTask("* * * * *","select * from hotnews","other",100,"COLLECAT_TEST","COLLECAT_TEST:@08f0Wp^",
//                "http://10.202.34.30:8292/mom-mon/monitor/requestService.pub",sdf.parse("2016-06-21 13:48:33"),"csv",60,"TESTDB","modify_tm",true);
//        while (true) {
//            TimeUnit.DAYS.sleep(1);
//        }
    }
}
