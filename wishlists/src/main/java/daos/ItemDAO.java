package daos;

import org.apache.logging.log4j.LogManager;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import model.Item;
import model.ItemPK;
import model.Wishlist;

public class ItemDAO extends BaseDAO<Item,ItemPK> {
	private static final Logger LOGGER_ID = LogManager.getLogger(ItemDAO.class);
	
	public ItemDAO() {
		emf = Persistence.createEntityManagerFactory("wishlists-pu-test");
	}
	
	@Override
	public Item findById(ItemPK id) {
		// TODO
		return null;
	}

	@Override
	public List<Item> getAll() {
		List<Item> result;
		openEntityManager();
		result = em.createQuery("SELECT it FROM Item it", Item.class).getResultList();
		em.close();
		return result;
	}

	@Override
	public void add(Item item) throws EntityExistsException{
		try {
			
		} catch (EntityExistsException e) {
			LOGGER_ID.error("Trying to insert an Item instance that already exists");
			throw e;
		} catch(IllegalStateException e){
			LOGGER_ID.error("Trying to insert an Item instance in a Wishlist that is not persisted");
			throw e;
		} catch (RuntimeException e) {
			LOGGER_ID.error("Errors executing the transaction");
			throw e;
		}
	}

	@Override
	public void remove(Item item) {
		// TODO Auto-generated method stub
		
	}

	public List<Item> getAllWLItems(String wlId) {
		// TODO Auto-generated method stub
		return null;
	}

	EntityManagerFactory getEmf() {
		return emf;
	}

}
