package daos;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Persistence;
import model.Item;
import model.ItemPK;
import model.Wishlist;

public class ItemDAO extends BaseDAO<Item,ItemPK> {
	private static final Logger LOGGER_ID = LogManager.getLogger(ItemDAO.class);

	public ItemDAO() {
		emf = Persistence.createEntityManagerFactory("wishlists-pu-test");
	}

	public List<Item> getAllWLItems(Wishlist wl) {
		List<Item> result;
		openEntityManager();
		result = em.createQuery("SELECT it FROM Item it WHERE it.wishlist = :wl", Item.class)
				.setParameter("wl", wl)
				.getResultList();
		LOGGER_ID.info("Items correctly retrieved");
		em.close();
		return result;
	}
}
