package daos;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import model.Item;
import model.Wishlist;

public final class DAOTestsSQLQueries {

	public static void initEmptyDB(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		em.createNativeQuery("truncate table Item");
		em.createNativeQuery("truncate table Wishlist");
		em.close();
	}
	
	public static void insertWishlist(Wishlist wl, EntityManagerFactory emf) {
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
	
	public static Wishlist findWishlist(Wishlist wl, EntityManagerFactory emf) {
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
	
	
	public static Item findItem(Wishlist wl, Item item, EntityManagerFactory emf) {
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
	
	public static void insertItem(Wishlist wl, Item item, EntityManagerFactory emf) {
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
	
	public static List<Item> findAllItemsFromAWL(Wishlist wl, EntityManagerFactory emf) {
		List<Item> itemList;
		EntityManager em = emf.createEntityManager();
		itemList = em.createQuery("SELECT it FROM Wishlist wl JOIN wl.items it WHERE wl.name = :wl_name", Item.class)
				.setParameter("wl_name", wl.getName())
				.getResultList();
		em.close();
		return itemList;
	}

	public static void mergeWishlist(Wishlist wl, EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(wl);
		em.getTransaction().commit();
		em.close();		
	}
}
