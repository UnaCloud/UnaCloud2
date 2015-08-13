/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.losandes.wsEntities;

import java.io.Serializable;
import java.util.List;

/**
 * Template entity to be transmitted on web services responses
 * @author Clouder
 */
public class TemplateWS implements Serializable {

    private static final long serialVersionUID = 1L;
    private int templateCode;
    private String templateName;
    private String templateType;
    private OperatingSystemWS operatingSystem;
    private List<String> applications;
    private boolean customizable;
    private boolean highAvailability;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getTemplateCode() != null ? getTemplateCode().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.losandes.persistence.entity.Template[id=" + getTemplateCode() + "]";
    }

    /**
     * @return the templateCode
     */
    public Integer getTemplateCode() {
        return templateCode;
    }

    /**
     * @param templateCode the templateCode to set
     */
    public void setTemplateCode(Integer TemplateCode) {
        this.templateCode = TemplateCode;
    }

    /**
     * @return the templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @param templateName the templateName to set
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * @return the operatingSystem
     */
    public OperatingSystemWS getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * @param operatingSystem the operatingSystem to set
     */
    public void setOperatingSystem(OperatingSystemWS operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    /**
     * @return the templateType
     */
    public String getTemplateType() {
        return templateType;
    }

    /**
     * @param templateType the templateType to set
     */
    public void setTemplateType(String template) {
        this.templateType = template;
    }

    /**
     * @return the applications
     */
    public List<String> getApplications() {
        return applications;
    }

    /**
     * @param applications the applications to set
     */
    public void setApplications(List<String> applications) {
        this.applications = applications;
    }

    public boolean isCustomizable() {
        return customizable;
    }

    public void setCustomizable(boolean customizable) {
        this.customizable = customizable;
    }

    public boolean isHighAvailability() {
        return highAvailability;
    }

    public void setHighAvailability(boolean highAvailability) {
        this.highAvailability = highAvailability;
    }
}//end of Template

