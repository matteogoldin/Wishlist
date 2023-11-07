package businesslogic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
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
		verify(view).refreshWL();
		assertThat(controller.getWlList().get(0)).isEqualTo(wl);
	}
	
	@Test 
	void addingWLWithSameNameCauseException(){
		doNothing().doThrow(new EntityExistsException()).when(dao).addWL(isA(Wishlist.class));
		Wishlist wl = new Wishlist("Compleanno","My birthday gifts");
		Wishlist wl_dup = new Wishlist("Compleanno","Mum birthday gifts");
		controller.addWishlist(wl);
		controller.addWishlist(wl_dup);
		assertThat(controller.getWlList().size()).isEqualTo(1);
		verify(view).refreshWL();		
	}

}
