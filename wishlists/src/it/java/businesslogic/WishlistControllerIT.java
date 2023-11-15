package businesslogic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
		wlDao = new WishlistDAO();
		itemDao = new ItemDAO();
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
		assertThat(controller.getWlList()).containsExactly(wl);
		controller.removeWishlist(wl);
		assertThat(controller.getWlList()).isEmpty();
		verify(view, times(2)).showAllWLs(controller.getWlList());
	}

}
