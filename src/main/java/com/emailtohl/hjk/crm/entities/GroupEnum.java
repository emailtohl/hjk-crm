package com.emailtohl.hjk.crm.entities;

public enum GroupEnum {
	ADMIN("系统管理员", "EMPLOYEE"), FINANCE("财务", "EMPLOYEE"), ADMINISTRATION("行政", "EMPLOYEE"), MARKET("市场",
			"EMPLOYEE"), FOREIGN("外务", "FOREIGN"), CUSTOMER("客户", "CUSTOMER");
	
	private GroupEnum(String name, String type) {
		this.name = name;
		this.type = type;
		this.id = name();
	}

	public final String name;
	public final String type;
	public final String id;

	@Override
	public String toString() {
		return String.format("{\"id\":\"%s\",\"name\":\"%s\",\"type\":\"%s\"}", id, name, type);
	}
	
}
