package com.taoswork.tallycheck.admincore.security.detail.impl;

import com.taoswork.tallycheck.admincore.security.detail.PersonDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by gaoyuan on 7/14/16.
 */
public class PersonAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private PersonDetailsService personDetailsService;

    public PersonDetailsService getPersonDetailsService() {
        return personDetailsService;
    }

    public void setPersonDetailsService(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return personDetailsService.loadPersonByAnyIdentity(username);
    }
}
