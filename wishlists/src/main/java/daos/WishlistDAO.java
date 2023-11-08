package daos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.TransactionRequiredException;
import model.Wishlist;

public class WishlistDAO{
	private static final Logger LOGGER = LogManager.getLogger(WishlistDAO.class);

	public Wishlist findById(String anyString) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addWL(Wishlist wl) throws EntityExistsException, RuntimeException{
		try {
			
			//for each transaction is necessary a rollback if an exception occurs
		} catch (IllegalStateException e) {
			LOGGER.error("Problems related with the transaction");
			throw new RuntimeException();
		} catch (IllegalArgumentException e) {
			LOGGER.error("The instance to persit is not an entity");
			throw new RuntimeException();
		} catch (TransactionRequiredException e) {
			LOGGER.error("A transaction is required to persist the instance");
			throw new RuntimeException();
		} catch (EntityExistsException e) {
			LOGGER.error("Trying to insert a Wishlist instance that already exists");
			throw e;
		}
		
	}

	public void removeWL(String Id) {
		try {
			
		} catch (IllegalArgumentException e) {
			LOGGER.error("The object to remove is not persisted");
			throw e;
		}
		
	}

}
