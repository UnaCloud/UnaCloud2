package monitoring;

import com.losandes.utils.VariableManager;

public class AgentConfiguration extends MonitorConfiguration{

	@Override
	public void disableCpuMonitoring() {
		VariableManager.local.setBooleanValue("MONITORING_ENABLE_CPU", false);			
	}

	@Override
	public void disableEnergyMonitoring() {
		VariableManager.local.setBooleanValue("MONITORING_ENABLE_ENERGY", false);	
	}

	@Override
	public void enableCpuMonitoring() {
		VariableManager.local.setBooleanValue("MONITORING_ENABLE_CPU", true);
	}

	@Override
	public void enableEnergyMonitoring() {
		VariableManager.local.setBooleanValue("MONITORING_ENABLE_ENERGY", true);
	}

	@Override
	public int getCpuMonitorFrecuency() {
		return VariableManager.global.getIntValue("MONITOR_FREQUENCY_CPU");
	}

	@Override
	public int getCpuMonitorRegisterFrecuency() {
		return VariableManager.global.getIntValue("MONITOR_REGISTER_FREQUENCY_CPU");
	}

	@Override
	public String getDataPath() {
		return VariableManager.local.getStringValue("DATA_PATH");
	}

	@Override
	public int getEnergyMonitorFrecuency() {
		return VariableManager.global.getIntValue("MONITOR_FREQUENCY_ENERGY");
	}

	@Override
	public int getEnergyMonitorRegisterFrecuency() {
		return VariableManager.global.getIntValue("MONITOR_REGISTER_FREQUENCY_ENERGY");
	}

	@Override
	public String getLogCpuPath() {
		return VariableManager.local.getStringValue("LOG_CPU_PATH");
	}

	@Override
	public String getLogEnergyPath() {
		return VariableManager.local.getStringValue("LOG_ENERGY_PATH");
	}

	@Override
	public String getPowerAppPath() {
		return VariableManager.local.getStringValue("PATH_POWERLOG");
	}

	@Override
	public boolean isMonitoringCpuEnable() {
		return VariableManager.local.getBooleanValue("MONITORING_ENABLE_CPU");
	}

	@Override
	public boolean isMonitoringEnergyEnable() {
		return VariableManager.local.getBooleanValue("MONITORING_ENABLE_ENERGY");			
	}

	@Override
	public void setCpuMonitorFrecuency(int arg0) {
		VariableManager.global.setIntValue("MONITOR_FREQUENCY_CPU",arg0);
	}

	@Override
	public void setCpuMonitorRegisterFrecuency(int arg0) {
		VariableManager.global.setIntValue("MONITOR_REGISTER_FREQUENCY_CPU",arg0);	
	}

	@Override
	public void setEnergyMonitorFrecuency(int arg0) {
		VariableManager.global.setIntValue("MONITOR_FREQUENCY_ENERGY",arg0);
	}

	@Override
	public void setEnergyMonitorRegisterFrecuency(int arg0) {
		VariableManager.global.setIntValue("MONITOR_REGISTER_FREQUENCY_ENERGY",arg0);
	}
	
}
