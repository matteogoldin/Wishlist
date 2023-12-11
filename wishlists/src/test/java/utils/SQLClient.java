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
	
	public void insertWishlist(Wishlist wl) {
		String nativeQuery = "INSERT INTO Wishlist (name, description) VALUES (?, ?)";
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(nativeQuery)
			.setParameter(1, wl.getName())
			.setParameter(2, wl.getDesc())
			.executeUpdate();
		em.getTransaction().commit();
		em.close();
	}
	
	public Wishlist findWishlist(Wishlist wl) {
		EntityManager em = emf.createEntityManager();
		Wishlist wl_dup = null;
		try {
			wl_dup = em.createQuery("SELECT wl FROM Wishlist wl WHERE wl.name = :name", Wishlist.class)
				.setParameter("name", wl.getName())
				.getSingleResult();
		} catch (NoResultException e) {
			wl_dup = null;
		}
		em.close();
		return wl_dup;
	}
	
	
	public Item findItem(Wishlist wl, Item item) {
		EntityManager em = emf.createEntityManager();
		Item item_dup = null;
		try {
			item_dup = em.createQuery("SELECT it FROM Wishlist wl JOIN wl.items it WHERE wl.name = :wl_name AND it.name = :it_name", Item.class)
					.setParameter("wl_name", wl.getName())
					.setParameter("it_name", item.getName())
					.getSingleResult();
		} catch (NoResultException e) {
			item_dup = null;
		}
		em.close();
		return item_dup;
	}
	
	public void insertItem(Wishlist wl, Item item) {
		String nativeQuery = "INSERT INTO item (name, description, price, wishlist_name) VALUES (?, ?, ?, ?)";
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(nativeQuery)
			.setParameter(1, item.getName())
			.setParameter(2, item.getDesc())
			.setParameter(3, item.getPrice())
			.setParameter(4, wl.getName())
			.executeUpdate();
		em.getTransaction().commit();
		em.close();
	}
	
	public List<Item> findAllItemsFromAWL(Wishlist wl) {
		List<Item> itemList;
		EntityManager em = emf.createEntityManager();
		itemList = em.createQuery("SELECT it FROM Wishlist wl JOIN wl.items it WHERE wl.name = :wl_name", Item.class)
				.setParameter("wl_name", wl.getName())
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
