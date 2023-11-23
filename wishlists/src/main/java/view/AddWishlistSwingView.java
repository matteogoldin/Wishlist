package view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import businesslogic.WishlistController;
import model.Wishlist;

public class AddWishlistSwingView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textName;
	private JButton btnAdd;
	private JTextArea textDesc;

	private transient WishlistController controller;

	public AddWishlistSwingView(WishlistController wlController) {
		this.controller = wlController;
		MyDocumentListener mdc = new MyDocumentListener();

		setName("frameAddWL");
		setTitle("Add Wishlist");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 307, 235);
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
		textName.setBounds(10, 36, 278, 20);
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
		btnAdd.setBounds(106, 166, 89, 23);
		contentPane.add(btnAdd);
		btnAdd.addActionListener(e -> {
			String name = textName.getText();
			String desc = textDesc.getText();
			Wishlist wl = new Wishlist(name, desc);
			controller.addWishlist(wl);
			this.dispose();
		});
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
			btnAdd.setEnabled(!textName.getText().trim().isEmpty() && !textDesc.getText().trim().isEmpty());
		}
	}

}
