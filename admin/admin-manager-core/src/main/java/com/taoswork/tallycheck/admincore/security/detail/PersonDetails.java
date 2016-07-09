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

    public PersonDetails(Person person, String password,
                         AccountStatus status,
                         Collection<? extends GrantedAuthority> authorities) {
        super(person.getName(), password,
                status.enabled, true, true, status.locked,
                authorities);
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

}
