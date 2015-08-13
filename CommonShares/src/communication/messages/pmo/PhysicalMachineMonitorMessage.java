package communication.messages.pmo;

import communication.messages.PhysicalMachineOperationMessage;

public class PhysicalMachineMonitorMessage extends PhysicalMachineOperationMessage{
	private static final long serialVersionUID = -6593319992803639653L;
	
	public static final int M_STOP = 1;
    public static final int M_START = 2;
    public static final int M_UPDATE = 3;
    public static final int M_ENABLE = 4;
	int operation;
	int monitorFrequency;
    int registerFrequency;
    int monitorFrecuencyEnergy;
    int registerFrecuencyEnergy;
    private boolean energy;
    private boolean cpu;
    
	public PhysicalMachineMonitorMessage() {
		super(PM_MONITOR);
	}
	public int getOperation() {
		return operation;
	}
	public int getMonitorFrequency() {
		return monitorFrequency;
	}
	public int getRegisterFrequency() {
		return registerFrequency;
	}
	public int getMonitorFrecuencyEnergy() {
		return monitorFrecuencyEnergy;
	}
	public int getRegisterFrecuencyEnergy() {
		return registerFrecuencyEnergy;
	}
	public boolean isCpu() {
		return cpu;
	}
	public void setCpu(boolean cpu) {
		this.cpu = cpu;
	}
	public boolean isEnergy() {
		return energy;
	}
	public void setEnergy(boolean energy) {
		this.energy = energy;
	}
	@Override
	public String toString() {
		return "PhysicalMachineMonitorMessage [operation=" + operation
				+ ", monitorFrequency=" + monitorFrequency
				+ ", registerFrequency=" + registerFrequency
				+ ", monitorFrecuencyEnergy=" + monitorFrecuencyEnergy
				+ ", registerFrecuencyEnergy=" + registerFrecuencyEnergy
				+ ", energy=" + energy + ", cpu=" + cpu + "]";
	}
	
}
