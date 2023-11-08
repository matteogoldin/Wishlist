package daos;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.NoResultException;
import model.Item;

public class ItemDAO implements BaseDAO<Item> {
	
	private static final Logger LOGGER = LogManager.getLogger(ItemDAO.class);
	
	@Override
	public Item findById(String id) {
		// TODO
		return null;
	}

	@Override
	public List<Item> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Item object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String id) {
		// TODO Auto-generated method stub
		
	}

	public List<Item> getAllWLItems(String wlId) {
		// TODO Auto-generated method stub
		return null;
	}

}
