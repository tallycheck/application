package com.taoswork.tallycheck.admincore.security.impl;

import com.taoswork.tallycheck.admincore.TallyBookAdminCoreRoot;
import com.taoswork.tallycheck.admincore.security.AdminSecurityService;
import com.taoswork.tallycheck.admincore.security.detail.AdminEmployeeDetails;
import com.taoswork.tallycheck.admincore.security.detail.AdminEmployeeDetailsService;
import com.taoswork.tallycheck.authority.core.ProtectionScope;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Gao Yuan on 2015/5/14.
 */
@Service(AdminSecurityService.COMPONENT_NAME)
public class AdminSecurityServiceImpl implements AdminSecurityService {
    private static final String ANONYMOUS_USER_NAME = "anonymousUser";

    @Resource(name = AdminEmployeeDetailsService.COMPONENT_NAME)
    AdminEmployeeDetailsService tallyUserDataService;

    @Override
    public Person getPersistentPerson(){
        AdminEmployeeDetails personDetails = tallyUserDataService.getPersistentEmployeeDetails();
        if(null == personDetails){
            return null;
        }
        return personDetails.getPerson();
    }

    @Override
    public AdminEmployee getPersistentAdminEmployee(){
        AdminEmployeeDetails personDetails = tallyUserDataService.getPersistentEmployeeDetails();
        if(null == personDetails){
            return null;
        }
        return personDetails.getEmployee();
    }

    @Override
    public String getCurrentUserId() {
        return getPersistentPerson().getId().toString();
    }

    @Override
    public ProtectionScope getCurrentProtectionScope() {
        return TallyBookAdminCoreRoot.PROTECTION_SCOPE;
    }
}
