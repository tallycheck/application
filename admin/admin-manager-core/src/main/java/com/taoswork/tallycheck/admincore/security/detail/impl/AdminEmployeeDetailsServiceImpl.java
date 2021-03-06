package com.taoswork.tallycheck.admincore.security.detail.impl;

import com.taoswork.tallycheck.admincore.security.detail.AdminEmployeeDetails;
import com.taoswork.tallycheck.admincore.security.detail.AdminEmployeeDetailsService;
import com.taoswork.tallycheck.admincore.security.detail.PersonDetails;
import com.taoswork.tallycheck.authentication.UserAuthenticationService;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import com.taoswork.tallycheck.tallyadmin.TallyAdminDataService;
import com.taoswork.tallycheck.tallyuser.TallyUserDataService;
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
public class AdminEmployeeDetailsServiceImpl
        extends PersonDetailsServiceImpl
        implements AdminEmployeeDetailsService {

    private TallyAdminDataService tallyAdminDataService;

    public void setTallyAdminDataService(TallyAdminDataService tallyAdminDataService) {
        this.tallyAdminDataService = tallyAdminDataService;
    }

    @Override
    public PersonDetails loadDetailsByAnyIdentity(String username) throws UsernameNotFoundException {
        return loadAdminEmployeeByAnyIdentity(username);
    }

    protected AdminEmployeeDetails loadAdminEmployeeByAnyIdentity(String username) throws UsernameNotFoundException {

        PersonDetails personDetails = super.loadDetailsByAnyIdentity(username);
        Person person = personDetails.getPerson();
        if (person == null || person.getId() == null) {
            throw new UsernameNotFoundException(username);
        }
        AdminEmployee employee = tallyAdminDataService.getAdminEmployeeByPersonId(person.getId().toHexString());
        if (employee == null || employee.getId() == null) {
            throw new UsernameNotFoundException(username);
        }
        AdminEmployeeDetails userDetails = new AdminEmployeeDetails(employee, personDetails);
        return userDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadAdminEmployeeByAnyIdentity(username);
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