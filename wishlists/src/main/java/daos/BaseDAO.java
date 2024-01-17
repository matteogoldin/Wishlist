package daos;

import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public abstract class BaseDAO<T>{
	private final Class<T> tClass;
	protected EntityManager em;
	protected EntityManagerFactory emf;

	private static final Logger LOGGER_BD = LogManager.getLogger(BaseDAO.class);
	
	protected BaseDAO(Class<T> tClass) {
		this.tClass = tClass;
	}
	
	public T findById(String id) {
		return em.find(tClass, id);
	}
	
	public void add(T t) {
		executeInsideTransaction(entitymanager -> entitymanager.persist(t));
	}
	
	public void remove(T t) {
		executeInsideTransaction(entitymanager -> entitymanager.remove(entitymanager.merge(t)));
	}
	public abstract List<T> getAll();


	void openEntityManager() {
		try {
			em = emf.createEntityManager();
		} catch (RuntimeException e) {
			LOGGER_BD.error("Create entity manager fails");
			throw e;
		}
	}

	protected void executeInsideTransaction(Consumer<EntityManager> action) {
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

	protected void transactionRollbackHandling(EntityTransaction transaction, String errorString) {
		transaction.rollback();
		LOGGER_BD.error(errorString);
	}

	EntityManagerFactory getEmf() {
		return emf;
	}
}
