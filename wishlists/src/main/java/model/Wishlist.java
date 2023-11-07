package model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Wishlist extends BaseEntity {
	private String name;
	private String desc;
	@OneToMany(mappedBy="wishlist")
	private List<Item> items;
	
	private Wishlist() {}

	public Wishlist(String name, String desc) {
		this.name = name;
		this.desc = desc;
		items = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}	
}
