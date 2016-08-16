package com.taoswork.tallycheck.adminmvc.controllers.entities;

import com.taoswork.tallycheck.admincore.conf.AdminCoreConfig;
import com.taoswork.tallycheck.admincore.menu.AdminMenuService;
import com.taoswork.tallycheck.admincore.security.AdminSecurityService;
import com.taoswork.tallycheck.admincore.web.model.service.AdminCommonModelService;
import com.taoswork.tallycheck.datadomain.tallyadmin.AdminEmployee;
import com.taoswork.tallycheck.datadomain.tallyuser.Person;
import com.taoswork.tallycheck.datadomain.base.restful.EntityAction;
import com.taoswork.tallycheck.dataservice.IDataService;
import com.taoswork.tallycheck.dataservice.frontend.dataio.FormEntity;
import com.taoswork.tallycheck.dataservice.frontend.io.request.*;
import com.taoswork.tallycheck.dataservice.frontend.io.request.parameter.EntityTypeParameter;
import com.taoswork.tallycheck.dataservice.frontend.io.request.parameter.EntityTypeParameterBuilder;
import com.taoswork.tallycheck.dataservice.frontend.io.request.translator.Parameter2RequestTranslator;
import com.taoswork.tallycheck.dataservice.frontend.io.response.*;
import com.taoswork.tallycheck.dataservice.frontend.service.FrontEndEntityService;
import com.taoswork.tallycheck.dataservice.frontend.service.IFrontEndEntityService;
import com.taoswork.tallycheck.dataservice.manage.DataServiceManager;
import com.taoswork.tallycheck.descriptor.dataio.in.Entity;
import com.taoswork.tallycheck.descriptor.description.infos.EntityInfoType;
import com.taoswork.tallycheck.general.solution.menu.IMenu;
import com.taoswork.tallycheck.info.IEntityInfo;
import org.apache.commons.httpclient.URI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *    Action            Method      Success :                       Error:NoRecord      Error:Validation    Error:KPermission
 * 1. query             get         Grid in SimplePage/FramedPage   N/A                 N/A                 Error In Page
 * 2. createFresh       get         Edit in SimplePage/FramedPage   N/A                 N/A                 Error In Page
 * 3. read              get         Edit in SimplePage/FramedPage   Error in Page       N/A                 Error In Page
 * 4. create            post        Redirect Read Page              N/A                 AJAX: Error         AJAX: Error
 * 5. update            post        Redirect Read Page              AJAX: Error         AJAX: Error         AJAX: Error
 * 6. delete            post        Redirect Read Page              AJAX: Error         N/A                 AJAX: Error
 */
@Controller(AdminBasicEntityController.CONTROLLER_NAME)
@RequestMapping("/{entityTypeName:^[\\w|\\-|\\.]+$}")
public class AdminBasicEntityController extends _AdminBasicEntityControllerBase {
    private static Logger LOGGER = LoggerFactory.getLogger(AdminBasicEntityController.class);
    public static final String CONTROLLER_NAME = "AdminBasicEntityController";

    @Resource(name = AdminMenuService.SERVICE_NAME)
    protected AdminMenuService adminMenuService;

    @Resource(name = AdminCommonModelService.COMPONENT_NAME)
    protected AdminCommonModelService adminCommonModelService;

    @Resource(name = AdminCoreConfig.DATA_SERVICE_MANAGER)
    private DataServiceManager dataServiceManager;

    @Resource(name = AdminSecurityService.COMPONENT_NAME)
    private AdminSecurityService adminSecurityService;

    @Resource(name = AdminCoreConfig.ENTITY_ACCESS_MESSAGE_SOURCE)
    private MessageSource entityAccessMessageSource;

    private Helper helper = new Helper();

    private Set<String> getParamInfoFilter() {
        return EntityInfoType.PageSupportedType;
    }

    @RequestMapping(value = "info", method = RequestMethod.GET)
    public String info(HttpServletRequest request, HttpServletResponse response,
                       Model model,
                       @PathVariable Map<String, String> pathVars,
                       @RequestParam MultiValueMap<String, String> requestParams) {

        String entityTypeName = getEntityTypeName(pathVars);
        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName);
        Class entityCeilingType = entityTypes.getCeilingType();
        if (entityCeilingType == null) {
            return VIEWS.Redirect2Home;
        }

