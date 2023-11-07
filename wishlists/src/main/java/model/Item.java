package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;

@Entity
@IdClass(ItemPK.class)
public class Item{
	@Id
	private String name;
	
	@Id
	@ManyToOne
	private Wishlist wishlist;
	
	private String desc;
	private String price;
	
	public Item() {}

	public Item(String name, String desc, String price, Wishlist wishlist) {
		this.name = name;
		this.wishlist = wishlist;
		this.desc = desc;
		this.price = price;
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
