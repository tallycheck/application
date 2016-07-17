package com.taoswork.tallycheck.admincore;

import com.taoswork.tallycheck.authority.core.ProtectionScope;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminProtection;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminProtectionSpec;

/**
 * Created by Gao Yuan on 2015/4/24.
 */
public class TallyBookAdminCoreRoot {
    public static final String ADMIN_PROTECTION_SPEC = AdminProtectionSpec.COMMON_SPEC_NAME;
    public static final String ADMIN_PROTECTION_REGION = AdminProtection.COMMON_REGION_NAME;

    public static final ProtectionScope PROTECTION_SCOPE =
            new ProtectionScope(ADMIN_PROTECTION_SPEC, ADMIN_PROTECTION_REGION);

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
