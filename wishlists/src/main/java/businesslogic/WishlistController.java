package businesslogic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import daos.ItemDAO;
import daos.WishlistDAO;
import model.Item;
import model.Wishlist;
import view.WishlistView;

public class WishlistController {
	private WishlistView view;
	private WishlistDAO wlDao;
	private ItemDAO itemDao;
	private List<Wishlist> wlList;

	private static final Logger LOGGER = LogManager.getLogger(WishlistController.class);
	private static final String ERROR_STRING = "Error: please try again or try to refresh";

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
			view.showError(ERROR_STRING);
		}
		view.showAllWLs(wlList);
	}

	public void addItemToWishlist(Item item, Wishlist wl) {
		item.setWishlist(wl);
		try {
			wl.getItems().add(item);
			wlDao.merge(wl);
			LOGGER.info(() -> String.format("Item %s correctly added to Wishlist %s", item.getName(), wl.getName()));
		} catch (RuntimeException e) {
			item.setWishlist(null);
			wl.getItems().remove(item);
			view.showError(ERROR_STRING);
		}
		view.showAllWLs(wlList);
		view.showAllItems(wl);
	}

	public void removeItemFromWishlist(Item item, Wishlist wl) {
		try {
			wl.getItems().remove(item);
			wlDao.merge(wl);
			LOGGER.info(() -> String.format("Item %s correctly removed from Wishlist %s", item.getName(), wl.getName()));
		} catch (RuntimeException e) {
			wl.getItems().add(item);
			view.showError(ERROR_STRING);
		}
		view.showAllWLs(wlList);
		view.showAllItems(wl);
	}

	public void refreshWishlists() {
		try {
			wlList = wlDao.getAll();
			view.showAllWLs(wlList);
		} catch (RuntimeException e) {
			view.showError(ERROR_STRING);
		}
	}

	public void refreshItems(Wishlist wl) {
		try {
			wl.setItems(itemDao.getAllWLItems(wl));
			view.showAllItems(wl);
		} catch (RuntimeException e) {
			view.showError(ERROR_STRING);
		}
	}

	List<Wishlist> getWlList() {
		return wlList;
	}

}
