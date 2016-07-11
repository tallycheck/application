package com.taoswork.tallycheck.admincore.conf;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taoswork.tallycheck.admincore.TallyBookAdminCoreRoot;
import com.taoswork.tallycheck.admincore.security.detail.AdminEmployeeDetailsService;
import com.taoswork.tallycheck.admincore.security.detail.impl.AdminEmployeeDetailsServiceImpl;
import com.taoswork.tallycheck.application.core.conf.ApplicationCommonConfig;
import com.taoswork.tallycheck.authentication.UserAuthenticationService;
import com.taoswork.tallycheck.authentication.UserAuthenticationServiceMock;
import com.taoswork.tallycheck.dataservice.manage.DataServiceManager;
import com.taoswork.tallycheck.dataservice.manage.impl.DataServiceManagerImpl;
import com.taoswork.tallycheck.datasolution.annotations.DaoMark;
import com.taoswork.tallycheck.datasolution.annotations.EntityServiceMark;
import com.taoswork.tallycheck.general.extension.annotations.FrameworkService;
import com.taoswork.tallycheck.general.extension.collections.PropertiesUtility;
import com.taoswork.tallycheck.general.solution.spring.BeanCreationMonitor;
import com.taoswork.tallycheck.tallyadmin.TallyAdminDataService;
import com.taoswork.tallycheck.tallyadmin.TallyAdminDataServiceMock;
import com.taoswork.tallycheck.tallyadmin.authority.AdminAuthorityProvider;
import com.taoswork.tallycheck.tallyadmin.authority.AdminAuthorityProviderMock;
import com.taoswork.tallycheck.tallybiz.TallyBizDataService;
import com.taoswork.tallycheck.tallybiz.TallyBizDataServiceMock;
import com.taoswork.tallycheck.tallybus.TallyBusDataService;
import com.taoswork.tallycheck.tallybus.TallyBusDataServiceMock;
import com.taoswork.tallycheck.tallyuser.TallyUserDataService;
import com.taoswork.tallycheck.tallyuser.TallyUserDataServiceMock;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;
import java.util.Properties;

/**
 * Created by Gao Yuan on 2015/4/24.
 */
@Configuration
@Import({ApplicationCommonConfig.class})
@ComponentScan(
        basePackageClasses = TallyBookAdminCoreRoot.class,
        //   useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter({
                        DaoMark.class,
                        EntityServiceMark.class,
                        FrameworkService.class
                })},
        excludeFilters = {@ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                value = {Configuration.class}
        )}
)
public class AdminCoreConfig {
    public static final String DATA_SERVICE_MANAGER = "AdminDataServiceManager";

    public static final String USER_DATA_SERVICE = "tallyuser-data-service";
    public static final String ADMIN_DATA_SERVICE = "tallyadmin-data-service";
    public static final String BIZ_DATA_SERVICE = "tallybiz-data-service";
    public static final String MANAGEMENT_DATA_SERVICE = "tallybus-data-service";
    public static final String USER_CERT_DATA_SERVICE = "user-cert-data-service";
    public static final String ADMIN_AUTHORITY_DATA_SERVICE = "admin-authority-data-service";

    @Bean
    public ApplicationConfig applicationConfig(){
        Properties properties = ConfigUtils.getProperties();
        ApplicationConfig ac = new ApplicationConfig();
        ac.setName(properties.getProperty("dubbo.application.name"));
        return ac;
    }

    @Bean
    public RegistryConfig registryConfig(){
        Properties properties = ConfigUtils.getProperties();
        Map<String, String> params = PropertiesUtility.propertiesToMap(properties);
        RegistryConfig registry = new RegistryConfig();
        registry.setParameters(params);
        registry.setAddress(properties.getProperty("dubbo.registry.address"));
        return registry;
    }

    private <T> T dubboService(Class<T> serviceType, Class<? extends T> mockType){
        ReferenceConfig<T> reference = new ReferenceConfig<T>();
        reference.setApplication(applicationConfig());
        reference.setRegistry(registryConfig());
        reference.setInterface(serviceType);
        if(mockType != null){
            reference.setMock(mockType.getName());
        }

        T dataService = reference.get();
        return dataService;
    }

    @Bean(name = USER_DATA_SERVICE)
    protected TallyUserDataService tallyUserDataService() {
        return dubboService(TallyUserDataService.class, TallyUserDataServiceMock.class);
    }

    @Bean(name = ADMIN_DATA_SERVICE)
    protected  TallyAdminDataService tallyAdminDataService() {
        return dubboService(TallyAdminDataService.class, TallyAdminDataServiceMock.class);
    }

    @Bean(name = BIZ_DATA_SERVICE)
    protected  TallyBizDataService tallyBusinessDataService() {
        return dubboService(TallyBizDataService.class, TallyBizDataServiceMock.class);
    }

    @Bean(name = MANAGEMENT_DATA_SERVICE)
    protected TallyBusDataService tallyManagementDataService() {
        return dubboService(TallyBusDataService.class, TallyBusDataServiceMock.class);
    }

    @Bean(name = USER_CERT_DATA_SERVICE)
    protected UserAuthenticationService userCertificationService() {
        return dubboService(UserAuthenticationService.class, UserAuthenticationServiceMock.class);
    }

    @Bean(name = ADMIN_AUTHORITY_DATA_SERVICE)
    protected AdminAuthorityProvider adminAuthorityProvider() {
        return dubboService(AdminAuthorityProvider.class, AdminAuthorityProviderMock.class);
    }

    @Bean(name = AdminEmployeeDetailsService.COMPONENT_NAME)
    public UserDetailsService adminEmployeeDetailsService() {
        AdminEmployeeDetailsServiceImpl adminEmployeeDetailsService = new AdminEmployeeDetailsServiceImpl();
        adminEmployeeDetailsService.setUserAuthenticationService(userCertificationService());
        adminEmployeeDetailsService.setTallyUserDataService(tallyUserDataService());
        adminEmployeeDetailsService.setTallyAdminDataService(tallyAdminDataService());
        return adminEmployeeDetailsService;
    }

    @Bean(name = DATA_SERVICE_MANAGER)
    public DataServiceManager dataServiceManager() {
        DataServiceManagerImpl dataServiceManager = new DataServiceManagerImpl();

        dataServiceManager
                .buildingAppendDataService(tallyUserDataService())
                .buildingAppendDataService(tallyAdminDataService())
                .buildingAppendDataService(tallyBusinessDataService())
                .buildingAppendDataService(tallyManagementDataService())

                .buildingAnnounceFinishing();

        return dataServiceManager;
    }

    @Bean
    public BeanCreationMonitor beanCreationMonitor(){
        return new BeanCreationMonitor("");
    }
}
