package com.taoswork.tallycheck.adminmvc.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gao Yuan on 2015/6/14.
 */
public class TallyResource {
    private ResourceSupport resourceSupport = new ResourceSupport();
    private Map<String, Object> data;

    @JsonIgnore
    public ResourceSupport getResourceSupport() {
        return resourceSupport;
    }

    @XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
    @JsonProperty("links")
    public List<Link> getLinks() {
        return resourceSupport.getLinks();
    }

    public void put(String key, Object value) {
        addData(key, value);
    }

    public void addData(String key, Object value) {
        if (data == null) {
            data = new HashMap<String, Object>();
        }
        data.put(key, value);
    }

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Map<String, Object> getData() {
        return data;
    }

    public Map<String, Object> getResult() {
        data.put("links", getLinks());
        return data;
    }
}
