package uniandes.unacloud.share.enums;

/**
 * Enum to represents type of message that could be sent by the queue
 * @author CesarF
 *
 */
public enum QueueMessageType {

	/**
	 * Remove an image from all physical machines
	 */
	CLEAR_CACHE,
	
	/**
	 * Remove an image from all physical machines and update state
	 */
	CLEAR_CACHE_UPDATE,
	
	/**
	 * Delete an user an all images belonged
	 */
	DELETE_USER,
	
	/**
	 * Send task to agent
	 */
	SEND_TASK,
	
	/**
	 * Deploy executions in physical machines
	 */
	DEPLOY_CLUSTER,
	
	/**
	 * Stop deployments
	 */
	STOP_DEPLOYS,
	
	/**
	 * Add instances to deployment
	 */
	ADD_INSTANCES,
	
	/**
	 * Create a copy from a current execution deployed
	 */
	CREATE_COPY,
	
	/**
	 * Create a public image from private one
	 */
	CREATE_PUBLIC_IMAGE,
	
	/**
	 * create a private image from public one
	 */
	CREATE_COPY_FROM_PUBLIC,
	
	/**
	 * Delete image in repositories
	 */
	DELETE_IMAGE,
	
	/**
	 * Delete a public image in repositories
	 */
	DELETE_PUBLIC_IMAGE;
	
	/**
	 * Returns a type of message searched by name
	 * @param name of type
	 * @return type of message
	 */
	public static QueueMessageType getType(String name){
		if (name.equals(CLEAR_CACHE.name())) return CLEAR_CACHE;
		if (name.equals(CLEAR_CACHE_UPDATE.name())) return CLEAR_CACHE_UPDATE;
		if (name.equals(DELETE_USER.name())) return DELETE_USER;
		if (name.equals(SEND_TASK.name())) return SEND_TASK;
		if (name.equals(DEPLOY_CLUSTER.name())) return DEPLOY_CLUSTER;
		if (name.equals(STOP_DEPLOYS.name())) return STOP_DEPLOYS;
		if (name.equals(ADD_INSTANCES.name())) return ADD_INSTANCES;
		if (name.equals(CREATE_COPY.name())) return CREATE_COPY;
		if (name.equals(CREATE_PUBLIC_IMAGE.name())) return CREATE_PUBLIC_IMAGE;
		if (name.equals(CREATE_COPY_FROM_PUBLIC.name())) return CREATE_COPY_FROM_PUBLIC;
		if (name.equals(DELETE_IMAGE.name())) return DELETE_IMAGE;
		if (name.equals(DELETE_PUBLIC_IMAGE.name())) return DELETE_PUBLIC_IMAGE;
		return null;
	}
}
