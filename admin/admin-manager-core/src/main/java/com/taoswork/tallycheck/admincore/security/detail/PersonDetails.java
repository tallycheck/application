package com.taoswork.tallycheck.admincore.security.detail;

import com.taoswork.tallycheck.datadomain.tallyuser.AccountStatus;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by Gao Yuan on 2015/4/21.
 */
public class PersonDetails extends User {

    protected Person person;

    public PersonDetails(Person person,
                         AccountStatus status,
                         Collection<? extends GrantedAuthority> authorities) {
        super(person.getName(), "",
                status.enabled, true, true, !status.locked,
                authorities);
        this.person = person;
    }

    public PersonDetails(PersonDetails other){
        super(other.getUsername(), other.getPassword(), other.isEnabled(),
                other.isAccountNonExpired(), other.isCredentialsNonExpired(),
                other.isAccountNonLocked(), other.getAuthorities());
        this.person = other.getPerson();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getPersonId() {
        return person.getId().toString();
    }

}
