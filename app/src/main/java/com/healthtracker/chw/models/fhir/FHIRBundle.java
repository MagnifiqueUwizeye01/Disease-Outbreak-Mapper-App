package com.healthtracker.chw.models.fhir;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * FHIR Bundle Resource
 * Used for batch operations or responses containing multiple resources
 * https://www.hl7.org/fhir/bundle.html
 */
public class FHIRBundle {
    @SerializedName("resourceType")
    private String resourceType = "Bundle";
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("type")
    private String type; // document | message | transaction | transaction-response | batch | batch-response | history | searchset | collection
    
    @SerializedName("entry")
    private List<Entry> entry;

    public FHIRBundle() {
        this.entry = new ArrayList<>();
    }

    // Getters and Setters
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Entry> getEntry() {
        return entry;
    }

    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }

    // Nested class
    public static class Entry {
        @SerializedName("resource")
        private Object resource; // Can be any FHIR resource
        
        @SerializedName("fullUrl")
        private String fullUrl;
        
        @SerializedName("request")
        private Request request;

        public Object getResource() {
            return resource;
        }

        public void setResource(Object resource) {
            this.resource = resource;
        }

        public String getFullUrl() {
            return fullUrl;
        }

        public void setFullUrl(String fullUrl) {
            this.fullUrl = fullUrl;
        }

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }
    }

    public static class Request {
        @SerializedName("method")
        private String method; // GET | POST | PUT | DELETE
        
        @SerializedName("url")
        private String url;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

