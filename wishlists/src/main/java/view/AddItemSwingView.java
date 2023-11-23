package view;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import businesslogic.WishlistController;
import model.Item;
import model.Wishlist;

public class AddItemSwingView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnAdd;
	private JTextField textName;
	private JTextArea textDesc;
	private JLabel lblPrice;
	private JLabel lblEuro;
	private JLabel lblPriceError;
	private JTextField textPrice;


	private transient WishlistController controller;
	private transient Wishlist wl;

	public AddItemSwingView(WishlistController wlController, Wishlist wishlist) {
		setTitle("Add Item");
		this.controller = wlController;
		this.wl = wishlist;
		MyDocumentListener mdc = new MyDocumentListener();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 314, 277);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblName = new JLabel("Name:");
		lblName.setName("lblName");
		lblName.setBounds(10, 20, 49, 14);
		contentPane.add(lblName);

		textName = new JTextField();
		textName.setName("textName");
		textName.setBorder(new EmptyBorder(0, 0, 0, 0));
		textName.setBounds(10, 36, 274, 20);
		contentPane.add(textName);
		textName.setColumns(10);
		textName.getDocument().addDocumentListener(mdc);

		JLabel lblDesc = new JLabel("Description:");
		lblDesc.setName("lblDesc");
		lblDesc.setBounds(10, 67, 278, 14);
		contentPane.add(lblDesc);

		textDesc = new JTextArea();
		textDesc.setName("textDesc");
		textDesc.setBorder(new EmptyBorder(0, 0, 0, 0));
		textDesc.setLineWrap(true);
		textDesc.setBounds(10, 84, 273, 58);
		contentPane.add(textDesc);
		textDesc.getDocument().addDocumentListener(mdc);

		btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		btnAdd.setName("btnAdd");
		btnAdd.setBounds(107, 210, 89, 23);
		contentPane.add(btnAdd);
		btnAdd.addActionListener(e -> {
			String name = textName.getText();
			String desc = textDesc.getText();
			float price = Float.parseFloat(textPrice.getText());
			Item item = new Item(name, desc, price);
			controller.addItemToWishlist(item, wl);
			this.dispose();
		});

		lblPrice = new JLabel("Price:");
		lblPrice.setName("lblPrice");
		lblPrice.setBounds(10, 155, 49, 14);
		contentPane.add(lblPrice);

		lblEuro = new JLabel("€");
		lblEuro.setName("lblEuro");
		lblEuro.setBounds(112, 174, 49, 14);
		contentPane.add(lblEuro);

		lblPriceError = new JLabel("Insert a valid price");
		lblPriceError.setVisible(false);
		lblPriceError.setForeground(new Color(255, 0, 0));
		lblPriceError.setName("lblPriceError");
		lblPriceError.setBounds(125, 174, 142, 14);
		contentPane.add(lblPriceError);

		textPrice = new JTextField();
		textPrice.setText("0.00");
		textPrice.setHorizontalAlignment(SwingConstants.RIGHT);
		textPrice.setBorder(new EmptyBorder(0, 0, 0, 0));
		textPrice.setName("textPrice");
		textPrice.setBounds(10, 171, 96, 20);
		contentPane.add(textPrice);
		textPrice.setColumns(10);
		textPrice.getDocument().addDocumentListener(mdc);


	}

	class MyDocumentListener implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			btnAddEnabler();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			btnAddEnabler();
		}

		@Override
		public void changedUpdate(DocumentEvent e) { /* Not used */ }

		private void btnAddEnabler() {
			try {
				Float.parseFloat(textPrice.getText());
				btnAdd.setEnabled(!textName.getText().trim().isEmpty() && !textDesc.getText().trim().isEmpty());
				lblPriceError.setVisible(false);
			} catch(Exception e) {
				lblPriceError.setVisible(true);
				btnAdd.setEnabled(false);
			}

		}
	}
}
