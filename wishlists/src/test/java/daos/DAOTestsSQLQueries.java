package daos;

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
		String nativeQuery = "INSERT INTO Wishlist (name, desc) VALUES (?, ?)";
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
	
	public static void insertItem(Item item, EntityManagerFactory emf) {
		String nativeQuery = "INSERT INTO Item (name, desc, price, wishlist_name) VALUES (?, ?, ?, ?)";
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(nativeQuery)
			.setParameter(1, item.getName())
			.setParameter(2, item.getDesc())
			.setParameter(3, item.getPrice())
			.setParameter(4, item.getWishlist().getName())
			.executeUpdate();
		em.getTransaction().commit();
		em.close();
	}
	
	public static Item findItem(Item item, EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		Item item_dup = null;
		try {
			item_dup = em.createQuery(
					"SELECT it FROM Item it WHERE it.name = :name", Item.class)
				.setParameter("name", item.getName())
				.getSingleResult();
		} catch (NoResultException e) {
			item_dup = null;
		}
		em.close();
		return item_dup;
	}
}
