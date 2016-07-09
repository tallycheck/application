package com.taoswork.tallycheck.admincore;

import com.taoswork.tallycheck.authority.core.ProtectionScope;

/**
 * Created by Gao Yuan on 2015/4/24.
 */
public class TallyBookAdminCoreRoot {
    public static final String ADMIN_PROTECTION_SPACE = "AdminProtectionSpace";
    public static final String ADMIN_PROTECTION_TENANT = "admin-tenant";

    public static final ProtectionScope PROTECTION_SCOPE = new ProtectionScope(ADMIN_PROTECTION_SPACE, ADMIN_PROTECTION_TENANT);

    /*
    public static void initEntityTypes(){
    EntityInterfaceManager.instance()
            .registEntity(AdminPermission.class, AdminPermissionImpl.class)
            .registEntity(AdminRole.class, AdminRole.class)
            .registEntity(AdminPermissionQualifiedEntity.class, AdminPermissionQualifiedEntityImpl.class)
            .registEntity(AdminEmployee.class, AdminUserImpl.class)
            .registEntity(UserCertification.class, UserCertificationImpl.class);

    }
    */

    public static String[] getMessageBasenames(){
        return new String[]{
          "classpath:/messages/MenuMessages"
        };
    }
}
