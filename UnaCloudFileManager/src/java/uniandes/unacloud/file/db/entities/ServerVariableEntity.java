package uniandes.unacloud.file.db.entities;

import uniandes.unacloud.share.enums.ServerVariableTypeEnum;

/**
 * This class represents a Server Variable entity saved in database
 * @author CesarF
 *
 */
public class ServerVariableEntity {
	
	private String name;
	private String value;
	private ServerVariableTypeEnum type;
	private boolean list;
	
	public ServerVariableEntity(String name, String value, ServerVariableTypeEnum type, boolean list) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
		this.list = list;
	}
	
	public boolean isList() {
		return list;
	}
	
	public void setList(boolean list) {
		this.list = list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ServerVariableTypeEnum getType() {
		return type;
	}

	public void setType(ServerVariableTypeEnum type) {
		this.type = type;
	}
	
}
