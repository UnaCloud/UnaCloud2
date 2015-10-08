package back.pmallocators;

public enum AllocatorEnum {
	RANDOM(new RandomAllocator(),"Random"),ROUND_ROBIN(new RoundRobinAllocator(),"Round Robin"),GREEN(null,"Green"),FIRST_FIT(new FirstFitAllocator(),"First Fit"),FIRST_FIT_DECREASING(new FirstFitDecreasingAllocator(),"First Fit Decreasing"),BEST_FIT(new BestFitAllocator(),"Best Fit"),SORTING(new SortingAllocator(),"Sorting"),SINGLETON( new SingletonAllocator(), "Singleton");
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
}
