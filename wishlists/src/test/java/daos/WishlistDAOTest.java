package daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.RollbackException;
import model.Item;
import model.Wishlist;

public class WishlistDAOTest {
	private WishlistDAO wDao;

	@BeforeEach
	void setup() {
		wDao = new WishlistDAO();
		EntityManager em = wDao.getEmf().createEntityManager();
		em.createNativeQuery("truncate table Item");
		em.createNativeQuery("truncate table Wishlist");
		em.close();
	}

	@Test
	void getAllWhenDatabaseIsEmpty() {
		assertThat(wDao.getAll()).isEmpty();
	}

	@Test
	void getAllWhenDatabaseIsNotEmpty() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		insertWishlist(wl);
		assertThat(wDao.getAll()).hasSize(1);
	}

	private void insertWishlist(Wishlist wl) {
		String nativeQuery = "INSERT INTO Wishlist (name, desc) VALUES (?, ?)";
		EntityManager em = wDao.getEmf().createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery(nativeQuery)
			.setParameter(1, wl.getName())
			.setParameter(2, wl.getDesc())
			.executeUpdate();
		em.getTransaction().commit();
		em.close();
	}

	@Test
	void WishlistCorrectlyInserted() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		wDao.add(wl);
		Wishlist wl_dup = findWishlist(wl);
		assertThat(wl).isEqualTo(wl_dup);
	}

	private Wishlist findWishlist(Wishlist wl) {
		EntityManager em = wDao.getEmf().createEntityManager();
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
	
	@Test
	void addingAWishlistThatAlreadyExistsRaiseAnException() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Wishlist wl_dup = new Wishlist("Birthday", "My mum birthday gifts");
		wDao.add(wl_dup);
		assertThatThrownBy(() -> wDao.add(wl)).isInstanceOf(RollbackException.class);
	}
	
	@Test
	void otherExceptionAreManaged() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		wDao.getEmf().close();
		assertThatThrownBy(() -> wDao.add(wl)).isInstanceOf(RuntimeException.class);
	}
	
	@Test
	void wishlistCorrectlyRemoved() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		insertWishlist(wl);
		wDao.remove(wl);
		assertThat(findWishlist(wl)).isNull();
	}
	
	@Test
	void removingANonPersistedEntity() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		assertDoesNotThrow(() -> wDao.remove(wl));
	}
	
	@Test
	void removingAWishlistRemovesAlsoItsItems() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		insertWishlist(wl);
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		wl.getItems().add(item);
		item.setWishlist(wl);
		insertItem(item);
		assertThat(findItem(item)).isNotNull();
		wDao.remove(wl);
		assertThat(findItem(item)).isNull();
	}
	
	private void insertItem(Item item) {
		String nativeQuery = "INSERT INTO Item (name, desc, price, wishlist_name) VALUES (?, ?, ?, ?)";
		EntityManager em = wDao.getEmf().createEntityManager();
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
	
	private Item findItem(Item item) {
		EntityManager em = wDao.getEmf().createEntityManager();
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
