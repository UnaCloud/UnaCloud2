package communication.messages;

import communication.UnaCloudAbstractMessage;
import communication.UnaCloudMessage;

public abstract class PhysicalMachineOperationMessage extends UnaCloudAbstractMessage{
	private static final long serialVersionUID = 6749899457514674239L;
	public static final int PM_INIT = 1;
    public static final int PM_TURN_OFF = 1;
    public static final int PM_RESTART = 2;
    public static final int PM_LOGOUT = 3;
    public static final int PM_MONITOR = 4;
    public static final int PM_WRITE_FILE = 6;
    public static final int PM_TURN_ON = 7;
    public static final int PM_RETRIEVE_FOLDER = 8;
	public PhysicalMachineOperationMessage(int subOperation){
		super(PHYSICAL_MACHINE_OPERATION, subOperation);
	}
	public static UnaCloudAbstractMessage fromMessage(UnaCloudMessage message){
		return null;
	}
}
