package com.zelwise.spiewnik;

public class TabsItem {

	private Integer id = -1;
	public Integer Id() {
		return id;
	}

	private String name = "";
	public String Name() {
		return name;
	}

	public TabsItem(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return this.Name();
	}
}