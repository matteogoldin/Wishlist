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
import model.Wishlist;

public class WishlistDAO implements BaseDAO<Wishlist, String> {
	private EntityManagerFactory emf;
	private EntityManager em;

	private static final Logger LOGGER = LogManager.getLogger(WishlistDAO.class);

	public WishlistDAO() {
		emf = Persistence.createEntityManagerFactory("wishlists-pu-test");
	}

	@Override
	public Wishlist findById(String id) {
		Wishlist result = null;
		openEntityManager();
		try {
			result = em.createQuery("SELECT wl FROM Wishlist wl WHERE wl.name = :id", Wishlist.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			LOGGER.info(() -> String.format("No Wishlist found with Id: %s", id));
		} finally {
			em.close();
		}
		return result;
	}

	@Override
	public void add(Wishlist wl) throws EntityExistsException, RuntimeException {
		executeInsideTransaction(entitymanager -> entitymanager.persist(wl));
	}

	@Override
	public void remove(Wishlist wl) {
		executeInsideTransaction(entitymanager -> entitymanager.remove(entitymanager.merge(wl)));
	}

	@Override
	public List<Wishlist> getAll() {
		List<Wishlist> result;
		openEntityManager();
		result = em.createQuery("SELECT wl FROM Wishlist wl", Wishlist.class).getResultList();
		em.close();
		return result;
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
		} catch (RuntimeException e) {
			transactionRollbackHandling(transaction, "Errors executing the transaction");
			throw e;
		} finally {
			em.close();
		}
	}

	private void transactionRollbackHandling(EntityTransaction transaction, String errorString) {
		transaction.rollback();
		LOGGER.error(errorString);
	}

	EntityManagerFactory getEmf() {
		return emf;
	}

}
