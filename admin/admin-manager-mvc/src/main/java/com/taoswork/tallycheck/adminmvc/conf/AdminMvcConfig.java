package com.taoswork.tallycheck.adminmvc.conf;

import com.taoswork.tallycheck.adminmvc.TallyBookAdminMvcRoot;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

/**
 * Created by Gao Yuan on 2015/5/14.
 */
@Configuration
@ComponentScan(
        basePackageClasses = TallyBookAdminMvcRoot.class,
        //   useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        {Controller.class})
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        {Configuration.class})
        }
)
public class AdminMvcConfig {
}
