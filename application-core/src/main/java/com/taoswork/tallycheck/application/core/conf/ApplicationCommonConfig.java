package com.taoswork.tallycheck.application.core.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.taoswork.tallycheck.datadomain.onmongo.jackson.ObjectIdJsonDeserializer;
import com.taoswork.tallycheck.datadomain.onmongo.jackson.ObjectIdJsonSerializer;
import com.taoswork.tallycheck.general.solution.exception.UnexpectedException;
import com.taoswork.tallycheck.general.solution.message.CachedMessageLocalizedDictionary;
import org.apache.commons.lang3.SerializationUtils;
import org.bson.types.ObjectId;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by Gao Yuan on 2015/9/12.
 */
@Configuration
public class ApplicationCommonConfig {
    public static final String COMMON_MESSAGE = "commonMessage";
    public static final String COMMON_MESSAGE_SOURCE = "commonMessageSource";
    public static final String JSON_OBJECT_MAPPER = "ObjectMapper";

    private HashMap<String, String> rawMessages = new HashMap<String, String>();

    @Bean(name = COMMON_MESSAGE_SOURCE)
    public MessageSource commonMessageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        List<String> basenames = new ArrayList<String>();
        basenames.add("classpath:/messages/CommonMessages");
        ms.setBasenames(basenames.toArray(new String[basenames.size()]));
        ms.setPropertiesPersister(new DefaultPropertiesPersister() {
            @Override
            public void load(Properties props, InputStream is) throws IOException {
                super.load(props, is);
                appendPropertyKeys(props);
            }

            @Override
            public void load(Properties props, Reader reader) throws IOException {
                super.load(props, reader);
                appendPropertyKeys(props);
            }

            @Override
            public void loadFromXml(Properties props, InputStream is) throws IOException {
                super.loadFromXml(props, is);
                appendPropertyKeys(props);
            }

            private void appendPropertyKeys(Properties props) {
                props.forEach(new BiConsumer<Object, Object>() {
                    @Override
                    public void accept(Object o, Object o2) {
                        String key = o.toString();
                        rawMessages.put(key, key);
                    }
                });
            }
        });
        //Ensure load.
        ms.getMessage("yes", null, "yes", Locale.ROOT);
        return ms;
    }

    @Bean(name = COMMON_MESSAGE)
    public CachedMessageLocalizedDictionary commonMessage() {
        //ensure ms created first.
        MessageSource ms = commonMessageSource();
        Map<String, String> raw = SerializationUtils.clone(rawMessages);
        if (raw.size() == 0) {
            throw new UnexpectedException("rawMessages not initialized.");
        }
        CachedMessageLocalizedDictionary commonMessage = new CachedMessageLocalizedDictionary(raw, ms);
        return commonMessage;
    }

    @Bean(name = JSON_OBJECT_MAPPER)
    protected ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ObjectId.class, new ObjectIdJsonSerializer());
        module.addDeserializer(ObjectId.class, new ObjectIdJsonDeserializer());

        om.registerModule(module);
        return om;
    }

}
