package com.taoswork.tallycheck.admincore.security.detail;

import com.taoswork.tallycheck.authentication.UserAuthenticationService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by Gao Yuan on 2015/5/14.
 */
public interface PersonDetailsService extends UserDetailsService {
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";

    public static final String COMPONENT_NAME = "PersonDetailsService";

    UserAuthenticationService getUserAuthenticationService();

    //
    //    private Boolean userDbHasData = null;
    PersonDetails loadDetailsByAnyIdentity(String username) throws UsernameNotFoundException;

    public PersonDetails getPersistentPersonDetails();
}
