package com.taoswork.tallycheck.adminmvc.controllers.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taoswork.tallycheck.adminmvc.view.JsonViewResolver;
import com.taoswork.tallycheck.application.core.conf.ApplicationCommonConfig;
import com.taoswork.tallycheck.dataservice.frontend.io.response.EntityResponse;
import com.taoswork.tallycheck.dataservice.frontend.io.response.result.EntityErrors;
import com.taoswork.tallycheck.general.solution.message.CachedMessageLocalizedDictionary;
import com.taoswork.tallycheck.general.solution.property.RuntimePropertiesPublisher;
import com.taoswork.tallycheck.general.web.control.BaseController;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Gao Yuan on 2015/11/25.
 */
abstract class _AdminBasicEntityControllerBase extends BaseController {

    protected static final String DataView = JsonViewResolver.JSON_VIEW_NAME;

    protected static class VIEWS {
        static final String Redirect2Home = "redirect:/";
        static final String Redirect2Failure = "redirect:failure";
        static final String FramedView = "entity/content/framedView";
        static final String SimpleView = "entity/content/simpleView";
    }

    @Resource(name = ApplicationCommonConfig.COMMON_MESSAGE)
    private CachedMessageLocalizedDictionary commonMessage;

    @Resource(name=ApplicationCommonConfig.JSON_OBJECT_MAPPER)
    private ObjectMapper objectMapper;

    private Map<String, String> getCommonMessage(Locale locale) {
        return commonMessage.getTranslated(locale);
    }

    protected void setCommonModelAttributes(Model model, Locale locale) {
        boolean production = RuntimePropertiesPublisher.instance().getBoolean("tally.production", false);
        Map<String, String> messageMap = getCommonMessage(locale);
        String messageDict = getObjectInJson(messageMap);

        model.addAttribute("messageDict", messageDict);
        model.addAttribute("production", production);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    ///         Helper                                                              //////
    //////////////////////////////////////////////////////////////////////////////////////
    protected String getEntityTypeName(Map<String, String> pathVars) {
        return super.getPathVariable(pathVars, "entityTypeName");
    }


    protected void setErrorModelAttributes(Model model, EntityResponse entityResponse) {
        EntityErrors errors = entityResponse.getErrors();
        if (errors != null && errors.containsError()) {
            model.addAttribute("errors", errors.getGlobal());
        }
    }

    protected String makeDataView(Model model, EntityResponse data) {
        model.addAttribute("data", data);
        model.addAttribute("success", data.success());
        return DataView;
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected String makeRedirectView(Model model, String url, boolean success) {
        model.addAttribute("operation", "redirect");
        model.addAttribute("url", url);
        model.addAttribute("success", success);

        return DataView;
    }

}
