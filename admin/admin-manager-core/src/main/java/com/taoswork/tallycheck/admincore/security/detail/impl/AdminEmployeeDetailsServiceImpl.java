package com.taoswork.tallycheck.admincore.security.detail.impl;

import com.taoswork.tallycheck.admincore.security.detail.AdminEmployeeDetails;
import com.taoswork.tallycheck.admincore.security.detail.AdminEmployeeDetailsService;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import com.taoswork.tallycheck.tallyadmin.TallyAdminDataService;
import com.taoswork.tallycheck.tallyuser.UserCertificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

/**
 * Created by Gao Yuan on 2015/5/10.
 */
//@Service(AdminEmployeeDetailsService.COMPONENT_NAME)
//unable to declare @Service component here, because we don't have bean TallyUserDataService declared in this maven module
public class AdminEmployeeDetailsServiceImpl implements AdminEmployeeDetailsService {

    private UserCertificationService userCertificationService;

    private TallyAdminDataService tallyAdminDataService;

    public void setUserCertificationService(UserCertificationService userCertificationService) {
        this.userCertificationService = userCertificationService;
    }

    public void setTallyAdminDataService(TallyAdminDataService tallyAdminDataService) {
        this.tallyAdminDataService = tallyAdminDataService;
    }

    @Override
    public AdminEmployeeDetails loadPersonByUsername(String username) throws UsernameNotFoundException {

        Person person = userCertificationService.getPersonByAnyIdentity(username);
        if (person == null || person.getId() == null) {
            return null;
        }
        AdminEmployee employee = tallyAdminDataService.getAdminEmployeeByPersonId(person.getId().toString());
        AdminEmployeeDetails userDetails = new AdminEmployeeDetails( employee, person, null, new ArrayList<GrantedAuthority>());
        return userDetails;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadPersonByUsername(username);
    }

    @Override
    public AdminEmployeeDetails getPersistentEmployeeDetails() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (null != ctx) {
            Authentication auth = ctx.getAuthentication();
            if (null != auth && !auth.getName().equals(ANONYMOUS_USER_NAME)) {
                AdminEmployeeDetails userDetails = (AdminEmployeeDetails) auth.getPrincipal();
                return userDetails;
            }
        }
        return null;
    }

}