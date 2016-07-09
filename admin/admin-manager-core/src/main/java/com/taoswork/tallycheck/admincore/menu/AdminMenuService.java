package com.taoswork.tallycheck.admincore.menu;

import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.general.solution.menu.IMenu;
import com.taoswork.tallycheck.general.solution.menu.IMenuEntry;
import com.taoswork.tallycheck.general.solution.menu.MenuPath;

import java.util.Collection;

/**
 * Created by Gao Yuan on 2015/4/28.
 */
public interface AdminMenuService {
    public static final String SERVICE_NAME = "AdminMenuService";
    IMenu buildMenu(AdminEmployee adminEmployee);

    Collection<IMenuEntry> getEntriesOnPath(MenuPath path);

    MenuPath findMenuPathByUrl(String url);

    MenuPath findMenuPathByEntryKey(String entryKey);

    Collection<String> workoutMenuKeyPathByUrl(String url);

    Collection<String> workoutMenuKeyByEntryKey(String entryKey);

}
