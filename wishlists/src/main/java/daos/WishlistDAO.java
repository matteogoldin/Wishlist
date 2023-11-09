package daos;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TransactionRequiredException;
import model.Wishlist;

public class WishlistDAO implements BaseDAO<Wishlist, String>{
	private static final Logger LOGGER = LogManager.getLogger(WishlistDAO.class);
	
	@Override
	public Wishlist findById(String Id) {
		try {
			// TODO
		} catch(NoResultException e) {
			LOGGER.info(() -> String.format("No Wishlist found with Id: %s", Id));
			return null;
		} catch(Exception e) {
			//TODO
		}
		return null;
	}
	
	@Override
	public void add(Wishlist wl) throws EntityExistsException, RuntimeException{
		try {
			
			//for each transaction is necessary a rollback if an exception occurs
		} catch (EntityExistsException e) {
			LOGGER.error("Trying to insert a Wishlist instance that already exists");
			throw e;
		} catch (RuntimeException e) {
			LOGGER.error("Errors executing the transaction");
			throw e;
		}
		
	}
	
	@Override
	public void remove(Wishlist wl) {
		try {
			
		} catch (IllegalArgumentException e) {
			LOGGER.error("The object to remove is not persisted");
			throw e;
		}
		
	}

	@Override
	public List<Wishlist> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
