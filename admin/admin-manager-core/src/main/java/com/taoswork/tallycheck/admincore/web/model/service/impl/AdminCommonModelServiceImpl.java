package com.taoswork.tallycheck.admincore.web.model.service.impl;

import com.taoswork.tallycheck.admincore.security.AdminSecurityService;
import com.taoswork.tallycheck.admincore.web.model.service.AdminCommonModelService;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import com.taoswork.tallycheck.general.solution.menu.Menu;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by Gao Yuan on 2015/5/14.
 */
@Component(AdminCommonModelService.COMPONENT_NAME)
public class AdminCommonModelServiceImpl implements AdminCommonModelService {

    @Resource(name = AdminSecurityService.COMPONENT_NAME)
    AdminSecurityService adminSecurityService;

    @Override
    public AdminEmployee getPersistentAdminEmployee() {
        return adminSecurityService.getPersistentAdminEmployee();
    }


    @Override
    public Person getPersistentPerson() {
        return adminSecurityService.getPersistentPerson();
    }

    @Override
    public Menu getAdminMenu() {
        return null;
    }
}
