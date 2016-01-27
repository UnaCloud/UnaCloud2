package unacloud.entities;

/**
 * Class to represent Repository entity in database
 * @author Cesar
 *
 */
public class Repository {
	
	private Long id;
	private String name;
	private int capacity;
	private String root;
	
	public Repository(Long id, String name, int capacity, String root) {
		super();
		this.id = id;
		this.name = name;
		this.capacity = capacity;
		this.root = root;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
