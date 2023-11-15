package daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;
import model.Item;
import model.Wishlist;

class ItemDAOTest {
	private ItemDAO itDao;
	private EntityManagerFactory emf;
	
	@BeforeEach
	void setup() {
		itDao = new ItemDAO("wishlists-pu-test");
		emf = itDao.getEmf();
		DAOTestsSQLQueries.initEmptyDB(emf);
	}
	
	@Test
	void getAllWlItemsReturnsAllTheItemsAssociatedToAList() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		Item item1 = new Item("Phone", "Samsung Galaxy A52", 300);
		Item item2 = new Item("Wallet", "D&G", 100);
		wl.getItems().add(item1);
		wl.getItems().add(item2);
		item1.setWishlist(wl);
		item2.setWishlist(wl);
		DAOTestsSQLQueries.mergeWishlist(wl, emf);
		List<Item> itList = itDao.getAllWLItems(wl);
		assertAll(
				() -> assertThat(itList).contains(item1, item2),
				() -> assertThat(itList).hasSize(2));
	}
	
	@Test
	void getAllWlItemsOnANonPersistedWLReturnsAnEmptyList() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		assertThat(itDao.getAllWLItems(wl)).isEmpty();
	}
}
