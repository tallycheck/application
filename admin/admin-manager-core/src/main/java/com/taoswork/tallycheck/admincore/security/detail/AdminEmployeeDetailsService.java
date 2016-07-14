package com.taoswork.tallycheck.admincore.security.detail;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by Gao Yuan on 2015/5/14.
 */
public interface AdminEmployeeDetailsService extends PersonDetailsService {

    AdminEmployeeDetails loadAdminEmployeeByAnyIdentity(String username) throws UsernameNotFoundException;

    public AdminEmployeeDetails getPersistentEmployeeDetails();
}
