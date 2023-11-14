package businesslogic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import daos.ItemDAO;
import daos.WishlistDAO;
import model.Item;
import model.Wishlist;
import view.WishlistView;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {
	@Mock
	private WishlistView view;

	@Mock
	private WishlistDAO wlDao;

	@Mock
	private ItemDAO itemDao;

	@InjectMocks
	private WishlistController controller;

	private static final String ERROR_STRING = "Error: please try again or try to refresh";

	@Test
	void wlCorrectlyAdded() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		controller.addWishlist(wl);
		verify(wlDao).add(wl);
		verify(view).showAllWLs(controller.getWlList());
		assertAll(() -> assertThat(controller.getWlList()).hasSize(1),
				() -> assertThat(controller.getWlList().get(0)).isEqualTo(wl));
	}

	@Test
	void addingWLWithSameNameShowError() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Wishlist wl_dup = new Wishlist("Birthday", "Mum birthday gifts");
		doNothing().doThrow(new RuntimeException()).when(wlDao).add(isA(Wishlist.class));
		controller.addWishlist(wl);
		controller.addWishlist(wl_dup);
		assertThat(controller.getWlList()).hasSize(1);
		verify(view, times(2)).showAllWLs(controller.getWlList());
		verify(view).showError(ERROR_STRING);
	}

	@Test
	void wlCorrectlyRemoved() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		controller.getWlList().add(wl);
		controller.removeWishlist(wl);
		verify(wlDao).remove(wl);
		assertThat(controller.getWlList()).isEmpty();
		verify(view).showAllWLs(controller.getWlList());
	}

	@Test
	void removingAWLNotPersistedAndNotInTheListDoNothing() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		doNothing().when(wlDao).remove(wl);
		controller.removeWishlist(wl);
		verify(view).showAllWLs(controller.getWlList());
		assertThat(controller.getWlList()).isEmpty();
	}

	@Test
	void removingAWLNotPersistedButInTheWlListRemovesItFromTheList() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		doNothing().when(wlDao).remove(wl);
		controller.getWlList().add(wl);
		controller.removeWishlist(wl);
		verify(view).showAllWLs(controller.getWlList());
		assertThat(controller.getWlList()).isEmpty();
	}

	@Test
	void otherExceptionWhileRemovingAWLAreManaged() {
		doThrow(new RuntimeException()).when(wlDao).remove(isA(Wishlist.class));
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		controller.removeWishlist(wl);
		assertThat(controller.getWlList()).isEmpty();
		verify(view).showError(ERROR_STRING);
	}

	@Test
	void itemCorrectlyAddedToWL() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		controller.getWlList().add(wl);
		controller.addItemToWishlist(item, wl);
		assertAll(() -> assertThat(item.getWishlist()).isEqualTo(wl),
				() -> assertThat(wl.getItems().contains(item)),
				() -> assertThat(wl.getItems()).hasSize(1));
		verify(wlDao).merge(wl);
		verify(view).showAllWLs(controller.getWlList());
		verify(view).showAllItems(wl);
	}

	@Test
	void correctlyAddDuplicatedItemToDifferentWishlist() {
		Wishlist wl1 = new Wishlist("Birthday", "My birthday gifts");
		Wishlist wl2 = new Wishlist("Christmas", "Gift ideas");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		Item item_dup = new Item("Phone", "Samsung Galaxy A52", 300);
		doNothing().when(wlDao).merge(isA(Wishlist.class));
		controller.getWlList().add(wl1);
		controller.getWlList().add(wl2);
		controller.addItemToWishlist(item, wl1);
		controller.addItemToWishlist(item_dup, wl2);
		assertAll(() -> assertThat(item.getWishlist()).isEqualTo(wl1),
				() -> assertThat(item_dup.getWishlist()).isEqualTo(wl2),
				() -> assertThat(wl1.getItems()).containsOnly(item),
				() -> assertThat(wl2.getItems()).containsOnly(item_dup));
		verify(view, times(2)).showAllWLs(controller.getWlList());
		verify(view, times(2)).showAllItems(isA(Wishlist.class));
	}

	@Test
	void tryingToAddADuplicatedItemToSameWishlistShowError() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		Item item_dup = new Item("Phone", "Samsung Galaxy A52", 300);
		doNothing().doThrow(new RuntimeException()).when(wlDao).merge(isA(Wishlist.class));
		controller.getWlList().add(wl);
		controller.addItemToWishlist(item, wl);
		controller.addItemToWishlist(item_dup, wl);
		assertAll(() -> assertThat(item.getWishlist()).isEqualTo(wl),
				() -> assertThat(item_dup.getWishlist()).isNull(),
				() -> assertThat(wl.getItems()).containsOnly(item));
		verify(view, times(2)).showAllWLs(controller.getWlList());
		verify(view, times(2)).showAllItems(wl);
		verify(view).showError(ERROR_STRING);
	}

	@Test
	void tryingToAddAnObjectInAWlNotPersistedShowError() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		doThrow(new RuntimeException()).when(wlDao).merge(isA(Wishlist.class));
		controller.addItemToWishlist(item, wl);
		assertThat(item.getWishlist()).isNull();
		assertThat(wl.getItems()).isEmpty();
		verify(view).showError(ERROR_STRING);
		verify(view).showAllWLs(controller.getWlList());
		verify(view).showAllItems(wl);
	}

	@Test
	void otherExceptionWhileAddingAnItemToAWLAreManaged() {
		doThrow(new RuntimeException()).when(wlDao).merge(isA(Wishlist.class));
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		controller.getWlList().add(wl);
		controller.addItemToWishlist(item, wl);
		assertThat(wl.getItems()).isEmpty();
		verify(view).showError(ERROR_STRING);
	}

	@Test
	void correctlyRemovingAnObject() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		controller.getWlList().add(wl);
		wl.getItems().add(item);
		controller.removeItemFromWishlist(item, wl);
		assertThat(wl.getItems()).isEmpty();
		verify(wlDao).merge(wl);
		verify(view).showAllItems(wl);
		verify(view).showAllWLs(controller.getWlList());
	}

	@Test
	void removeAnObjectNotPersistedButInTheListRemoveTheObjectFromTheList() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		doNothing().when(wlDao).merge(wl);
		controller.getWlList().add(wl);
		wl.getItems().add(item);
		controller.removeItemFromWishlist(item, wl);
		assertThat(wl.getItems()).isEmpty();
		verify(wlDao).merge(wl);
		verify(view).showAllItems(wl);
		verify(view).showAllWLs(controller.getWlList());
	}

	@Test
	void otherExceptionWhileRemovingAnItemToAWLAreManaged() {
		doThrow(new RuntimeException()).when(wlDao).merge(isA(Wishlist.class));
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		controller.getWlList().add(wl);
		wl.getItems().add(item);
		controller.removeItemFromWishlist(item, wl);
		assertThat(wl.getItems()).hasSize(1);
		verify(view).showError(ERROR_STRING);
	}

	@Test
	void refreshWishlistsGetWlsFromTheDaoAndSendThemToTheView() {
		controller.refreshWishlists();
		verify(wlDao).getAll();
		verify(view).showAllWLs(controller.getWlList());
	}

	@Test
	void refreshWishlistManagesExceptionFromDao() {
		when(wlDao.getAll()).thenThrow(new RuntimeException());
		controller.refreshWishlists();
		verify(wlDao).getAll();
		verify(view, times(0)).showAllWLs(controller.getWlList());
		verify(view).showError(ERROR_STRING);
	}

	@Test
	void refreshItemsGetItemsFromTheDaoAndSendThemToTheView() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		when(itemDao.getAllWLItems(wl)).thenReturn(Arrays.asList(item));
		controller.refreshItems(wl);
		assertThat(wl.getItems()).isEqualTo(Arrays.asList(item));
		verify(itemDao).getAllWLItems(wl);
		verify(view).showAllItems(wl);
	}

	@Test
	void refreshItemsManagesExceptionFromDao() {
		Wishlist wl = new Wishlist("Birthday", "My birthday gifts");
		when(itemDao.getAllWLItems(wl)).thenThrow(new RuntimeException());
		controller.refreshItems(wl);
		verify(itemDao).getAllWLItems(wl);
		verify(view, times(0)).showAllItems(wl);
		verify(view).showError(ERROR_STRING);
	}

}
