package com.taoswork.tallycheck.admincore.web.model.service;

import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import com.taoswork.tallycheck.general.solution.menu.Menu;

/**
 * Created by Gao Yuan on 2015/5/14.
 */
public interface AdminCommonModelService {
    public static final String COMPONENT_NAME = "AdminCommonModelService";

    AdminEmployee getPersistentAdminEmployee();

    Person getPersistentPerson();

    Menu getAdminMenu();
}
