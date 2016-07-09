package com.taoswork.tallycheck.admincore.menu.impl;

import com.taoswork.tallycheck.admincore.conf.AdminCoreConfig;
import com.taoswork.tallycheck.admincore.menu.AdminMenuService;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.dataservice.manage.DataServiceManager;
import com.taoswork.tallycheck.general.extension.annotations.FrameworkService;
import com.taoswork.tallycheck.general.solution.menu.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Gao Yuan on 2015/4/28.
 */
@FrameworkService
@Service(AdminMenuService.SERVICE_NAME)
public class AdminMenuServiceImpl implements AdminMenuService, ApplicationContextAware {
    @Resource(name = AdminCoreConfig.DATA_SERVICE_MANAGER)
    private DataServiceManager dataServiceManager;

    private Menu fullMenu;

    public AdminMenuServiceImpl() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loadFullMenu();
    }

    private void loadFullMenu(){
        if(fullMenu != null)
            return;
        try {
            IMenuEntryUpdater updater = new IMenuEntryUpdater() {
                @Override
                public void update(IMenuEntry entry) {
                    String entity = entry.getEntity();
                    if(StringUtils.isNotEmpty(entity)){
                        if(StringUtils.isEmpty(entry.getUrl())){
                            String friendly = getFriendlyName(entity);
                            entry.setUrl("/" + friendly);
                        }
                        if(StringUtils.isEmpty(entry.getSecurityGuard())){
                            entry.setSecurityGuard(entity);
                        }
                    }
                }
            };
            fullMenu = MenuBuilder.buildMenu(updater);
//            InputStream fileStream = this.getClass().getClassLoader().getResource("menu/admin-menu.json").openStream();
//            fullMenu = new Menu(fileStream, updater);
//        } catch (IOException e) {
//            e.printStackTrace();
        }finally {

        }
    }

    private String getFriendlyName(String entityName) {
        return dataServiceManager.getEntityResourceName(entityName);
    }

    @Override
    public IMenu buildMenu(AdminEmployee adminEmployee) {
        if(fullMenu == null){
            loadFullMenu();
        }
        return fullMenu;
    }

    @Override
    public Collection<IMenuEntry> getEntriesOnPath(MenuPath path){
        return fullMenu.getEntriesOnPath(path);
    }

    @Override
    public MenuPath findMenuPathByUrl(String url) {
        String realUrl = "/" + url;
        MenuPath path = fullMenu.getSinglePathOfUrl(realUrl);
        return path;
    }

    @Override
    public MenuPath findMenuPathByEntryKey(String entryKey) {
        MenuPath path = fullMenu.getSinglePathOfEntry(entryKey);
        return path;
    }

    @Override
    public Collection<String> workoutMenuKeyPathByUrl(String url) {
        MenuPath path = this.findMenuPathByUrl(url);
        if (path != null) {
            return path.getPath();
        }

        return new ArrayList<String>();
    }

    @Override
    public Collection<String> workoutMenuKeyByEntryKey(String entryKey) {
        MenuPath path = this.findMenuPathByEntryKey(entryKey);
        if (path != null) {
            return path.getPath();
        }

        return new ArrayList<String>();
    }
}