        Locale locale = super.getLocale(request);
        IDataService dataService = dataServiceManager.getDataService(entityCeilingType.getName());
        IFrontEndEntityService frontEndEntityService = newFrontEndEntityService(dataService);

        EntityInfoRequest infoRequest = Parameter2RequestTranslator.makeInfoRequest(entityTypes,
                uriFromRequest(request), requestParams, getParamInfoFilter(), locale);
        infoRequest.addEntityInfoType(EntityInfoType.Grid);

        EntityInfoResponse entityResponse = frontEndEntityService.getInfoResponse(infoRequest, locale);
        return this.makeDataView(model, entityResponse);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    /// CRUDQ                                                                       //////
    //////////////////////////////////////////////////////////////////////////////////////
    /**
     * Renders the main entity listing for the specified class, which is based on the current entityTypeName with some optional
     * criteria
     *
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param requestParams
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String query(HttpServletRequest request, HttpServletResponse response,
                        Model model,
                        @PathVariable Map<String, String> pathVars,
                        @RequestParam MultiValueMap<String, String> requestParams) {
        String entityTypeName = getEntityTypeName(pathVars);
        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName);
        Class entityCeilingType = entityTypes.getCeilingType();
        if (entityCeilingType == null) {
            return VIEWS.Redirect2Home;
        }

        Locale locale = super.getLocale(request);
        IDataService dataService = dataServiceManager.getDataService(entityCeilingType.getName());
        IFrontEndEntityService frontEndEntityService = newFrontEndEntityService(dataService);

        EntityQueryRequest entityRequest = Parameter2RequestTranslator.makeQueryRequest(entityTypes,
            uriFromRequest(request), requestParams, getParamInfoFilter(), locale);
        entityRequest.addEntityInfoType(EntityInfoType.Grid);

        EntityQueryResponse entityQueryResponse = frontEndEntityService.query(entityRequest, locale);
        if (isAjaxDataRequest(request)) {
            return makeDataView(model, entityQueryResponse);
        }

        Person person = adminCommonModelService.getPersistentPerson();
        AdminEmployee employee = adminCommonModelService.getPersistentAdminEmployee();
        IMenu menu = adminMenuService.buildMenu(employee);
        Collection<String> currentMPath = adminMenuService.workoutMenuKeyPathByUrl(entityTypes.getTypeName());
        model.addAttribute("person", person);
        String entityResultInJson = getObjectInJson(entityQueryResponse);
        model.addAttribute("queryResult", entityResultInJson);

        makeDataMapBuilder("dataMap")
                .addAttribute("menu", menu)
                .addAttribute("menuPath", currentMPath)
                .addAttribute("queryResult", entityQueryResponse)
                .addToModule(model);

        {
//            ObjectMapper mapper = new ObjectMapper();
//            DeserializationConfig cfg = mapper.getDeserializationConfig();
//            SimpleModule module = new SimpleModule();
//            module.addSerializer()
//            mapper.registerModule()
        }

        model.addAttribute("viewType", "entityMainGrid");
        setCommonModelAttributes(model, locale);

        boolean success = entityQueryResponse.success();
        if (!success) {
            if (!entityQueryResponse.getErrors().isAuthorized()) {
                model.addAttribute("viewType", "noPermission");
            } else {
                setErrorModelAttributes(model, entityQueryResponse);
                model.addAttribute("viewType", "uncheckedError");
            }
        }
        if (isSimpleViewRequest(request)) {
            return VIEWS.SimpleView;
        } else {
            model.addAttribute("scope", "main");
            return VIEWS.FramedView;
        }
    }


    @RequestMapping(value = "select", method = RequestMethod.GET)
    public String select(HttpServletRequest request, HttpServletResponse response,
                        Model model,
                        @PathVariable Map<String, String> pathVars,
                        @RequestParam MultiValueMap<String, String> requestParams)throws Exception{
        String entityTypeName = getEntityTypeName(pathVars);
        String oldUri = request.getRequestURI();
        URI uri = new URI(request.getRequestURI(), false);
        uri.setPath(entityTypeName);
        return "forward:/" + uri.toString();
    }
    /**
     * Renders the modal form that is used to add a new parent level entity. Note that this form cannot render any
     * subcollections as operations on those collections require the parent level entity to first be saved and have
     * and id. Once the entity is initially saved, we will redirect the user to the normal manage entity screen where
     * they can then perform operations on sub collections.
     *
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @return the return view path
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String createFresh(HttpServletRequest request, HttpServletResponse response,
                              Model model,
                              @PathVariable Map<String, String> pathVars) {
        String entityTypeName = getEntityTypeName(pathVars);
        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName);
        Class entityCeilingType = entityTypes.getCeilingType();
        if (entityCeilingType == null) {
            return VIEWS.Redirect2Home;
        }

        Locale locale = super.getLocale(request);
        IDataService dataService = dataServiceManager.getDataService(entityCeilingType.getName());
        IFrontEndEntityService frontEndEntityService = newFrontEndEntityService(dataService);

        EntityCreateFreshRequest addRequest = Parameter2RequestTranslator.makeCreateFreshRequest(entityTypes,
            uriFromRequest(request), locale);

        EntityCreateFreshResponse addResponse = frontEndEntityService.createFresh(addRequest, locale);
        if (isAjaxDataRequest(request)) {
            return makeDataView(model, addResponse);
        }

        model.addAttribute("currentAction", EntityAction.CREATE.getType());
        model.addAttribute("formAction", request.getRequestURL().toString());

        Person person = adminCommonModelService.getPersistentPerson();
        AdminEmployee employee = adminCommonModelService.getPersistentAdminEmployee();
        IMenu menu = adminMenuService.buildMenu(employee);
        Collection<String> currentMPath = adminMenuService.workoutMenuKeyPathByUrl(entityTypes.getTypeName());
        model.addAttribute("person", person);

        makeDataMapBuilder("dataMap")
                .addAttribute("menu", menu)
                .addAttribute("menuPath", currentMPath)
                .addAttribute("formResult", addResponse)
                .addToModule(model);

        model.addAttribute("formInfo", addResponse.getInfos().getForm());
        String entityResultInJson = getObjectInJson(addResponse);
        model.addAttribute("formResult", entityResultInJson);

        model.addAttribute("viewType", "entityMainView");
        setCommonModelAttributes(model, locale);

        boolean success = addResponse.success();
        if (!success) {
            if (!addResponse.getErrors().isAuthorized()) {
                model.addAttribute("viewType", "noPermission");
            } else {
                setErrorModelAttributes(model, addResponse);
                model.addAttribute("viewType", "uncheckedError");
            }
        }
        if (isSimpleViewRequest(request)) {
            return VIEWS.SimpleView;
        } else {
            model.addAttribute("formScope", "main");
            return VIEWS.FramedView;
        }
    }

    /**
     * Processes the request to add a new entity. If successful, returns a redirect to the newly created entity.
     *
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param entityForm
     * @param result
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String create(HttpServletRequest request, HttpServletResponse response,
                         Model model,
                         @PathVariable Map<String, String> pathVars,
                         @ModelAttribute(value = "entityForm") FormEntity entityForm, BindingResult result) {
        String entityTypeName = getEntityTypeName(pathVars);
        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName, entityForm);
        Class entityType = entityTypes.getType();
        Class entityCeilingType = entityTypes.getCeilingType();
        if (entityCeilingType == null) {
            return VIEWS.Redirect2Home;
        }
        if (entityType == null) {
            return VIEWS.Redirect2Failure;
        }

        if (!(isAjaxRequest(request))) {
            return VIEWS.Redirect2Failure;
        }

        Locale locale = super.getLocale(request);
        IDataService dataService = dataServiceManager.getDataService(entityType.getName());
        IFrontEndEntityService frontEndEntityService = newFrontEndEntityService(dataService);

        EntityCreateRequest createRequest = Parameter2RequestTranslator.makeCreateRequest(entityTypes,
            uriFromRequest(request), entityForm, locale);
        EntityCreateResponse createResponse = frontEndEntityService.create(createRequest, locale);

        boolean success = createResponse.success();
        if (!success) {
            return this.makeDataView(model, createResponse);
        } else {
            String resultUrl = request.getContextPath() + "/" + entityTypeName + "/" + createResponse.getEntity().getIdValue();
            return makeRedirectView(model, resultUrl, success);
        }
    }

    /**
     * Renders the main entity form for the specified entity
     *
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String read(HttpServletRequest request, HttpServletResponse response,
                       Model model,
                       @PathVariable Map<String, String> pathVars,
                       @PathVariable("id") String id) {
        String entityTypeName = getEntityTypeName(pathVars);
        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName);
        Class entityCeilingType = entityTypes.getCeilingType();
        if (entityCeilingType == null) {
            return VIEWS.Redirect2Home;
        }

        Locale locale = super.getLocale(request);
        IDataService dataService = dataServiceManager.getDataService(entityCeilingType.getName());
        IFrontEndEntityService frontEndEntityService = newFrontEndEntityService(dataService);

        EntityReadRequest readRequest = Parameter2RequestTranslator.makeReadRequest(entityTypes,
            uriFromRequest(request), id, locale);

        EntityReadResponse readResponse = frontEndEntityService.read(readRequest, locale);
        if (isAjaxDataRequest(request)) {
            return makeDataView(model, readResponse);
        }

        model.addAttribute("currentAction", EntityAction.READ.getType());
        model.addAttribute("formAction", request.getRequestURL().toString());

        Person person = adminCommonModelService.getPersistentPerson();
        AdminEmployee employee = adminCommonModelService.getPersistentAdminEmployee();
        IMenu menu = adminMenuService.buildMenu(employee);
        Collection<String> currentMPath = adminMenuService.workoutMenuKeyPathByUrl(entityTypes.getTypeName());
        String entityName = "";
        if (readResponse.getEntity() != null) {
            entityName = readResponse.getEntity().getName();
        }
        model.addAttribute("person", person);

        makeDataMapBuilder("dataMap")
                .addAttribute("menu", menu)
                .addAttribute("menuPath", currentMPath)
                .addAttribute("formResult", readResponse)
                .addAttribute("entityName", entityName, StringUtils.isNotEmpty(entityName))
                .addToModule(model);;

        IEntityInfo formInfo = null;
        if (readResponse.getInfos() != null) {
            formInfo = readResponse.getInfos().getForm();
        }
        model.addAttribute("formInfo", formInfo);
        String entityResultInJson = getObjectInJson(readResponse);
        model.addAttribute("readResult", entityResultInJson);

        model.addAttribute("viewType", "entityMainView");
        setCommonModelAttributes(model, locale);

        boolean success = readResponse.success();
        if (!success) {
            if (!readResponse.getErrors().isAuthorized()) {
                model.addAttribute("viewType", "noPermission");
            } else if (!readResponse.gotRecord()) {
                model.addAttribute("viewType", "noSuchRecord");
                model.addAttribute("id", id);
            } else {
                setErrorModelAttributes(model, readResponse);
                model.addAttribute("viewType", "uncheckedError");
            }
        }
        if (isSimpleViewRequest(request)) {
            return VIEWS.SimpleView;
        } else {
            model.addAttribute("formScope", "main");
            return VIEWS.FramedView;
        }
    }

    /**
     * Attempts to save the given entity. If validation is unsuccessful, it will re-render the entity form with
     * error fields highlighted. On a successful save, it will refresh the entity page.
     *
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @param entityForm
     * @param result
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String update(HttpServletRequest request, HttpServletResponse response,
                         Model model,
                         @PathVariable Map<String, String> pathVars,
                         @PathVariable(value = "id") String id,
                         @ModelAttribute(value = "entityForm") FormEntity entityForm,
                         BindingResult result,
                         RedirectAttributes ra) {
        String entityTypeName = getEntityTypeName(pathVars);
        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName, entityForm);
        Class entityType = entityTypes.getType();
        Class entityCeilingType = entityTypes.getCeilingType();
        if (entityCeilingType == null) {
            return VIEWS.Redirect2Home;
        }
        if (entityType == null) {
            return VIEWS.Redirect2Failure;
        }

        if (!(isAjaxRequest(request))) {
            return VIEWS.Redirect2Failure;
        }

        Locale locale = super.getLocale(request);
        IDataService dataService = dataServiceManager.getDataService(entityType.getName());
        IFrontEndEntityService frontEndEntityService = newFrontEndEntityService(dataService);

        EntityUpdateRequest updateRequest = Parameter2RequestTranslator.makeUpdateRequest(entityTypes,
            uriFromRequest(request), entityForm, locale);
        EntityUpdateResponse updateResponse = frontEndEntityService.update(updateRequest, locale);

        boolean success = updateResponse.success();
        if (!success) {
            return this.makeDataView(model, updateResponse);
        } else {
            return makeRedirectView(model, request.getRequestURI(), success);
        }
    }

    /**
     * Attempts to delete the given entity.
     *
     * @param request
     * @param response
     * @param model
     * @param pathVars
     * @param id
     * @return the return view path
     * @throws Exception
     */
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public String delete(HttpServletRequest request, HttpServletResponse response,
                         Model model,
                         @PathVariable Map<String, String> pathVars,
                         @PathVariable(value = "id") String id,
                         @ModelAttribute(value = "entityForm") FormEntity entityForm, BindingResult result) {
        String entityTypeName = getEntityTypeName(pathVars);
        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName, entityForm);
        Class entityType = entityTypes.getType();
        Class entityCeilingType = entityTypes.getCeilingType();
        if (entityCeilingType == null) {
            return VIEWS.Redirect2Home;
        }
        if (entityType == null) {
            return VIEWS.Redirect2Failure;
        }

