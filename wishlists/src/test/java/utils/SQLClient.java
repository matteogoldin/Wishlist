package utils;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import model.Item;
import model.Wishlist;

public class SQLClient {
	EntityManagerFactory emf;
	
	public SQLClient(String persistenceUnit) {
		emf = Persistence.createEntityManagerFactory(persistenceUnit);
	}
	
	public void initEmptyDB() {
		EntityManager em = emf.createEntityManager();
		em.createNativeQuery("truncate table Item");
		em.createNativeQuery("truncate table Wishlist");
		em.close();
	}
	
	public void insertWishlist(String name, String desc) {
		String nativeQuery = "INSERT INTO Wishlist (name, description) VALUES (?, ?)";
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(nativeQuery)
			.setParameter(1, name)
			.setParameter(2, desc)
			.executeUpdate();
		em.getTransaction().commit();
		em.close();
	}
	
	public Wishlist findWishlist(String name) {
		EntityManager em = emf.createEntityManager();
		Wishlist wl_dup = null;
		try {
			wl_dup = em.createQuery("SELECT wl FROM Wishlist wl WHERE wl.name = :name", Wishlist.class)
				.setParameter("name", name)
				.getSingleResult();
		} catch (NoResultException e) {
			wl_dup = null;
		}
		em.close();
		return wl_dup;
	}
	
	
	public Item findItem(String wlName, String itemName) {
		EntityManager em = emf.createEntityManager();
		Item item_dup = null;
		try {
			item_dup = em.createQuery("SELECT it FROM Wishlist wl JOIN wl.items it WHERE wl.name = :wl_name AND it.name = :it_name", Item.class)
					.setParameter("wl_name", wlName)
					.setParameter("it_name", itemName)
					.getSingleResult();
		} catch (NoResultException e) {
			item_dup = null;
		}
		em.close();
		return item_dup;
	}
	
	public void insertItem(String wlName, String itemName, String itemDesc, float itemPrice) {
		String nativeQuery = "INSERT INTO item (name, description, price, wishlist_name) VALUES (?, ?, ?, ?)";
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(nativeQuery)
			.setParameter(1, itemName)
			.setParameter(2, itemDesc)
			.setParameter(3, itemPrice)
			.setParameter(4, wlName)
			.executeUpdate();
		em.getTransaction().commit();
		em.close();
	}
	
	public List<Item> findAllItemsFromAWL(String wlName) {
		List<Item> itemList;
		EntityManager em = emf.createEntityManager();
		itemList = em.createQuery("SELECT it FROM Wishlist wl JOIN wl.items it WHERE wl.name = :wl_name", Item.class)
				.setParameter("wl_name", wlName)
				.getResultList();
		em.close();
		return itemList;
	}

	public void mergeWishlist(Wishlist wl) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(wl);
		em.getTransaction().commit();
		em.close();		
	}
}
