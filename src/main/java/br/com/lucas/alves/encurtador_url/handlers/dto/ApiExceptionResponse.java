package br.com.lucas.alves.encurtador_url.handlers.dto;

import java.util.Map;

public class ApiExceptionResponse {
    private String friendlyMessage;
    private String technicalMessage;
    private Integer errorCode;
    private Map<String, Object> details;
    private String traceId;
    private String timestamp;

    public ApiExceptionResponse(String friendlyMessage, String technicalMessage, Integer errorCode, Map<String, Object> details, String traceId, String timestamp) {
        this.friendlyMessage = friendlyMessage;
        this.technicalMessage = technicalMessage;
        this.errorCode = errorCode;
        this.details = details;
        this.traceId = traceId;
        this.timestamp = timestamp;
    }

    public String getFriendlyMessage() {
        return friendlyMessage;
    }

    public void setFriendlyMessage(String friendlyMessage) {
        this.friendlyMessage = friendlyMessage;
    }

    public String getTechnicalMessage() {
        return technicalMessage;
    }

    public void setTechnicalMessage(String technicalMessage) {
        this.technicalMessage = technicalMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    
}
