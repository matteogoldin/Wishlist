package businesslogic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import daos.WishlistDAO;
import jakarta.persistence.EntityExistsException;
import model.Wishlist;
import view.WishlistView;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {
	@Mock
	private WishlistView view;

	@Mock
	private WishlistDAO dao;

	@InjectMocks
	private WishlistController controller;

	@Test
	void wlCorrectlyAdded() {
		Wishlist wl = new Wishlist("Compleanno","My birthday gifts");
		controller.addWishlist(wl);
		verify(dao).addWL(wl);
		verify(view).refreshWlList();
		assertThat(controller.getWlList()).hasSize(1);
		assertThat(controller.getWlList().get(0)).isEqualTo(wl);
	}

	@Test 
	void addingWLWithSameNameCausesException(){
		doNothing().doThrow(new EntityExistsException()).when(dao).addWL(isA(Wishlist.class));
		Wishlist wl = new Wishlist("Compleanno","My birthday gifts");
		Wishlist wl_dup = new Wishlist("Compleanno","Mum birthday gifts");
		controller.addWishlist(wl);
		controller.addWishlist(wl_dup);
		assertThat(controller.getWlList()).hasSize(1);
		verify(view).refreshWlList();
		verify(view).showError("Wishlist Compleanno already exists");
	}

	@Test
	void otherExceptionWhileAddingAWLAreManaged() {
		doThrow(new RuntimeException()).when(dao).addWL(isA(Wishlist.class));
		Wishlist wl = new Wishlist("Compleanno","My birthday gifts");
		controller.addWishlist(wl);
		assertThat(controller.getWlList()).isEmpty();
		verify(view).showError("Error: please try again");
	}

	@Test
	void wlCorrectlyRemoved() {
		Wishlist wl = new Wishlist("Compleanno", "My birthday gifts");
		controller.getWlList().add(wl);
		controller.removeWishlist(wl);
		verify(dao).removeWL(anyString());
		assertThat(controller.getWlList()).isEmpty();
		verify(view).refreshWlList();
	}


	@Test 
	void tryingToRemoveAWLNotPersistedCausesException() { 
		Wishlist wl = new Wishlist("Compleanno", "My birthday gifts"); 
		doThrow(new IllegalArgumentException()).when(dao).removeWL(wl.getName());
		controller.removeWishlist(wl);
		verify(view, times(0)).refreshWlList();
		verify(view).showError("Wishlist Compleanno doesn't exist or has been already removed");
	}
	
	@Test
	void removingAWLNotPersistedButStillInTheWLListRemovesItFromTheWLList() {
		Wishlist wl = new Wishlist("Compleanno", "My birthday gifts"); 
		controller.getWlList().add(wl);
		doThrow(new IllegalArgumentException()).when(dao).removeWL(wl.getName());
		controller.removeWishlist(wl);
		verify(view, times(0)).refreshWlList();
		verify(view).showError("Wishlist Compleanno doesn't exist or has been already removed");
		assertThat(controller.getWlList()).isEmpty();
	}

}
