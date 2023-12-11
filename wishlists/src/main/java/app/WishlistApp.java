package app;

import java.awt.EventQueue;

import businesslogic.WishlistController;
import daos.WishlistDAO;
import view.WishlistSwingView;

public class WishlistApp {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				WishlistSwingView view = new WishlistSwingView();
				WishlistDAO dao = new WishlistDAO("wishlists-pu");
				WishlistController controller = new WishlistController(view, dao);
				view.setController(controller);
				view.setVisible(true);
				controller.refreshWishlists();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
