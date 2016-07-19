package com.taoswork.tallycheck.admincore.security.detail.impl;

import com.taoswork.tallycheck.admincore.security.detail.PersonDetails;
import com.taoswork.tallycheck.admincore.security.detail.PersonDetailsService;
import com.taoswork.tallycheck.authentication.UserAuthenticationService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by gaoyuan on 7/14/16.
 */
public class PersonAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private PersonDetailsService personDetailsService;
    private UserAuthenticationService userCertificationService;

    public PersonDetailsService getPersonDetailsService() {
        return personDetailsService;
    }

    public void setPersonDetailsService(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
        this.userCertificationService = personDetailsService.getUserAuthenticationService();
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();
        PersonDetails personDetails = (PersonDetails) userDetails;
        boolean passwordOk = userCertificationService.checkPassword(personDetails.getPersonId(), presentedPassword);

        if (!passwordOk) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails userDetails = personDetailsService.loadDetailsByAnyIdentity(username);
        return userDetails;
    }
}
