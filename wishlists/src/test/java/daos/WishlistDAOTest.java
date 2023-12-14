package daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.RollbackException;
import model.Item;
import model.Wishlist;
import utils.SQLClient;

class WishlistDAOTest {
	private WishlistDAO wDao;
	private SQLClient client;
	private String persistentUnit = "wishlists-pu-test";

	@BeforeEach
	void setup() {
		wDao = new WishlistDAO(persistentUnit);
		client = new SQLClient(persistentUnit);
		client.initEmptyDB();
	}

	@Test
	void getAllWhenDatabaseIsEmptyReturnEmptyList() {
		assertThat(wDao.getAll()).isEmpty();
	}

	@Test
	void getAllWhenDatabaseIsNotEmptyReturnANotEmptyList() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		client.insertWishlist(wl.getName(), wl.getDesc());
		assertThat(wDao.getAll()).hasSize(1);
	}

	@Test
	void WishlistCorrectlyInserted() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		wDao.add(wl);
		Wishlist wl_dup = client.findWishlist(wl.getName());
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
		client.insertWishlist(wl.getName(), wl.getDesc());
		wDao.remove(wl);
		assertThat(client.findWishlist(wl.getName())).isNull();
	}

	@Test
	void removingANonPersistedEntity() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		assertDoesNotThrow(() -> wDao.remove(wl));
	}

	@Test
	void removingAWishlistRemovesAlsoItsItems() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		client.insertWishlist(wl.getName(), wl.getDesc());
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		wl.getItems().add(item);
		client.mergeWishlist(wl);
		assertThat(client.findItem(wl.getName(), item.getName())).isNotNull();
		wDao.remove(wl);
		assertThat(client.findItem(wl.getName(), item.getName())).isNull();
	}

	@Test
	void getAllCorrectlyRetrieveAllTheWishlists() {
		Wishlist wl1 = new Wishlist("Birthday", "My birthday gifts");
		Wishlist wl2 = new Wishlist("Christmas", "Gift ideas");
		client.insertWishlist(wl1.getName(), wl1.getDesc());
		client.insertWishlist(wl2.getName(), wl2.getDesc());
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
		client.insertWishlist(wl1.getName(), wl1.getDesc());
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
		client.insertWishlist(wl.getName(), wl.getDesc());
		assertThat(client.findItem(wl.getName(), item.getDesc())).isNull();
		wDao.addItem(wl, item);
		assertThat(client.findItem(wl.getName(), item.getName())).isEqualTo(item);
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
		client.insertWishlist(wl.getName(), wl.getDesc());
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		wl.getItems().add(item);
		client.insertItem(wl.getName(), item.getName(), item.getDesc(), item.getPrice());
		assertThat(client.findItem(wl.getName(), item.getName())).isNotNull();
		wDao.removeItem(wl, item);
		assertThat(client.findItem(wl.getName(), item.getName())).isNull();
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
		client.insertWishlist(wl.getName(), wl.getDesc());
		Item item1 = new Item("Phone", "Samsung Galaxy A52", 300);
		Item item2 = new Item("Wallet", "D&G", 100);
		client.insertItem(wl.getName(), item1.getName(), item1.getDesc(), item1.getPrice());
		client.insertItem(wl.getName(), item2.getName(), item2.getDesc(), item2.getPrice());
		List<Item> itList = wDao.getAllWlItems(wl);
		assertThat(itList).containsExactly(item1, item2);
	}

	@Test
	void getAllWlItemsOnANonPersistedWLReturnsAnEmptyList() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		assertThat(wDao.getAllWlItems(wl)).isEmpty();
	}
}
