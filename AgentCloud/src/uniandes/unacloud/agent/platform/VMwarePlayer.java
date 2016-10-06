package uniandes.unacloud.agent.platform;

/**
 * Implementation of platform abstract class to give support for VMwarePlayer platform.
 * @author Clouder
 */
class VMwarePlayer extends VMwareAbstractHypervisor{
	public VMwarePlayer(String path) {
		super(path);
	}
	@Override
	protected String getType() {
		return "player";
	}	
}