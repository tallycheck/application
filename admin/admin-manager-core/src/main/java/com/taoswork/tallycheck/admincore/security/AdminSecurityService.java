package com.taoswork.tallycheck.admincore.security;

import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import com.taoswork.tallycheck.dataservice.frontend.IProtectedAccessContext;

/**
 * Created by Gao Yuan on 2015/5/14.
 */
public interface AdminSecurityService extends IProtectedAccessContext {
    public static final String COMPONENT_NAME="AdminSecurityService";

    Person getPersistentPerson();

    AdminEmployee getPersistentAdminEmployee();
}
