package daos;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import model.Item;
import model.ItemPK;

public class ItemDAO implements BaseDAO<Item,ItemPK> {
	
	private static final Logger LOGGER = LogManager.getLogger(ItemDAO.class);
	
	@Override
	public Item findById(ItemPK id) {
		// TODO
		return null;
	}

	@Override
	public List<Item> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Item item) throws EntityExistsException{
		try {
			
		} catch (EntityExistsException e) {
			LOGGER.error("Trying to insert an Item instance that already exists");
			throw e;
		} catch(IllegalStateException e){
			LOGGER.error("Trying to insert an Item instance in a Wishlist that is not persisted");
			throw e;
		} catch (RuntimeException e) {
			LOGGER.error("Errors executing the transaction");
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

}
