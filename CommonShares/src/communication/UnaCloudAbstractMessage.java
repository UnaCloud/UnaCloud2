/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.io.Serializable;

import communication.messages.PhysicalMachineOperationMessage;
import communication.messages.VirtualMachineOperationMessage;

/**
 *
 * @author Clouder
 */
public abstract class UnaCloudAbstractMessage implements Serializable{
    
	private static final long serialVersionUID = 567714696423776118L;
	public static final int VIRTUAL_MACHINE_OPERATION = 1;
    public static final int PHYSICAL_MACHINE_OPERATION = 2;
    public static final int AGENT_OPERATION = 3;
    
    public static final int DATABASE_OPERATION = 1;
    public static final int REGISTRATION_OPERATION = 2;
    
    private int mainOp;
    private int subOp;

    public UnaCloudAbstractMessage(int mainOp, int subOp){
        this.mainOp = mainOp;
        this.subOp = subOp;
    }
    public int getMainOp() {
        return mainOp;
    }

    public void setMainOp(int mainOp) {
        this.mainOp = mainOp;
    }

    public int getSubOp() {
        return subOp;
    }

    public void setSubOp(int subOp) {
        this.subOp = subOp;
    }
    public static UnaCloudAbstractMessage fromMessage(UnaCloudAbstractMessage message){
    	switch(message.getMainOp()){
    		case PHYSICAL_MACHINE_OPERATION:
    			return PhysicalMachineOperationMessage.fromMessage(message);
    		case VIRTUAL_MACHINE_OPERATION:
    			return VirtualMachineOperationMessage.fromMessage(message);
    	}
    	return null;
    }
}
