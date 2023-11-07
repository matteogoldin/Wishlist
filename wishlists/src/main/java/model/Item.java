package model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Item extends BaseEntity {
	private String name;
	private String desc;
	private String price;
	@ManyToOne
	private Wishlist wishlist;
	
	public Item() {}

	public Item(String name, String desc, String price, Wishlist wishlist) {
		this.name = name;
		this.desc = desc;
		this.price = price;
		this.wishlist = wishlist;
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Wishlist getWishlist() {
		return wishlist;
	}

	public void setWishlist(Wishlist wishlist) {
		this.wishlist = wishlist;
	}
}
