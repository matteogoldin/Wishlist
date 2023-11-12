package daos;

import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.RollbackException;
import jakarta.persistence.TransactionRequiredException;
import model.Wishlist;

public class WishlistDAO implements BaseDAO<Wishlist, String> {
	private EntityManagerFactory emf;
	private EntityManager em;

	private static final Logger LOGGER = LogManager.getLogger(WishlistDAO.class);

	public WishlistDAO() {
		emf = Persistence.createEntityManagerFactory("wishlists-pu-test");
	}

	@Override
	public Wishlist findById(String Id) {
		try {
			// TODO
		} catch (NoResultException e) {
			LOGGER.info(() -> String.format("No Wishlist found with Id: %s", Id));
			return null;
		} catch (Exception e) {
			// TODO
		}
		return null;
	}

	@Override
	public void add(Wishlist wl) throws EntityExistsException, RuntimeException {
		executeInsideTransaction(entitymanager -> entitymanager.persist(wl));
	}

	@Override
	public void remove(Wishlist wl) {
		executeInsideTransaction(entitymanager -> em.remove(em.contains(wl) ? wl : entitymanager.merge(wl)));
	}

	@Override
	public List<Wishlist> getAll() {
		List<Wishlist> result;
		openEntityManager();
		result = em.createQuery("SELECT wl FROM Wishlist wl", Wishlist.class).getResultList();
		closeEntityManager();
		return result;
	}

	private void closeEntityManager() {
		try {
			em.close();
		} catch (RuntimeException e) {
			LOGGER.error("Close entity manager fails");
			throw e;
		}
	}

	private void openEntityManager() {
		try {
			em = emf.createEntityManager();
		} catch (RuntimeException e) {
			LOGGER.error("Create entity manager fails");
			throw e;
		}
	}

	private void executeInsideTransaction(Consumer<EntityManager> action) {
		openEntityManager();
		EntityTransaction transaction = null;
		try {
			transaction = em.getTransaction();
			transaction.begin();
			action.accept(em);
			transaction.commit();
		} catch (RollbackException e) {
			transactionRollbackHandling(transaction, "Trying to insert a Wishlist instance that already exists");
			throw e;
		} catch (RuntimeException e) {
			transactionRollbackHandling(transaction, "Errors executing the transaction");
			throw e;
		} finally {
			closeEntityManager();
		}
	}

	private void transactionRollbackHandling(EntityTransaction transaction, String errorString) {
		if (transaction != null && transaction.isActive()) {
			transaction.rollback();
		}
		LOGGER.error(errorString);
	}

	EntityManager getEm() {
		return em;
	}

	EntityManagerFactory getEmf() {
		return emf;
	}

}
