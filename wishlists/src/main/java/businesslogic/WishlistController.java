package businesslogic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import daos.ItemDAO;
import daos.WishlistDAO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.RollbackException;
import model.Item;
import model.Wishlist;
import view.WishlistView;

public class WishlistController {
	private WishlistView view;
	private WishlistDAO wlDao;
	private ItemDAO itemDao;
	private List<Wishlist> wlList;

	private static final Logger LOGGER = LogManager.getLogger(WishlistController.class);
	private static final String ERROR_STRING = "Error: please try again";

	public WishlistController(WishlistView view, WishlistDAO wlDao, ItemDAO itemDao) {
		this.view = view;
		this.wlDao = wlDao;
		this.itemDao = itemDao;
		wlList = new ArrayList<>();
	}

	public void addWishlist(Wishlist wl) {
		try {
			wlDao.add(wl);
			wlList.add(wl);
			LOGGER.info(() -> String.format("Wishlist %s correctly inserted", wl.getName()));
		} catch (RollbackException e) {
			view.showError("Wishlist " + wl.getName() + " already exists");
		} catch (RuntimeException e) {
			view.showError(ERROR_STRING);
		}
		view.showAllWLs(wlList);
	}

	public void removeWishlist(Wishlist wl) {
		try {
			wlDao.remove(wl);
			wlList.remove(wl);
			LOGGER.info(() -> String.format("Wishlist %s correctly removed", wl.getName()));
		} catch (RuntimeException e) {
			view.showError("Error: please try again");
		}
		view.showAllWLs(wlList);
	}

	public void addItemToWishlist(Item item, Wishlist wl) {
		item.setWishlist(wl);
		try {
			itemDao.add(item);
			wl.getItems().add(item);
			LOGGER.info(() -> String.format("Item %s correctly added to Wishlist %s", item.getName(), wl.getName()));
		} catch (EntityExistsException e) {
			item.setWishlist(null);
			view.showError("Item " + item.getName() + " already exists in Wishlist " + wl.getName());
		} catch (IllegalArgumentException e) {
			item.setWishlist(null);
			view.showError("Wishlist " + wl.getName() + " doesn't exist anymore");
			wlList = wlDao.getAll();
			view.showAllWLs(wlList);
			view.showAllItems(null);
			return;
		} catch (RuntimeException e) {
			view.showError("Error: please try again");
		}
		view.showAllWLs(wlList);
		view.showAllItems(wl);
	}

	public void removeItemFromWishlist(Item item, Wishlist wl) {
		try {
			itemDao.remove(item);
			wl.getItems().remove(item);
			LOGGER.info(() -> String.format("Item %s correctly removed from Wishlist %s", item.getName(), wl.getName()));
		} catch (IllegalArgumentException e) {
			view.showError("Error: please try again");
			if (!wlList.contains(wl)) {
				wlList = wlDao.getAll();
				view.showAllWLs(wlList);
				view.showAllItems(null);
				return;
			}
		} catch (RuntimeException e) {
			view.showError("Error: please try again");
		}
		view.showAllWLs(wlList);
		view.showAllItems(wl);
	}

	public void refreshWishlists() {
		try {
			wlList = wlDao.getAll();
			view.showAllWLs(wlList);
		} catch (RuntimeException e) {
			view.showError("Error: please try again");
		}
	}

	public void refreshItems(Wishlist wl) {
		try {
			wl.setItems(itemDao.getAllWLItems(wl.getName()));
			view.showAllItems(wl);
		} catch (RuntimeException e) {
			view.showError("Error: please try again");
		}
	}

	List<Wishlist> getWlList() {
		return wlList;
	}

}
