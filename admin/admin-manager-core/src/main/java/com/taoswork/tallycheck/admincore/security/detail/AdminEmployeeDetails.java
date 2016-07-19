package com.taoswork.tallycheck.admincore.security.detail;

import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyuser.AccountStatus;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by Gao Yuan on 2015/5/10.
 */
public class AdminEmployeeDetails extends PersonDetails {
    protected AdminEmployee employee;

    public AdminEmployeeDetails(AdminEmployee employee, PersonDetails personDetails){
        super(personDetails);
        this.employee = employee;
    }

    public AdminEmployeeDetails(
            AdminEmployee employee, Person person, Collection<? extends GrantedAuthority> authorities) {
        super(person,
                employee.getStatus() == null ? new AccountStatus() : employee.getStatus(),
                authorities);
        this.employee = employee;
    }

    public AdminEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(AdminEmployee employee) {
        this.employee = employee;
    }
}
