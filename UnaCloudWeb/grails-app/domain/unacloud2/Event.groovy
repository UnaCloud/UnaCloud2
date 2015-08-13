package unacloud2

class Event {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * resume of the event
	 */
    String resume
	
	/**
	 * date when the event took place
	 */
	Date time
	
	/**
	 * event detailed description
	 */
	String description
	
	/**
	 * event type classification
	 */
	String type
	
	static constraints = {
    }
}
