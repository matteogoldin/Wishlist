package bdd.steps;

import java.util.List;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.*;
import org.assertj.swing.timing.Pause;
import org.junit.runner.RunWith;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import utils.SQLClient;
import view.WishlistSwingView;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/bdd/resources", monochrome = true)
public class WishlistSwingAppSteps {
	private SQLClient client;
	private FrameFixture window;
	private String persistenceUnit = "wishlists-pu-it";

	@Before
	public void setUp() {
		client = new SQLClient(persistenceUnit);
		client.initEmptyDB();
	}

	@After
	public void onTearDown() {
		if (window != null)
			window.cleanUp();
	}

	@Given("The database contains the following wishlists")
	public void the_database_contains_the_following_wishlists(List<List<String>> wlValues) {
		wlValues.forEach(v -> client.insertWishlist(v.get(0), v.get(1)));
		System.out.println(wlValues);
	}

	@Given("The wishlist {string} contains the following values")
	public void the_wishlist_contains_the_following_values(String wlName, List<List<String>> itemValues) {
		itemValues.forEach(v -> client.insertItem(wlName, v.get(0), v.get(1), Float.parseFloat(v.get(2))));
	}

	@When("The Wishlist App view is shown")
	public void the_view_is_shown() {
		application("app.WishlistApp")
			.withArgs("--persistence-unit=" + persistenceUnit)
			.start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<WishlistSwingView>(WishlistSwingView.class) {
			@Override
			protected boolean isMatching(WishlistSwingView frame) {
				return frame.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@When("The wishlist {string} is selected")
	public void the_wishlist_is_selected(String wlName) {
		window.list("listWL").selectItem(wlName);
	}

	@Then("The list of wishlist contains")
	public void the_list_of_wishlist_contains(List<String> wlNames) {
		wlNames.forEach(n -> assertThat(window.list("listWL").contents()).anySatisfy(e -> assertThat(e).isEqualTo(n)));
	}

	@Then("The list of item contains")
	public void the_list_of_item_contains(List<String> itemNames) {
		itemNames.forEach(n -> assertThat(window.list("listItem").contents()).anySatisfy(e -> assertThat(e).isEqualTo(n)));
	}
	
}
