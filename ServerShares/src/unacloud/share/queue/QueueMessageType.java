package unacloud.share.queue;

/**
 * Enum to represents type of message that could be sent by the queue
 * @author Cesar
 *
 */
public enum QueueMessageType {

	//Types for Agent control
	/**
	 * Remove an image from all physical machines
	 */
	CLEAR_CACHE,
	
	DELETE_USER,
	SEND_TASK,
	DEPLOY_CLUSTER,
	STOP_DEPLOYS,
	ADD_INSTANCES,
	CREATE_COPY,
	/**
	 * Types for File Manager control
	 */
	CREATE_PUBLIC_IMAGE,
	CREATE_COPY_FROM_PUBLIC,
	DELETE_IMAGE,
	DELETE_PUBLIC_IMAGE;
	
	public static QueueMessageType getType(String name){
		if(name.equals(CLEAR_CACHE.name()))return CLEAR_CACHE;
		if(name.equals(DELETE_USER.name()))return DELETE_USER;
		if(name.equals(SEND_TASK.name()))return SEND_TASK;
		if(name.equals(DEPLOY_CLUSTER.name()))return DEPLOY_CLUSTER;
		if(name.equals(STOP_DEPLOYS.name()))return STOP_DEPLOYS;
		if(name.equals(ADD_INSTANCES.name()))return ADD_INSTANCES;
		if(name.equals(CREATE_COPY.name()))return CREATE_COPY;
		if(name.equals(CREATE_PUBLIC_IMAGE.name()))return CREATE_PUBLIC_IMAGE;
		if(name.equals(CREATE_COPY_FROM_PUBLIC.name()))return CREATE_COPY_FROM_PUBLIC;
		if(name.equals(DELETE_IMAGE.name()))return DELETE_IMAGE;
		if(name.equals(DELETE_PUBLIC_IMAGE.name()))return DELETE_PUBLIC_IMAGE;
		return null;
	}
}
