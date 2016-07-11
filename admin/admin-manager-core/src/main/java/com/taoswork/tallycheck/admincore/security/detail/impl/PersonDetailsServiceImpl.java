package com.taoswork.tallycheck.admincore.security.detail.impl;

import com.taoswork.tallycheck.admincore.security.detail.PersonDetails;
import com.taoswork.tallycheck.admincore.security.detail.PersonDetailsService;
import com.taoswork.tallycheck.authentication.UserAuthenticationService;
import com.taoswork.tallycheck.datadomain.tallyuser.AccountStatus;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import com.taoswork.tallycheck.datasolution.annotations.EntityServiceMark;
import com.taoswork.tallycheck.tallyuser.TallyUserDataService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by Gao Yuan on 2015/4/21.
 */
@EntityServiceMark(PersonDetailsService.COMPONENT_NAME)
@Service(PersonDetailsService.COMPONENT_NAME)
public class PersonDetailsServiceImpl implements PersonDetailsService {
//    private static final String ADMIN_USER_NAME_ON_EMPTY_DB = "admin";
//    private static final String ADMIN_PASSWORD_ON_EMPTY_DB = "admin";

    private UserAuthenticationService userAuthenticationService;

    private TallyUserDataService tallyUserDataService;

    //
//    private Boolean userDbHasData = null;
    @Override
    public PersonDetails loadPersonByUsername(String username) throws UsernameNotFoundException {
//        if(null == userDbHasData){
//            if(personDao.isThereAnyData()){
//                userDbHasData = true;
//            } else {
//                PersonDetails manualUserDetail =
//                        new PersonDetails(-1L, "",  ADMIN_USER_NAME_ON_EMPTY_DB, ADMIN_PASSWORD_ON_EMPTY_DB,
//                                true, true, true, true, new ArrayList<GrantedAuthority>() );
//                return manualUserDetail;
//            }
//        }

        Person person = tallyUserDataService.getPersonByAnyIdentity(username);
        if (person == null || person.getId() == null) {
            return null;
        }
        PersonDetails userDetails = new PersonDetails(person, null, new AccountStatus(), new ArrayList<GrantedAuthority>());
        return userDetails;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadPersonByUsername(username);
    }

    @Override
    public PersonDetails getPersistentPersonDetails() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (null != ctx) {
            Authentication auth = ctx.getAuthentication();
            if (null != auth && !auth.getName().equals(ANONYMOUS_USER_NAME)) {
                PersonDetails userDetails = (PersonDetails) auth.getPrincipal();
                return userDetails;
            }
        }
        return null;
    }

}
