package daos;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
	
	

}
