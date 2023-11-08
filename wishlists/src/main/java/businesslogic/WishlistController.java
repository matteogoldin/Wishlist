package businesslogic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

import daos.ItemDAO;
import daos.WishlistDAO;
import jakarta.persistence.EntityExistsException;
import model.Item;
import model.Wishlist;
import view.WishlistView;

public class WishlistController {
	private WishlistView view;
	private WishlistDAO wlDao;
	private ItemDAO itemDao;
	private List<Wishlist> wlList;
	
	private static final Logger LOGGER = LogManager.getLogger(WishlistController.class);
	
	public WishlistController(WishlistView view, WishlistDAO wlDao, ItemDAO itemDao) {
		this.view = view;
		this.wlDao = wlDao;
		this.itemDao = itemDao;
		wlList = new ArrayList<>();
	}

	public void addWishlist(Wishlist wl) {
		try {
			wlDao.add(wl);
			LOGGER.info(() -> String.format("Wishlist %s correctly inserted", wl.getName()));
		} catch (EntityExistsException e) {
			view.showError("Wishlist " + wl.getName() + " already exists");
		} catch (RuntimeException e) {
			view.showError("Error: please try again");
		} 
		refreshWlList();
	}

	public void removeWishlist(Wishlist wl) {
		try {
			wlDao.remove(wl.getName());
			LOGGER.info(() -> String.format("Wishlist %s correctly removed", wl.getName()));
		} catch (IllegalArgumentException e) {
			view.showError("Wishlist " + wl.getName() + " doesn't exist or has been already removed");
		}
		refreshWlList();
		
	}
	
	public void addItemToWishlist(Item item, Wishlist wl) {
		item.setWishlist(wl);
		try {
			itemDao.add(item);
			LOGGER.info(() -> String.format("Item %s correctly added to Wishlist %s", item.getName(), wl.getName()));
		} catch (Exception e) {
			item.setWishlist(null);
		}
		refreshWlList();
		refreshItemList(wl);
	}
	
	public void refreshWlList() {
		wlList = wlDao.getAll();
		view.showAllWLs(wlList);
	}
	
	public void refreshItemList(Wishlist wl) {
		wl.setItems(itemDao.getAllWLItems(wl.getName()));
		view.showAllItems(wl);
	}
	
	List<Wishlist> getWlList() {
		return wlList;
	}
	
}
