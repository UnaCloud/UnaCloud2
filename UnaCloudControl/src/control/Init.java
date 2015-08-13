package control;

public class Init {
	public static final String QUEUE_NAME = "test";
	public static void main(String[] args) {
		new Producer().start();
		new Consumer().start();		
	}
}
