package unacloud2

class Traceability {
	
	String virtualMachine
	String user
	Date machineStartTime
	Date machineStopTime
	String machineIP
	Collection<Event> events
    static constraints = {
    }
}
