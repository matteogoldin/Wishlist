package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Wishlist{
	@Id
	private String name;
	
	@OneToMany(mappedBy="wishlist")
	private List<Item> items;
	
	private String desc;
	private int total;
	
	public Wishlist() {
		total = 0;
	}

	public Wishlist(String name, String desc) {
		this.name = name;
		this.desc = desc;
		items = new ArrayList<>();
		total = 0;
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
	
	public int getTotal() {
		return total;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wishlist other = (Wishlist) obj;
		return Objects.equals(name, other.name);
	}
}
