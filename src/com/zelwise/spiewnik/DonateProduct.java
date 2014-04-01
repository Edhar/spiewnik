package com.zelwise.spiewnik;

public class DonateProduct {
	private int id = 0;
	public int Id() {
		return id;
	}

	private String labelDescription = "";
	public String LabelDescription() {
		return labelDescription;
	}
	
	private String productSKU = "";
	public String ProductSKU() {
		return productSKU;
	}

	public DonateProduct(int id, String labelDescription, String productSKU) {
		this.id = id;
		this.labelDescription = labelDescription;
		this.productSKU = productSKU;
	}
	
	@Override
	public String toString() {
		return this.LabelDescription();
	}
}
