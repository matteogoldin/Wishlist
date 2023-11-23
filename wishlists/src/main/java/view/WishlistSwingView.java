package view;

import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import businesslogic.WishlistController;
import model.Item;
import model.Wishlist;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JInternalFrame;
import javax.swing.JTextPane;

public class WishlistSwingView extends JFrame implements WishlistView {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static WishlistSwingView frame;
	private AddWishlistSwingView addWLFrame;
	private AddItemSwingView addItemFrame;

	private WishlistController controller;

	private JList<Wishlist> listWL;
	private DefaultListModel<Wishlist> listWLModel;
	private JList<Item> listItem;
	private DefaultListModel<Item> listItemModel;
	private JLabel lblError;

	public WishlistSwingView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 397);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblWL = new JLabel("Wishlists:");
		lblWL.setName("lblWL");
		lblWL.setBounds(10, 9, 226, 14);
		contentPane.add(lblWL);

		JLabel lblWLDesc = new JLabel("");
		lblWLDesc.setName("lblWLDesc");
		lblWLDesc.setBounds(10, 88, 416, 34);
		contentPane.add(lblWLDesc);

		JButton btnAddWL = new JButton("Add");
		btnAddWL.setName("btnAddWL");
		btnAddWL.setBounds(80, 123, 89, 23);
		contentPane.add(btnAddWL);
		btnAddWL.addActionListener(e -> {
			addWLFrame = new AddWishlistSwingView(controller);
			addWLFrame.setVisible(true);
		});

		JButton btnRemoveWL = new JButton("Remove");
		btnRemoveWL.setName("btnRemoveWL");
		btnRemoveWL.setEnabled(false);
		btnRemoveWL.setBounds(278, 123, 89, 23);
		contentPane.add(btnRemoveWL);
		btnRemoveWL.addActionListener(e -> {
			controller.removeWishlist(listWLModel.get(listWL.getSelectedIndex()));
		});

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setName("btnRefresh");
		btnRefresh.setBounds(337, 0, 89, 23);
		contentPane.add(btnRefresh);
		btnRefresh.addActionListener(e -> {
			int selectedIndex = listWL.getSelectedIndex();
			if (selectedIndex != -1) {
				Wishlist wl = listWLModel.getElementAt(selectedIndex);
				controller.refreshWishlists();
				if (listWLModel.contains(wl)) {
					controller.refreshItems(wl);
					listWL.setSelectedValue(wl, true);
				}
			} else {
				controller.refreshWishlists();
			}
		});

		JLabel lblItem = new JLabel("Select a Wishlist...");
		lblItem.setName("lblItem");
		lblItem.setBounds(10, 170, 416, 14);
		contentPane.add(lblItem);

		JButton btnAddItem = new JButton("Add");
		btnAddItem.setName("btnAddItem");
		btnAddItem.setEnabled(false);
		btnAddItem.setBounds(80, 281, 89, 23);
		contentPane.add(btnAddItem);
		btnAddItem.addActionListener(e -> {
			addItemFrame = new AddItemSwingView(controller, listWLModel.get(listWL.getSelectedIndex()));
			addItemFrame.setVisible(true);
		});

		JButton btnRemoveItem = new JButton("Remove");
		btnRemoveItem.setName("btnRemoveItem");
		btnRemoveItem.setEnabled(false);
		btnRemoveItem.setBounds(278, 281, 89, 23);
		contentPane.add(btnRemoveItem);
		btnRemoveItem.addActionListener(e -> {
			controller.removeItemFromWishlist(listItemModel.get(listItem.getSelectedIndex()),
					listWLModel.get(listWL.getSelectedIndex()));
		});

		lblError = new JLabel("");
		lblError.setName("lblError");
		lblError.setForeground(new Color(255, 0, 0));
		lblError.setBounds(10, 335, 416, 14);
		contentPane.add(lblError);

		JLabel lblItemDesc = new JLabel("");
		lblItemDesc.setName("lblItemDesc");
		lblItemDesc.setBounds(10, 247, 416, 34);
		contentPane.add(lblItemDesc);

		listItemModel = new DefaultListModel<>();
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setName("scrollPane2");
		scrollPane2.setBounds(10, 184, 416, 61);
		contentPane.add(scrollPane2);
		listItem = new JList<>(listItemModel);
		scrollPane2.setViewportView(listItem);
		listItem.setName("listItem");
		listItem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listItem.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedIndex = listItem.getSelectedIndex();
				btnRemoveItem.setEnabled(selectedIndex != -1);
				if (selectedIndex != -1) {
					Item element = listItemModel.elementAt(selectedIndex);
					lblItemDesc.setText(String.format("%s (Price: %.2f€)", element.getDesc(), element.getPrice()));
				} else {
					lblItemDesc.setText("");
				}
			}
		});

		listWLModel = new DefaultListModel<>();
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setName("scrollPane1");
		scrollPane1.setBounds(10, 25, 416, 61);
		contentPane.add(scrollPane1);
		listWL = new JList<>(listWLModel);
		scrollPane1.setViewportView(listWL);
		listWL.setToolTipText("");
		listWL.setName("listWL");
		listWL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listWL.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedIndex = listWL.getSelectedIndex();
				btnRemoveWL.setEnabled(selectedIndex != -1);
				btnAddItem.setEnabled(selectedIndex != -1);
				if (selectedIndex != -1) {
					showAllItems(listWLModel.elementAt(selectedIndex));
					lblWLDesc.setText(listWLModel.elementAt(selectedIndex).getDesc());
					lblItem.setText(String.format("Wishes in %s:", listWLModel.elementAt(selectedIndex).getName()));
				} else {
					listItemModel.clear();
					lblWLDesc.setText("");
					lblItem.setText("Select a Wishlist...");
				}
			}
		});

		JSeparator separator1 = new JSeparator();
		separator1.setBounds(10, 157, 416, 2);
		separator1.setName("separator1");
		contentPane.add(separator1);

		JSeparator separator2 = new JSeparator();
		separator2.setBounds(10, 315, 416, 2);
		separator2.setName("separator2");
		contentPane.add(separator2);
	}

	@Override
	public void showAllWLs(List<Wishlist> wlList) {
		listWLModel.clear();
		listWLModel.addAll(wlList);
	}

	@Override
	public void showAllItems(Wishlist wl) {
		listItemModel.clear();
		listItemModel.addAll(wl.getItems());
	}

	@Override
	public void showError(String errorMessage) {
		lblError.setText(errorMessage);
		Timer timer = new Timer(3000, event -> {
			lblError.setText("");
		});
		timer.setRepeats(false);
		timer.start();
	}

	public void setController(WishlistController controller) {
		this.controller = controller;
	}

	DefaultListModel<Item> getListItemModel() {
		return listItemModel;
	}

	DefaultListModel<Wishlist> getListWLModel() {
		return listWLModel;
	}

	AddWishlistSwingView getAddWLFrame() {
		return addWLFrame;
	}

	AddItemSwingView getAddItemFrame() {
		return addItemFrame;
	}
}
