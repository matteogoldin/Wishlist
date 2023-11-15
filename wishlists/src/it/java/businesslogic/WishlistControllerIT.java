package businesslogic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import daos.ItemDAO;
import daos.WishlistDAO;
import model.Item;
import model.Wishlist;
import view.WishlistView;

@Testcontainers
class WishlistControllerIT {
	@Container
	private MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33")).withLogConsumer(new Slf4jLogConsumer(logger));

	@Mock
	private WishlistView view;

	private WishlistDAO wlDao;

	private ItemDAO itemDao;

	private WishlistController controller;

	private AutoCloseable closeable;
	private static Logger logger = LoggerFactory.getLogger(WishlistControllerIT.class);

	@BeforeEach
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		wlDao = new WishlistDAO("wishlists-pu");
		itemDao = new ItemDAO("wishlists-pu");
		for(Wishlist wl : wlDao.getAll()) wlDao.remove(wl);
		controller = new WishlistController(view, wlDao, itemDao);
	}

	@AfterEach
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	void ContainerIsRunningTest() {
		assertThat(mysqlContainer.isRunning()).isTrue();
	}

	@Test
	void wlCorrectlyAdded() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		controller.addWishlist(wl);
		assertThat(controller.getWlList()).containsExactly(wl);
		verify(view).showAllWLs(controller.getWlList());
	}

	@Test
	void wlCorrectlyRemoved() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		controller.addWishlist(wl);
		controller.removeWishlist(wl);
		assertThat(controller.getWlList()).isEmpty();
		verify(view, times(2)).showAllWLs(controller.getWlList());
	}

	@Test
	void itemCorrectlyAddedToAWL() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		controller.addWishlist(wl);
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		controller.addItemToWishlist(item, wl);
		assertThat(itemDao.getAllWLItems(wl)).containsExactly(item);
	}

	@Test
	void itemCorrectlyRemovedFromAWL() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		controller.addWishlist(wl);
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		controller.addItemToWishlist(item, wl);
		assertThat(itemDao.getAllWLItems(wl)).containsExactly(item);
		controller.removeItemFromWishlist(item, wl);
		assertThat(itemDao.getAllWLItems(wl)).isEmpty();
	}

	@Test
	void refreshWishlistsCorrectlyRetrieveAllTheWishlistsPersisted() {
		Wishlist wl1 = new Wishlist("Birthday", "My birthday gifts");
		Wishlist wl2 = new Wishlist("Christmas", "Gift ideas");
		wlDao.add(wl1);
		wlDao.add(wl2);
		assertThat(controller.getWlList()).isEmpty();
		controller.refreshWishlists();
		assertThat(controller.getWlList()).containsExactly(wl1, wl2);
	}

	@Test
	void refreshWishlistItemsCorrectlyRetrieveAllTheItems() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		controller.addWishlist(wl);
		Item item1 = new Item("Phone", "Samsung Galaxy A52", 300);
		Item item2 = new Item("Wallet", "D&G", 200);
		controller.addItemToWishlist(item1, wl);
		controller.addItemToWishlist(item2, wl);
		wl.getItems().clear();
		controller.refreshItems(wl);
		assertThat(wl.getItems()).containsExactly(item1, item2);
	}

	@Test
	void addingAnItemToANonPersistedWLShowError() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		controller.addItemToWishlist(item, wl);
		verify(view).showError(anyString());
	}
}
