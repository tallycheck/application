package com.taoswork.tallycheck.admincore;

import com.taoswork.tallycheck.admincore.conf.AdminCoreConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TallyBookAdminCoreTest {
    @Test
    public void testCore() {
        try {
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            applicationContext.register(AdminCoreConfig.class);
            applicationContext.refresh();

        } catch (Exception e) {
            Assert.fail();
        }
    }
}
