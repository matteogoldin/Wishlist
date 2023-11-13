package daos;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import model.Item;
import model.Wishlist;

class ItemDAOTest {
	private ItemDAO itDao;
	private EntityManagerFactory emf;
	
	@BeforeEach
	void setup() {
		itDao = new ItemDAO();
		emf = itDao.getEmf();
		DAOTestsSQLQueries.initEmptyDB(emf);
	}

	@Test
	void getAllItemWhenDBIsEmpty() {
		assertThat(itDao.getAll()).isEmpty();
	}
	
	@Test
	void getAllWhenDatabaseIsNotEmptyReturnANotEmptyList() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		DAOTestsSQLQueries.insertWishlist(wl, emf);
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		wl.getItems().add(item);
		item.setWishlist(wl);
		DAOTestsSQLQueries.insertItem(item, emf);
		assertThat(itDao.getAll().get(0).getName()).isEqualTo(item.getName());
	}
	

}
