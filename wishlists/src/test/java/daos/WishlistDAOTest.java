package daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
		DAOTestsSQLQueries.initEmptyDB(emf);
	}

	@Test
	void getAllWhenDatabaseIsEmptyReturnEmptyList() {
		assertThat(wDao.getAll()).isEmpty();
	}

	@Test
	void getAllWhenDatabaseIsNotEmptyReturnANotEmptyList() {
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

	@Test
	void mergeUpdateItemsListWhenAnItemIsAdded() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		wl.getItems().add(item);
		item.setWishlist(wl);
		assertThat(DAOTestsSQLQueries.findItem(item, emf)).isNull();
		wDao.merge(wl);
		assertThat(DAOTestsSQLQueries.findItem(item, emf)).isNotNull();
	}

	@Test
	void mergeUpdateItemsListWhenAnItemIsRemoved() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		wl.getItems().add(item);
		item.setWishlist(wl);
		wDao.merge(wl);
		assertThat(DAOTestsSQLQueries.findItem(item, emf)).isNotNull();
		wl.getItems().remove(item);
		wDao.merge(wl);
		assertThat(DAOTestsSQLQueries.findItem(item, emf)).isNull();
	}

	@Test
	void mergeOnAWLNotPersisted() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		assertThatThrownBy(() -> wDao.merge(wl)).isInstanceOf(RuntimeException.class);
		assertThat(DAOTestsSQLQueries.findWishlist(wl, emf)).isNull();
	}

	@Test
	void addTwoItemToTheSameWLRaiseAnException() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		Item item1 = new Item("Phone", "Samsung Galaxy A52", 300);
		wl.getItems().add(item1);
		item1.setWishlist(wl);
		wDao.merge(wl);
		assertThat(DAOTestsSQLQueries.findItem(item1, emf)).isNotNull();
		Item item2 = new Item("Phone", "Iphone 11", 700);
		wl.getItems().add(item2);
		item2.setWishlist(wl);
		assertAll(
				() -> assertThatThrownBy(() -> wDao.merge(wl)).isInstanceOf(RuntimeException.class),
				() -> assertThat(DAOTestsSQLQueries.findAllItemsFromAWL(wl, emf)).containsOnly(item1));
	}

	@Test
	void addTwoItemToDifferentWLDoesntRaiseAnException() {
		Wishlist wl1 = new Wishlist("Christmas", "Christmas gifts");
		DAOTestsSQLQueries.insertWishlist(wl1, emf);
		Wishlist wl2 = new Wishlist("Birthday", "My birthday gifts");
		DAOTestsSQLQueries.insertWishlist(wl2, emf);
		Item item1 = new Item("Phone", "Samsung Galaxy A52", 300);
		wl1.getItems().add(item1);
		item1.setWishlist(wl1);
		wDao.merge(wl1);
		Item item2 = new Item("Phone", "Iphone 11", 700);
		wl2.getItems().add(item2);
		item2.setWishlist(wl2);
		wDao.merge(wl2);
		assertAll(
				() -> assertThat(DAOTestsSQLQueries.findAllItemsFromAWL(wl1, emf)).containsOnly(item1),
				() -> assertThat(DAOTestsSQLQueries.findAllItemsFromAWL(wl2, emf)).containsOnly(item2));
	}

}
