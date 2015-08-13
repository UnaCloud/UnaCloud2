package monitoring;

public class PhysicalMachineMonitor extends ControlMonitoring{
	
	private static PhysicalMachineMonitor instance;
	
	public static PhysicalMachineMonitor getInstance(){
		if(instance == null)instance = new PhysicalMachineMonitor();
		return instance;
	}
	
	public PhysicalMachineMonitor() {
		setConfiguration(new AgentConfiguration(), new MonitorDBAgentConnection());
	}
}