        if (!(isAjaxRequest(request))) {
            return VIEWS.Redirect2Failure;
        }
        Locale locale = super.getLocale(request);
        IDataService dataService = dataServiceManager.getDataService(entityType.getName());
        IFrontEndEntityService frontEndEntityService = newFrontEndEntityService(dataService);

        EntityDeleteRequest deleteRequest = Parameter2RequestTranslator.makeDeleteRequest(entityTypes,
            uriFromRequest(request), id, entityForm, locale);

        EntityDeleteResponse deleteResponse = frontEndEntityService.delete(deleteRequest, locale);
        boolean success = deleteResponse.success();
        if (!success) {
            return this.makeDataView(model, deleteResponse);
        } else {
            String resultUrl = request.getContextPath() + "/" + entityTypeName;
            return makeRedirectView(model, resultUrl, success);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    /// Methods by Collection Field                                                 //////
    //////////////////////////////////////////////////////////////////////////////////////

//    @RequestMapping(value = "/{collectionField:.*}/add", method = RequestMethod.GET)
//    public String collectionCreateFresh(HttpServletRequest request, HttpServletResponse response,
//                                        Model model,
//                                        @PathVariable Map<String, String> pathVars,
//                                        @PathVariable(value = "collectionField") String collectionField){
//        String entityTypeName = getEntityTypeName(pathVars);
//        EntityTypeParameter holderEntityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName);
//        Class entityType = holderEntityTypes.getType();
//        Class entityCeilingType = holderEntityTypes.getCeilingType();
//        if (entityCeilingType == null) {
//            return VIEWS.Redirect2Home;
//        }
//        if (entityType == null) {
//            return VIEWS.Redirect2Failure;
//        }
//        Locale locale = super.getLocale(request);
//        IDataService dataService = dataServiceManager.getDataService(entityType.getName());
//        final JpaEntityService dynamicEntityService = dataService.getService(JpaEntityService.COMPONENT_NAME);
//        IClassMeta cm = dynamicEntityService.inspectMeta(entityType, true);
//        IFieldMeta fm = cm.getFieldMeta(collectionField);
//        if(!(fm instanceof BaseCollectionFieldMeta)){
//           return null;
//        }
//        BaseCollectionFieldMeta cfm = (BaseCollectionFieldMeta)fm;
//        IFrontEndEntityService frontEndEntityService = FrontEndEntityService.newInstance(dataServiceManager, dataService, adminSecurityService);
//
//        EntityTypeParameter entityTypes = new EntityTypeParameter();
//        entityTypes.setTypeName(cfm.getName())
//            .setCeilingType(cfm.getPresentationCeilingClass())
//            .setType(cfm.getPresentationClass());
//        CollectionEntryCreateFreshRequest addRequest = Parameter2RequestTranslator.makeCollectionCreateFreshRequest(entityTypes, collectionField,
//            uriFromRequest(request));
//
//        CollectionEntryCreateFreshResponse addResponse = frontEndEntityService.collectionEntryCreateFresh(addRequest, locale);
//        if (isAjaxDataRequest(request)) {
//            return makeDataView(model, addResponse);
//        }
//
//        model.addAttribute("currentAction", EntityAction.CREATE.getType());
//        model.addAttribute("formAction", request.getRequestURL().toString());
//
//        Person person = adminCommonModelService.getPersistentPerson();
//        AdminEmployee employee = adminCommonModelService.getPersistentAdminEmployee();
//        IMenu menu = adminMenuService.buildMenu(employee);
//
//        model.addAttribute("menu", menu);
//        model.addAttribute("person", person);
//
//        model.addAttribute("formInfo", addResponse.getInfos().getDetail(EntityInfoType.Form));
//        String entityResultInJson = getObjectInJson(addResponse);
//        model.addAttribute("addData", entityResultInJson);
//
//        model.addAttribute("viewType", "entityAdd");
//        setCommonModelAttributes(model, locale);
//
//        boolean success = addResponse.success();
//        if (!success) {
//            if (!addResponse.getErrors().isAuthorized()) {
//                model.addAttribute("viewType", "noPermission");
//            } else {
//                setErrorModelAttributes(model, addResponse);
//                model.addAttribute("viewType", "uncheckedError");
//            }
//        }
//        if (isSimpleViewRequest(request)) {
//            return VIEWS.SimpleView;
//        } else {
//            model.addAttribute("formScope", "main");
//            return VIEWS.FramedView;
//        }
//    }

    @RequestMapping(value = "/{id}/{collectionField:.*}", method = RequestMethod.GET)
    public String collectionQuery(HttpServletRequest request, HttpServletResponse response,
                                  Model model,
                                  @PathVariable Map<String, String> pathVars,
                                  @PathVariable(value = "id") String id,
                                  @PathVariable(value = "collectionField") String collectionField,
                                  @RequestParam MultiValueMap<String, String> requestParams){
        return null;
    }

    @RequestMapping(value = "/{id}/{collectionField:.*}/add", method = RequestMethod.GET)
    public String collectionCreateFresh(HttpServletRequest request, HttpServletResponse response,
                                        Model model,
                                        @PathVariable Map<String, String> pathVars,
                                        @PathVariable(value = "id") String id,
                                        @PathVariable(value = "collectionField") String collectionField){
        return  null;
//        String entityTypeName = getEntityTypeName(pathVars);
//        EntityTypeParameter entityTypes = EntityTypeParameterBuilder.getBy(dataServiceManager, entityTypeName);
//        Class entityType = entityTypes.getType();
//        Class entityCeilingType = entityTypes.getCeilingType();
//        if (entityCeilingType == null) {
//            return VIEWS.Redirect2Home;
//        }
//        if (entityType == null) {
//            return VIEWS.Redirect2Failure;
//        }
//
//        if (!(isAjaxRequest(request))) {
//            return VIEWS.Redirect2Failure;
//        }
//
//        Locale locale = super.getLocale(request);
//        IDataService dataService = dataServiceManager.getDataService(entityType.getName());
//        final IEntityService entityService = dataService.getService(IEntityService.COMPONENT_NAME);
//        IClassMeta cm = entityService.inspectMeta(entityType, true);
//        IFieldMeta fm = cm.getFieldMeta(collectionField);
//        if(!(fm instanceof BaseCollectionFieldMeta)){
//            return null;
//        }
//
//        BaseCollectionFieldMeta cfm = (BaseCollectionFieldMeta)fm;
//        IFrontEndEntityService frontEndEntityService = FrontEndEntityService.newInstance(dataServiceManager, dataService, adminSecurityService);
//
//        CollectionEntryTypeParameter collectionEntryTypeParameter = new CollectionEntryTypeParameter(entityType,
//            cfm.getName(), cfm.getPresentationCeilingClass(), cfm.getPresentationClass());
////        EntityTypeParameter entryTypes = new EntityTypeParameter();
////        entryTypes.setTypeName(cfm.getName())
////            .setCeilingType(cfm.getPresentationCeilingClass())
////            .setType(cfm.getPresentationClass());
//        CollectionEntryCreateFreshRequest addRequest = Parameter2RequestTranslator.makeCollectionCreateFreshRequest(entityTypes,
//            uriFromRequest(request), collectionEntryTypeParameter);
//
//        CollectionEntryCreateFreshResponse addResponse = frontEndEntityService.collectionEntryCreateFresh(addRequest, locale);
//        if (isAjaxDataRequest(request)) {
//            return makeDataView(model, addResponse);
//        }
//
//        model.addAttribute("currentAction", EntityAction.CREATE.getType());
//        model.addAttribute("formAction", request.getRequestURL().toString());
//
//        Person person = adminCommonModelService.getPersistentPerson();
//        AdminEmployee employee = adminCommonModelService.getPersistentAdminEmployee();
//        IMenu menu = adminMenuService.buildMenu(employee);
//
//        model.addAttribute("menu", menu);
//        model.addAttribute("person", person);
//
//        model.addAttribute("formInfo", addResponse.getInfos().getDetail(EntityInfoType.Form));
//        String entityResultInJson = getObjectInJson(addResponse);
//        model.addAttribute("addData", entityResultInJson);
//
//        model.addAttribute("viewType", "entityAdd");
//        setCommonModelAttributes(model, locale);
//
//        boolean success = addResponse.success();
//        if (!success) {
//            if (!addResponse.getErrors().isAuthorized()) {
//                model.addAttribute("viewType", "noPermission");
//            } else {
//                setErrorModelAttributes(model, addResponse);
//                model.addAttribute("viewType", "uncheckedError");
//            }
//        }
//        if (isSimpleViewRequest(request)) {
//            return VIEWS.SimpleView;
//        } else {
//            model.addAttribute("formScope", "main");
//            return VIEWS.FramedView;
//        }
    }

    @RequestMapping(value = "/{id}/{collectionField:.*}/add", method = RequestMethod.POST)
    public String collectionCreate(HttpServletRequest request, HttpServletResponse response,
                                   Model model,
                                   @PathVariable Map<String, String> pathVars,
                                   @PathVariable(value = "id") String id,
                                   @PathVariable(value = "collectionField") String collectionField,
                                   @ModelAttribute(value = "entityForm") Entity entityForm, BindingResult result){
        return null;
    }

    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}", method = RequestMethod.GET)
    public String collectionRead(HttpServletRequest request, HttpServletResponse response,
                       Model model,
                       @PathVariable Map<String, String> pathVars,
                       @PathVariable("id") String id,
                       @PathVariable(value = "collectionField") String collectionField,
                       @PathVariable(value = "collectionItemId") String collectionItemId){
        return null;
    }

    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}", method = RequestMethod.POST)
    public String collectionUpdate(HttpServletRequest request, HttpServletResponse response,
                                   Model model,
                                   @PathVariable Map<String, String> pathVars,
                                   @PathVariable(value = "id") String id,
                                   @PathVariable(value = "collectionField") String collectionField,
                                   @PathVariable(value = "collectionItemId") String collectionItemId,
                                   @ModelAttribute(value = "entityForm") Entity entityForm,
                                   BindingResult result,
                                   RedirectAttributes ra) {
        return null;
    }
    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/reorder", method = RequestMethod.POST)
    public String collectionReorder(HttpServletRequest request, HttpServletResponse response,
                                    Model model,
                                    @PathVariable Map<String, String> pathVars,
                                    @PathVariable(value = "id") String id,
                                    @PathVariable(value = "collectionField") String collectionField,
                                    @PathVariable(value = "collectionItemId") String collectionItemId,
                                    @RequestParam(value="newOrder") String newOrder){
        return null;
    }

    @RequestMapping(value = "/{id}/{collectionField:.*}/{collectionItemId}/delete", method = RequestMethod.POST)
    public String collectionDelete(HttpServletRequest request, HttpServletResponse response,
                                   Model model,
                                   @PathVariable Map<String, String> pathVars,
                                   @PathVariable(value = "id") String id,
                                   @PathVariable(value = "collectionField") String collectionField,
                                   @PathVariable(value = "collectionItemId") String collectionItemId,
                                   @ModelAttribute(value = "entityForm") Entity entityForm, BindingResult result){
        return null;
    }


    //////////////////////////////////////////////////////////////////////////////////////
    ///         Helper                                                              //////
    //////////////////////////////////////////////////////////////////////////////////////
    class Helper {
    }


    private IFrontEndEntityService newFrontEndEntityService(IDataService dataService) {
        return FrontEndEntityService.newInstance(dataServiceManager, dataService, adminSecurityService, entityAccessMessageSource);
    }
}
