package daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.RollbackException;
import model.Item;
import model.Wishlist;

class WishlistDAOTest {
	private WishlistDAO wDao;
	private EntityManagerFactory emf;

	@BeforeEach
	void setup() {
		wDao = new WishlistDAO();
		emf = wDao.getEmf();
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
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		assertThat(wDao.getAll()).hasSize(1);
	}

	@Test
	void WishlistCorrectlyInserted() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		wDao.add(wl);
		Wishlist wl_dup = DAOTestsSQLQueries.findWishlist(wl, emf);
		assertThat(wl).isEqualTo(wl_dup);
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
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		wDao.remove(wl);
		assertThat(DAOTestsSQLQueries.findWishlist(wl, emf)).isNull();
	}

	@Test
	void removingANonPersistedEntity() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		assertDoesNotThrow(() -> wDao.remove(wl));
	}

	@Test
	void removingAWishlistRemovesAlsoItsItems() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		wl.getItems().add(item);
		item.setWishlist(wl);
		DAOTestsSQLQueries.insertItem(item, emf);
		assertThat(DAOTestsSQLQueries.findItem(item, emf)).isNotNull();
		wDao.remove(wl);
		assertThat(DAOTestsSQLQueries.findItem(item, emf)).isNull();
	}

	@Test
	void getAllCorrectlyRetrieveAllTheWishlists() {
		Wishlist wl1 = new Wishlist("Birthday", "My birthday gifts");
		Wishlist wl2 = new Wishlist("Christmas", "Gift ideas");
		DAOTestsSQLQueries.insertWishlist(wl1, emf);
		DAOTestsSQLQueries.insertWishlist(wl2, emf);
		List<Wishlist> wlList = wDao.getAll();
		assertAll(
			() -> assertThat(wlList).hasSize(2),
			() -> assertThat(wlList).contains(wl1),
			() -> assertThat(wlList).contains(wl2));
	}

	@Test
	void getAllWith0WishlistsInTheDBReturnAnEmptyList() {
		assertThat(wDao.getAll()).isEmpty();
	}

	@Test
	void findByIdCorrectlyRetrieveAWishlist() {
		Wishlist wl1 = new Wishlist("Birthday", "My birthday gifts");
		DAOTestsSQLQueries.insertWishlist(wl1, emf);
		assertThat(wDao.findById(wl1.getName())).isEqualTo(wl1);
	}

	@Test
	void findByIdReturnNullIfWishlistIsNotPersisted() {
		assertThat(wDao.findById("Birthday")).isNull();
	}
}
