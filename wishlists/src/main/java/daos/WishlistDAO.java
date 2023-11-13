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

public class WishlistDAO extends BaseDAO<Wishlist, String> {
	private static final Logger LOGGER_WD = LogManager.getLogger(WishlistDAO.class);
	
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
			LOGGER_WD.info(() -> String.format("No Wishlist found with Id: %s", id));
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
}
