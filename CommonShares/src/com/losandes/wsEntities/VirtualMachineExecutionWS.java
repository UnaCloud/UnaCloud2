/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.losandes.wsEntities;

import java.io.Serializable;
import java.util.Date;

/**
 * Virtual machine execution to be transmitted on web services responses
 * @author Clouder
 */
public class VirtualMachineExecutionWS implements Serializable {

	private static final long serialVersionUID = 6477669535131451902L;

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private String virtualMachineExecutionCode;
    private String virtualMachineName;
    private Date virtualMachineExecutionStart;
    private Date virtualMachineExecutionStop;
    private int virtualMachineExecutionStatus;
    private String virtualMachineExecutionStatusMessage;
    private long virtualMachineExecutionHardDisk;
    private int virtualMachineExecutionCores;
    private int virtualMachineExecutionRAMMemory;
    private String virtualMachineExecutionIP;
    private Integer template;
    private String systemUser;
    private Integer virtualMachine;
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getVirtualMachineExecutionCode() != null ? getVirtualMachineExecutionCode().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.losandes.persistence.entity.VirtualMachineExecution[id=" + getVirtualMachineExecutionCode() + "]";
    }

    public String getVirtualMachineExecutionCode() {
        return virtualMachineExecutionCode;
    }

    public void setVirtualMachineExecutionCode(String virtualMachineExecutionCode) {
        this.virtualMachineExecutionCode = virtualMachineExecutionCode;
    }



    /**
     * @return the virtualMachineExecutionStart
     */
    public Date getVirtualMachineExecutionStart() {
        return virtualMachineExecutionStart;
    }

    /**
     * @param virtualMachineExecutionStart the virtualMachineExecutionStart to set
     */
    public void setVirtualMachineExecutionStart(Date virtualMachineExecutionStart) {
        this.virtualMachineExecutionStart = virtualMachineExecutionStart;
    }

    /**
     * @return the virtualMachineExecutionStop
     */
    public Date getVirtualMachineExecutionStop() {
        return virtualMachineExecutionStop;
    }

    /**
     * @param virtualMachineExecutionStop the virtualMachineExecutionStop to set
     */
    public void setVirtualMachineExecutionStop(Date virtualMachineExecutionStop) {
        this.virtualMachineExecutionStop = virtualMachineExecutionStop;
    }

    /**
     * @return the virtualMachineExecutionStatus
     */
    public int getVirtualMachineExecutionStatus() {
        return virtualMachineExecutionStatus;
    }

    /**
     * @param virtualMachineExecutionStatus the virtualMachineExecutionStatus to set
     */
    public void setVirtualMachineExecutionStatus(int virtualMachineExecutionStatus) {
        this.virtualMachineExecutionStatus = virtualMachineExecutionStatus;
    }

    /**
     * @return the virtualMachineExecutionHardDisk
     */
    public long getVirtualMachineExecutionHardDisk() {
        return virtualMachineExecutionHardDisk;
    }

    /**
     * @param virtualMachineExecutionHardDisk the virtualMachineExecutionHardDisk to set
     */
    public void setVirtualMachineExecutionHardDisk(long virtualMachineExecutionHardDisk) {
        this.virtualMachineExecutionHardDisk = virtualMachineExecutionHardDisk;
    }

    /**
     * @return the virtualMachineExecutionCores
     */
    public int getVirtualMachineExecutionCores() {
        return virtualMachineExecutionCores;
    }

    /**
     * @param virtualMachineExecutionCores the virtualMachineExecutionCores to set
     */
    public void setVirtualMachineExecutionCores(int virtualMachineExecutionCores) {
        this.virtualMachineExecutionCores = virtualMachineExecutionCores;
    }

    /**
     * @return the virtualMachineExecutionRAMMemory
     */
    public int getVirtualMachineExecutionRAMMemory() {
        return virtualMachineExecutionRAMMemory;
    }

    /**
     * @param virtualMachineExecutionRAMMemory the virtualMachineExecutionRAMMemory to set
     */
    public void setVirtualMachineExecutionRAMMemory(int virtualMachineExecutionRAMMemory) {
        this.virtualMachineExecutionRAMMemory = virtualMachineExecutionRAMMemory;
    }

    /**
     * @return the virtualMachineExecutionIP
     */
    public String getVirtualMachineExecutionIP() {
        return virtualMachineExecutionIP;
    }

    /**
     * @param virtualMachineExecutionIP the virtualMachineExecutionIP to set
     */
    public void setVirtualMachineExecutionIP(String virtualMachineExecutionIP) {
        this.virtualMachineExecutionIP = virtualMachineExecutionIP;
    }

    /**
     * @return the virtualMachine
     */
    public Integer getVirtualMachine() {
        return virtualMachine;
    }

    /**
     * @param virtualMachine the virtualMachine to set
     */
    public void setVirtualMachine(Integer virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    /**
     * @return the template
     */
    public Integer getTemplate() {
        return template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(Integer template) {
        this.template = template;
    }

    /**
     * @return the systemUser
     */
    public String getSystemUser() {
        return systemUser;
    }

    /**
     * @param systemUser the systemUser to set
     */
    public void setSystemUser(String systemUser) {
        this.systemUser = systemUser;
    }

    public String getVirtualMachineExecutionStatusMessage() {
        return virtualMachineExecutionStatusMessage;
    }

    public void setVirtualMachineExecutionStatusMessage(String virtualMachineExecutionStatusMessage) {
        this.virtualMachineExecutionStatusMessage = virtualMachineExecutionStatusMessage;
    }

    public String getVirtualMachineName() {
        return virtualMachineName;
    }

    public void setVirtualMachineName(String virtualMachineName) {
        this.virtualMachineName = virtualMachineName;
    }

    

}// end of VirtualMachineExecution

