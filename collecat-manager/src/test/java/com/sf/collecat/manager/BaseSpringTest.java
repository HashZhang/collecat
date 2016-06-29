package com.sf.collecat.manager;

import org.junit.After;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created by 862911 on 2016/6/25.
 */
@ContextConfiguration(locations = {"/spring.xml"})
public class BaseSpringTest extends AbstractJUnit4SpringContextTests {
    @Before
    public void init() {
    }
    @After
    public void drop() {
    }
}
