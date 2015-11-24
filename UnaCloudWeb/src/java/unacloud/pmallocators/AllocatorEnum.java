package unacloud.pmallocators;

public enum AllocatorEnum {
	RANDOM(new RandomAllocator(),"Random"),ROUND_ROBIN(new RoundRobinAllocator(),"Round Robin"),GREEN(null,"Green"),
	FIRST_FIT(new FirstFitAllocator(),"First Fit"),FIRST_FIT_DECREASING(new FirstFitDecreasingAllocator(),"First Fit Decreasing"),
	BEST_FIT(new BestFitAllocator(),"Best Fit"),SORTING(new SortingAllocator(),"Sorting"),SINGLETON( new SingletonAllocator(), "Singleton");
	VirtualMachineAllocator allocator;
	String name;
	
	private AllocatorEnum(VirtualMachineAllocator allocator, String name) {
		this.allocator=allocator;
		this.name=name;
	}
	public VirtualMachineAllocator getAllocator() {
		return allocator;
	}
	
	public String getName(){
		return name;
	}
	
	public static String[] getList(){
		return new String[]{RANDOM.name,ROUND_ROBIN.name,GREEN.name,FIRST_FIT.name,FIRST_FIT_DECREASING.name,
				BEST_FIT.name,SORTING.name,SINGLETON.name};
	}
	
	public static AllocatorEnum getAllocatorByName(String name){
		if(name.equals(RANDOM.name))return RANDOM;
		if(name.equals(ROUND_ROBIN.name))return ROUND_ROBIN;
		if(name.equals(GREEN.name))return GREEN;
		if(name.equals(FIRST_FIT.name))return FIRST_FIT;
		if(name.equals(FIRST_FIT_DECREASING.name))return FIRST_FIT_DECREASING;
		if(name.equals(BEST_FIT.name))return BEST_FIT;
		if(name.equals(SORTING.name))return SORTING;
		if(name.equals(SINGLETON.name))return SINGLETON;
		return null;
	}
}
