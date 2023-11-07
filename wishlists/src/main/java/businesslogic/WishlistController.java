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
			view.refreshWL();
		} catch (EntityExistsException e) {
			LOGGER.error("Wishlist " + wl.getName() + "already exist");
			view.showError("Wishlist " + wl.getName() + "already exist");
		}
	}

	public List<Wishlist> getWlList() {
		return wlList;
	}

}
