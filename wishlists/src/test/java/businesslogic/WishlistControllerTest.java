package businesslogic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import daos.ItemDAO;
import daos.WishlistDAO;
import jakarta.persistence.EntityExistsException;
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

	@Test
	void wlCorrectlyAdded() {
		Wishlist wl = new Wishlist("Compleanno","My birthday gifts");
		when(wlDao.getAll()).thenAnswer(a -> {
			controller.getWlList().add(wl);
			return controller.getWlList();
		});
		controller.addWishlist(wl);
		verify(wlDao).add(wl);
		verify(view).showAllWLs(controller.getWlList());
		assertThat(controller.getWlList()).hasSize(1);
		assertThat(controller.getWlList().get(0)).isEqualTo(wl);
		verify(wlDao).getAll();
	}

	@Test 
	void addingWLWithSameNameCausesException(){
		Wishlist wl = new Wishlist("Compleanno","My birthday gifts");
		Wishlist wl_dup = new Wishlist("Compleanno","Mum birthday gifts");
		doNothing().doThrow(new EntityExistsException()).when(wlDao).add(isA(Wishlist.class));
		//First call we insert the wl, the second call no
		when(wlDao.getAll()).thenAnswer(a -> {
			controller.getWlList().add(wl);
			return controller.getWlList();
		}).thenReturn(controller.getWlList());
		controller.addWishlist(wl);
		controller.addWishlist(wl_dup);
		assertThat(controller.getWlList()).hasSize(1);
		verify(view, times(2)).showAllWLs(controller.getWlList());
		verify(wlDao, times(2)).getAll();
		verify(view).showError("Wishlist Compleanno already exists");
	}

	@Test
	void otherExceptionWhileAddingAWLAreManaged() {
		doThrow(new RuntimeException()).when(wlDao).add(isA(Wishlist.class));
		Wishlist wl = new Wishlist("Compleanno","My birthday gifts");
		controller.addWishlist(wl);
		assertThat(controller.getWlList()).isEmpty();
		verify(view).showError("Error: please try again");
		verify(wlDao).getAll();
	}

	@Test
	void wlCorrectlyRemoved() {
		Wishlist wl = new Wishlist("Compleanno", "My birthday gifts");
		when(wlDao.getAll()).thenReturn(new ArrayList<Wishlist>());
		controller.getWlList().add(wl);
		controller.removeWishlist(wl);
		verify(wlDao).remove(wl.getName());
		assertThat(controller.getWlList()).isEmpty();
		verify(view).showAllWLs(controller.getWlList());
		verify(wlDao).getAll();
	}


	@Test 
	void tryingToRemoveAWLNotPersistedCausesException() { 
		Wishlist wl = new Wishlist("Compleanno", "My birthday gifts"); 
		doThrow(new IllegalArgumentException()).when(wlDao).remove(wl.getName());
		controller.removeWishlist(wl);
		verify(view).showAllWLs(controller.getWlList());
		verify(wlDao).getAll();
		verify(view).showError("Wishlist Compleanno doesn't exist or has been already removed");
		assertThat(controller.getWlList()).isEmpty();
	}
	
	@Test
	void removingAWLNotPersistedButStillInTheWLListRemovesItFromTheWLList() {
		Wishlist wl = new Wishlist("Compleanno", "My birthday gifts"); 
		controller.getWlList().add(wl);
		doThrow(new IllegalArgumentException()).when(wlDao).remove(wl.getName());
		when(wlDao.getAll()).thenReturn(new ArrayList<Wishlist>());
		controller.removeWishlist(wl);
		verify(view).showError(anyString());
		verify(view).showAllWLs(controller.getWlList());
		verify(wlDao).getAll();
		assertThat(controller.getWlList()).isEmpty();
	}
	
	@Test
	void itemCorrectlyAddedToWL() {
		Wishlist wl = new Wishlist("Compleanno", "My birthday gifts");
		Item item = new Item("Phone", "Samsung Galaxy A52", 300);
		controller.getWlList().add(wl);
		controller.addItemToWishlist(item, wl);
		assertAll(
				() -> assertThat(item.getWishlist()).isEqualTo(wl),
				() -> assertThat(controller.getWlList().get(0).getItems()).contains(item),
				() -> assertThat(controller.getWlList().get(0).getItems()).hasSize(1));
		verify(itemDao).add(item);
		verify(view).showAllItems(wl);
		verify(wlDao).getAll();
		verify(itemDao).getAllWLItems(wl.getName());
	}
	
	/*don't add Item To Wl if the wl doesn't exists and set item.wl to null again (also iff the add doesn't succeed)*/
	
}
