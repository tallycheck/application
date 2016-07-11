package com.taoswork.tallycheck.admincore.menu.impl;

import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminGroup;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminProtection;
import com.taoswork.tallycheck.datadomain.tallybiz.module.ModuleUsage;
import com.taoswork.tallycheck.datadomain.tallybiz.subject.*;
import com.taoswork.tallycheck.datadomain.tallybiz.work.WorkFeedback;
import com.taoswork.tallycheck.datadomain.tallybiz.work.WorkPlan;
import com.taoswork.tallycheck.datadomain.tallybiz.work.WorkTicket;
import com.taoswork.tallycheck.datadomain.tallybus.ModuleEntry;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import com.taoswork.tallycheck.general.solution.menu.IMenuEntryUpdater;
import com.taoswork.tallycheck.general.solution.menu.Menu;
import com.taoswork.tallycheck.general.solution.menu.MenuEntryBuilder;

/**
 * Created by Gao Yuan on 2016/3/15.
 */
class MenuBuilder {
    public static final String USER_GROUP = "G_User";
    public static final String USER_ENTRY_PERSON = "E_Person";

    public static final String MANAGEMENT_GROUP = "G_Management";
    public static final String MANAGEMENT_ENTRY_ADMIN_EMP = "E_AdminEmployee";
    public static final String MANAGEMENT_ENTRY_ADMIN_ROLE = "E_AdminRole";
    public static final String MANAGEMENT_ENTRY_ADMIN_RES = "E_AdminResource";
    public static final String MANAGEMENT_ENTRY_MODULE_ENTRY = "E_ModuleEntry";

    public static final String BUSINESS_GROUP = "G_Business";
    public static final String BUSINESS_ENTRY_BU = "E_BusinessUnit";
    public static final String BUSINESS_ENTRY_EMP = "E_Employee";
    public static final String BUSINESS_ENTRY_ROLE = "E_Role";
    public static final String BUSINESS_ENTRY_BP = "E_BusinessPartner";
    public static final String BUSINESS_ENTRY_ASSET = "E_Asset";
    public static final String BUSINESS_ENTRY_MODULE_USAGE = "E_ModuleUsage";
    public static final String BUSINESS_ENTRY_WORKPLAN = "E_WorkPlan";
    public static final String BUSINESS_ENTRY_WORKTICKET = "E_WorkTicket";
    public static final String BUSINESS_ENTRY_WORKFEEDBACK = "E_WorkFeedback";

    public static Menu buildMenu(IMenuEntryUpdater updater){
        MenuEntryBuilder builder = MenuEntryBuilder.createRootNode();
        builder.beginEntry()
                .key("user").name(USER_GROUP).icon("fa-user")
                    .beginEntry()
                        .key("person").name(USER_ENTRY_PERSON).icon("glyphicon-user").entity(Person.class)
                    .endEntry()
//                    .beginEntry()
//                        .key("bu").name(BU).icon("glyphicon-user").entity(Bu.class)
//                    .endEntry()
                .beginSiblingEntry()
                    .key("admin-security").name(MANAGEMENT_GROUP).icon("fa-user-secret")
                    .beginEntry()
                        .key("admin-res").name(MANAGEMENT_ENTRY_ADMIN_RES).icon("glyphicon-user").entity(AdminProtection.class)
                    .beginSiblingEntry()
                        .key("admin-employee").name(MANAGEMENT_ENTRY_ADMIN_EMP).icon("glyphicon-user").entity(AdminEmployee.class)
                    .beginSiblingEntry()
                        .key("admin-role").name(MANAGEMENT_ENTRY_ADMIN_ROLE).icon("glyphicon-user").entity(AdminGroup.class)
                    .beginSiblingEntry()
                        .key("module-entry").name(MANAGEMENT_ENTRY_MODULE_ENTRY).icon("glyphicon-user").entity(ModuleEntry.class)
                    .endEntry()
                .beginSiblingEntry()
                    .key("business").name(BUSINESS_GROUP).icon("fa-briefcase")
                    .beginEntry()
                        .key("bu").name(BUSINESS_ENTRY_BU).icon("").entity(Bu.class)
                    .beginSiblingEntry()
                        .key("employee").name(BUSINESS_ENTRY_EMP).entity(Employee.class)
                    .beginSiblingEntry()
                        .key("role").name(BUSINESS_ENTRY_ROLE).entity(Role.class)
                    .beginSiblingEntry()
                        .key("asset").name(BUSINESS_ENTRY_ASSET).entity(Asset.class)
                .beginSiblingEntry()
                        .key("bp").name(BUSINESS_ENTRY_BP).entity(Bp.class)
                    .beginSiblingEntry()
                        .key("moduleusage").name(BUSINESS_ENTRY_MODULE_USAGE).entity(ModuleUsage.class)
                    .beginSiblingEntry()
                        .key("workplan").name(BUSINESS_ENTRY_WORKPLAN).entity(WorkPlan.class)
                    .beginSiblingEntry()
                        .key("workticket").name(BUSINESS_ENTRY_WORKTICKET).entity(WorkTicket.class)
                    .beginSiblingEntry()
                        .key("workfeedback").name(BUSINESS_ENTRY_WORKFEEDBACK).entity(WorkFeedback.class)
                    .endEntry()
                .endEntry();

        return builder.makeMenu(updater);
    }
}
