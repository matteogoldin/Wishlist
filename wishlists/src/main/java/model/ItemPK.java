package model;

import java.io.Serializable;
import java.util.Objects;

public class ItemPK implements Serializable{
	private String name;
	private Wishlist wishlist;
	
	public ItemPK(String name, Wishlist wishlist) {
		this.name = name;
		this.wishlist = wishlist;
	}
	
	public ItemPK() {}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Wishlist getWishlist() {
		return wishlist;
	}

	public void setWishlist(Wishlist wishlist) {
		this.wishlist = wishlist;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		ItemPK pk = (ItemPK) o;
		return Objects.equals( name, pk.name ) &&
				Objects.equals( wishlist, pk.wishlist );
	}

	@Override
	public int hashCode() {
		return Objects.hash( name, wishlist );
	}

}
