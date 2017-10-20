package uniandes.unacloud.web.pmallocators;

/**
 * represents types of allocator algorithms 
 * @author Clouder
 *
 */
public enum AllocatorEnum {
	
	/**
	 * Assigns an execution to a physical machine based in random number.
	 */
	RANDOM(new RandomAllocator(), "Random"),
	
	/**
	 * Assigns an for each physical machine order by physical machine id
	 */
	ROUND_ROBIN(new RoundRobinAllocator(), "Round Robin"),
	
	/**
	 * Unused
	 */
	//GREEN(null,"Green"),
	
	/**
	 * Assigns all possible executions for each physical machine based in available resources
	 */
	FIRST_FIT(new FirstFitAllocator(), "First Fit"),
	
	/**
	 * Sorts physical machines based in available resources, then assigns all possible executions for each physical machine in list
	 */
	FIRST_FIT_DECREASING(new FirstFitDecreasingAllocator(), "First Fit Decreasing"),
	
	/**
	 * Sorts physical machines based in available resources, assigns an execution in first machine in list and sorts again.
	 */
	BEST_FIT(new BestFitAllocator(), "Best Fit"),
	
	/**
	 * Extends BEST FIT algorithm adding user as a variable in sort process
	 */
	SORTING(new SortingAllocator(), "Sorting"),
	
	/**
	 * Assigns only one execution for each physical machine
	 */
	SINGLETON( new SingletonAllocator(), "Singleton");
	
	/**
	 * allocator class to execute algorithm
	 */
	private ExecutionAllocator allocator;
	
	/**
	 * Name of allocator
	 */
	private String name;
	
	/**
	 * Creates an allocator enum
	 * @param allocator enum
	 * @param name for enum
	 */
	private AllocatorEnum(ExecutionAllocator allocator, String name) {
		this.allocator = allocator;
		this.name = name;
	}
	
	/**
	 * Returns allocator class to execute algorithm
	 * @return allocator class 
	 */
	public ExecutionAllocator getAllocator() {
		return allocator;
	}
	
	/**
	 * Returns name of allocator
	 * @return String name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return a list of current types of allocator
	 * @return
	 */
	public static String[] getList() {
		return new String[]{
				RANDOM.name,
				ROUND_ROBIN.name,
				/*GREEN.name,*/
				FIRST_FIT.name,
				FIRST_FIT_DECREASING.name,
				BEST_FIT.name,
				SORTING.name,
				SINGLETON.name};
	}
	
	/**
	 * Searches and returns an allocator class based in string name
	 * @param name to search
	 * @return allocator type
	 */
	public static AllocatorEnum getAllocatorByName(String name) {
		if (name.equals(RANDOM.name)) return RANDOM;
		if (name.equals(ROUND_ROBIN.name)) return ROUND_ROBIN;
		//if(name.equals(GREEN.name))return GREEN;
		if (name.equals(FIRST_FIT.name)) return FIRST_FIT;
		if (name.equals(FIRST_FIT_DECREASING.name)) return FIRST_FIT_DECREASING;
		if (name.equals(BEST_FIT.name)) return BEST_FIT;
		if (name.equals(SORTING.name)) return SORTING;
		if (name.equals(SINGLETON.name)) return SINGLETON;
		return null;
	}
}
