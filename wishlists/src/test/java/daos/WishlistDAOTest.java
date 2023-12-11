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
import utils.DAOTestsSQLQueries;

class WishlistDAOTest {
	private WishlistDAO wDao;
	private EntityManagerFactory emf;

	@BeforeEach
	void setup() {
		wDao = new WishlistDAO("wishlists-pu-test");
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
		DAOTestsSQLQueries.mergeWishlist(wl, emf);
		assertThat(DAOTestsSQLQueries.findItem(wl, item, emf)).isNotNull();
		wDao.remove(wl);
		assertThat(DAOTestsSQLQueries.findItem(wl, item, emf)).isNull();
	}

	@Test
	void getAllCorrectlyRetrieveAllTheWishlists() {
		Wishlist wl1 = new Wishlist("Birthday", "My birthday gifts");
		Wishlist wl2 = new Wishlist("Christmas", "Gift ideas");
		DAOTestsSQLQueries.insertWishlist(wl1, emf);
		DAOTestsSQLQueries.insertWishlist(wl2, emf);
		List<Wishlist> wlList = wDao.getAll();
		assertAll(() -> assertThat(wlList).hasSize(2), () -> assertThat(wlList).contains(wl1),
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
	void addItemAddAnItemToAWishlist() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		assertThat(DAOTestsSQLQueries.findItem(wl, item, emf)).isNull();
		wDao.addItem(wl, item);
		assertThat(DAOTestsSQLQueries.findItem(wl, item, emf)).isEqualTo(item);
	}

	@Test
	void addItemCanRaiseException() {
		Wishlist wl = null;
		Item item = null;
		wDao.setEmf(null);
		assertThatThrownBy(() -> wDao.addItem(wl, item)).isInstanceOf(RuntimeException.class);
	}

	@Test
	void removeItemRemovesItemFromTheWishlist() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		wl.getItems().add(item);
		DAOTestsSQLQueries.insertItem(wl, item, emf);
		assertThat(DAOTestsSQLQueries.findItem(wl, item, emf)).isNotNull();
		wDao.removeItem(wl, item);
		assertThat(DAOTestsSQLQueries.findItem(wl, item, emf)).isNull();
	}
	
	@Test
	void removeItemCanRaiseException() {
		Wishlist wl = null;
		Item item = null;
		wDao.setEmf(null);
		assertThatThrownBy(() -> wDao.removeItem(wl, item)).isInstanceOf(RuntimeException.class);
	}

	@Test
	void getAllWlItemsReturnsAllTheItemsAssociatedToAList() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		Item item1 = new Item("Phone", "Samsung Galaxy A52", 300);
		Item item2 = new Item("Wallet", "D&G", 100);
		DAOTestsSQLQueries.insertItem(wl, item1, emf);
		DAOTestsSQLQueries.insertItem(wl, item2, emf);
		List<Item> itList = wDao.getAllWlItems(wl);
		assertThat(itList).containsExactly(item1, item2);
	}

	@Test
	void getAllWlItemsOnANonPersistedWLReturnsAnEmptyList() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		assertThat(wDao.getAllWlItems(wl)).isEmpty();
	}
}
