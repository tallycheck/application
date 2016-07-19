package com.taoswork.tallycheck.admincore.security.impl;

import com.taoswork.tallycheck.admincore.TallyBookAdminCoreRoot;
import com.taoswork.tallycheck.admincore.security.AdminSecurityService;
import com.taoswork.tallycheck.admincore.security.detail.AdminEmployeeDetails;
import com.taoswork.tallycheck.admincore.security.detail.AdminEmployeeDetailsService;
import com.taoswork.tallycheck.authority.core.ProtectionScope;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyadmin.TallyAdminDataDomain;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Gao Yuan on 2015/5/14.
 */
@Service(AdminSecurityService.COMPONENT_NAME)
public class AdminSecurityServiceImpl implements AdminSecurityService {

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
    public boolean isAdministrator() {
        return true;
    }

    @Override
    public String getCurrentBu() {
        return TallyAdminDataDomain.ADMINISTRATION_BU;
    }

    @Override
    public String getCurrentPersonId() {
        return getPersistentPerson().getId().toHexString();
    }

    @Override
    public String getCurrentEmployeeId() {
        return getPersistentAdminEmployee().getId().toHexString();
    }

    @Override
    public ProtectionScope getCurrentProtectionScope() {
        return TallyBookAdminCoreRoot.PROTECTION_SCOPE;
    }
}
