package app;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import businesslogic.WishlistController;
import daos.WishlistDAO;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import view.WishlistSwingView;

@Command(mixinStandardHelpOptions = true)
public class WishlistApp implements Callable<Void>{
	@Option(names = { "--persistence-unit" }, description = "Persistence Unit name")
	private String persistenceUnit = "wishlists-pu";

	public static void main(String[] args) {
		new CommandLine(new WishlistApp()).execute(args);
	}

	@Override
	public Void call() {
		EventQueue.invokeLater(() -> {
			try {
				WishlistSwingView view = new WishlistSwingView();
				WishlistDAO dao = new WishlistDAO(persistenceUnit);
				WishlistController controller = new WishlistController(view, dao);
				view.setController(controller);
				view.setVisible(true);
				controller.refreshWishlists();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}
}
