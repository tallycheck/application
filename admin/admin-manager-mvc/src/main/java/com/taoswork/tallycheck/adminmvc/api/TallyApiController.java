package com.taoswork.tallycheck.adminmvc.api;

import com.taoswork.tallycheck.admincore.conf.AdminCoreConfig;
import com.taoswork.tallycheck.admincore.security.AdminSecurityService;
import com.taoswork.tallycheck.dataservice.IDataService;
import com.taoswork.tallycheck.dataservice.frontend.io.request.EntityQueryRequest;
import com.taoswork.tallycheck.dataservice.frontend.io.request.EntityReadRequest;
import com.taoswork.tallycheck.dataservice.frontend.io.request.parameter.EntityTypeParameter;
import com.taoswork.tallycheck.dataservice.frontend.io.request.parameter.EntityTypeParameterBuilder;
import com.taoswork.tallycheck.dataservice.frontend.io.request.translator.Parameter2RequestTranslator;
import com.taoswork.tallycheck.dataservice.frontend.io.response.EntityQueryResponse;
import com.taoswork.tallycheck.dataservice.frontend.io.response.EntityReadResponse;
import com.taoswork.tallycheck.dataservice.frontend.service.FrontEndEntityService;
import com.taoswork.tallycheck.dataservice.frontend.service.IFrontEndEntityService;
import com.taoswork.tallycheck.dataservice.manage.DataServiceManager;
import com.taoswork.tallycheck.descriptor.description.infos.EntityInfoType;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Gao Yuan on 2015/6/13.
 */
//@Controller(TallyApiController.TALLY_API_CONTROLLER_NAME)
//@RequestMapping("/api/{entityTypeName:^[\\w|\\-|\\.]+$}")
public class TallyApiController {
    public static final String TALLY_API_CONTROLLER_NAME = "TallyApiController";

    @Resource(name = AdminCoreConfig.DATA_SERVICE_MANAGER)
    private DataServiceManager dataServiceManager;

    @Resource(name = AdminCoreConfig.ENTITY_ACCESS_MESSAGE_SOURCE)
    private MessageSource entityAccessMessageSource;

    @Resource(name = AdminSecurityService.COMPONENT_NAME)
    private AdminSecurityService adminSecurityService;

    private Set<String> getParamInfoFilter() {
        return EntityInfoType.ApiSupportedType;
    }

    @RequestMapping("")
    @ResponseBody
    public HttpEntity<?> getEntityList(HttpServletRequest request,
                                       @PathVariable(value = "entityTypeName") String entityTypeName,
                                       @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName);
        Class entityType = entityTypes.getCeilingType();

        Locale locale = request.getLocale();
        EntityQueryRequest queryRequest = Parameter2RequestTranslator.makeQueryRequest(
                entityTypes,
                uriFromRequest(request),
                requestParams, getParamInfoFilter(), locale);

        IDataService dataService = dataServiceManager.getDataService(entityType.getName());
        IFrontEndEntityService frontEndEntityService = FrontEndEntityService.newInstance(dataServiceManager, dataService,adminSecurityService, entityAccessMessageSource);

        EntityQueryResponse response = frontEndEntityService.query(queryRequest, request.getLocale());
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<?> readEntity(HttpServletRequest request, Model model,
                                    @PathVariable(value = "entityTypeName") String entityTypeName,
                                    @PathVariable("id") String id,
                                    @PathVariable Map<String, String> pathVars) throws Exception {

        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName);
        Class entityType = entityTypes.getCeilingType();

        Locale locale = request.getLocale();
        EntityReadRequest readRequest = Parameter2RequestTranslator.makeReadRequest(entityTypes,
                uriFromRequest(request), id, locale);

        IDataService dataService = dataServiceManager.getDataService(entityType.getName());
        IFrontEndEntityService frontEndEntityService = FrontEndEntityService.newInstance(dataServiceManager, dataService,adminSecurityService, entityAccessMessageSource);

        EntityReadResponse response = frontEndEntityService.read(readRequest, request.getLocale());
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    /*   @RequestMapping("/greeting")
       @ResponseBody
       public HttpEntity<Greeting> greeting(
               @RequestParam(value = "name", required = false, defaultValue = "World") String name) {

           Greeting greeting = new Greeting(String.format(TEMPLATE, name));
           greeting.add(linkTo(methodOn(GreetingController.class).greeting(name)).withSelfRel());

           return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
       }
   */
    protected static URI uriFromRequest(HttpServletRequest request) {
        try {
            URI uriobj = new URI(null, null, request.getRequestURI(), request.getQueryString(), null);
            return uriobj;
        } catch (URISyntaxException e) {
            String query = request.getQueryString();
            String uri = request.getRequestURI();
            if (StringUtils.isEmpty(query)) {
                return URI.create(uri);
            } else {
                return URI.create(uri + "?" + query);
            }
        }
    }
}
