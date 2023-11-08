package businesslogic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

import daos.WishlistDAO;
import jakarta.persistence.EntityExistsException;
import model.Wishlist;
import view.WishlistView;

public class WishlistController {
	private WishlistView view;
	private WishlistDAO dao;
	private List<Wishlist> wlList;
	
	private static final Logger LOGGER = LogManager.getLogger(WishlistController.class);
	
	public WishlistController(WishlistView view, WishlistDAO dao) {
		this.view = view;
		this.dao = dao;
		wlList = new ArrayList<>();
	}

	public void addWishlist(Wishlist wl) {
		try {
			dao.addWL(wl);
			wlList.add(wl);
			view.refreshWlList();
			LOGGER.info("Wishlist %s correctly inserted", wl.getName());
		} catch (EntityExistsException e) {
			view.showError("Wishlist " + wl.getName() + " already exists");
		} catch (RuntimeException e) {
			view.showError("Error: please try again");
		}
	}

	public void removeWishlist(Wishlist wl) {
		try {
			dao.removeWL(wl.getName());
			wlList.remove(wl);
			view.refreshWlList();
			LOGGER.info("Wishlist %s correctly removed", wl.getName());
		} catch (IllegalArgumentException e) {
			view.showError("Wishlist " + wl.getName() + " doesn't exist or has been already removed");
			if(wlList.contains(wl)) wlList.remove(wl);
		}
		
	}
	
	public List<Wishlist> getWlList() {
		return wlList;
	}
}
