import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class MainFrame extends JFrame {
    private JPanel contentPane;
    private JPanel store;
    private JTable productsTable;
    private JPanel sales;
    private JPanel subblies;
    private JPanel storage;
    private JTabbedPane tabbedPane1;
    private JButton addProductInStoreButton;
    private JScrollPane jScrollPane;
    private JTable storageTable;
    private JScrollPane scrollPaneStorage;
    private JButton addNewProductsStorage;
    private JTable salesTable;
    private JButton buttonAddNewSale;
    private JTable subbliesTable;
    private JButton buttonAddNewDelievery;
    private JPanel clientStorePage;
    private JTable clientStoreTable;
    private JScrollPane scrollPaneSales;
    private JScrollPane scrollPaneSubblies;
    private JPanel listOfPurchases;
    private JTabbedPane tabbedPane2;
    private JButton moveAllToStoreButton;
    private JButton removeByArticleButton;
    private JButton removeByArticleFromStoreButton;
    private JButton purchaseByArticleButton;
    private Timer shopTimer;
    private JLabel timerLabel;

    private int remainingTime;
    private JTable listOfProductsTable;
    DefaultTableModel listOfProductsTableModel;
    private JTextField buyerMoneyField;
    private double buyerMoney = 5000.0;

    public MainFrame() {
        setTitle("Продукты и точка");

        ImageIcon mainIcon = createIcon("icons/main_icon.png");
        assert mainIcon != null;
        setIconImage(mainIcon.getImage());
        remainingTime = 5 * 60;
        timerLabel.setText("Оставшееся время: 5:00");
        buyerMoneyField.setText(String.valueOf(buyerMoney));


        shopTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimerLabel();
                remainingTime--;

                if (remainingTime <= 0) {
                    handleShopTimerExpiration();
                }
            }
        });

        shopTimer.start();

        listOfProductsTableModel = new DefaultTableModel(
                "Название;Артикул;Цена".split(";"), 0);

        DefaultTableModel storeTableModel = new DefaultTableModel(
                "Название;Артикул;Цена;Дата изготовления;Срок годности;Количество товара".split(";"),
                0
        );

        DefaultTableModel storageTableModel = new DefaultTableModel(
                "Название;Артикул;Цена;Дата изготовления;Срок годности;Количество товара".split(";"),
                0
        );

        DefaultTableModel salesTableModel = new DefaultTableModel(
                "Название;Артикул;Цена до скидки;Скидка;Цена после скидки".split(";"),
                0
        );

        DefaultTableModel subbliesTableModel = new DefaultTableModel(
                "Название;Артикул;Количество поставки;Дата поставки;Состояние поставки".split(";"),
                0
        );


        DefaultTableModel clientStoreTableModel = new DefaultTableModel(
                ("Название;Артикул;Цена;Дата изготовления;Срок годности;Количество товара").split(";"),
                0
        );

        clientStoreTable.setModel(clientStoreTableModel);
        salesTable.setModel(salesTableModel);
        subbliesTable.setModel(subbliesTableModel);
        storageTable.setModel(storageTableModel);
        productsTable.setModel(storeTableModel);
        listOfProductsTable.setModel(listOfProductsTableModel);

        purchaseByArticleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePurchase();
            }
        });

        addProductInStoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddToStoreDialog();
            }
        });

        addNewProductsStorage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddProductDialog();
            }
        });

        moveAllToStoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveAllToStore();
            }
        });
        removeByArticleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRemoveByArticleDialog();
            }
        });

        removeByArticleFromStoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRemoveFromStoreByArticleDialog();
            }
        });


        buttonAddNewSale.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddDiscountDialog();
            }
        });
        buttonAddNewDelievery.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewDelivery();
            }
        });


        setContentPane(contentPane);
    }
    private void handlePurchase() {
        try {
            double buyerMoney = Double.parseDouble(buyerMoneyField.getText());

            int selectedRow = clientStoreTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите товар для покупки", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String name = (String) clientStoreTable.getValueAt(selectedRow, 0);
            System.out.println(clientStoreTable.getValueAt(selectedRow, 2));
            double pricePerUnit = Double.parseDouble(((String) clientStoreTable.getValueAt(selectedRow, 2)).replace(',','.'));
            int quantityAvailable = (int) clientStoreTable.getValueAt(selectedRow, 5);

            String purchaseQuantityString = JOptionPane.showInputDialog(this, "Введите количество товара для покупки:", "Покупка товара", JOptionPane.QUESTION_MESSAGE);
            if (purchaseQuantityString == null || purchaseQuantityString.isEmpty()) {
                return;
            }

            int purchaseQuantity = Integer.parseInt(purchaseQuantityString);

            double totalPurchasePrice = pricePerUnit * purchaseQuantity;
            if (totalPurchasePrice > buyerMoney) {
                JOptionPane.showMessageDialog(this, "У покупателя недостаточно денег для совершения покупки", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (purchaseQuantity > quantityAvailable) {
                JOptionPane.showMessageDialog(this, "Недостаточно товара на складе для совершения покупки", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int newQuantityAvailable = quantityAvailable - purchaseQuantity;

            clientStoreTable.setValueAt(newQuantityAvailable, selectedRow, 5);

            DefaultTableModel listOfProductsTableModel = (DefaultTableModel) listOfProductsTable.getModel();
            Object[] purchaseRowData = {
                    name,
                    purchaseQuantity,
                    totalPurchasePrice
            };
            listOfProductsTableModel.addRow(purchaseRowData);

            buyerMoney -= totalPurchasePrice;

            buyerMoneyField.setText(String.valueOf(buyerMoney));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите корректные числовые значения", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTimerLabel() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timerLabel.setText(String.format("Оставшееся время: %d:%02d", minutes, seconds));
    }

    private void addNewDelivery() {
        JFrame addDeliveryFrame = new JFrame("Добавление поставки");
        addDeliveryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(10, 2));

        JTextField nameField = new JTextField();
        JTextField articleField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField deliveryDateField = new JTextField();
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Оформлен", "В пути", "Доставлен"});

        panel.add(new JLabel("Название товара:"));
        panel.add(nameField);
        panel.add(new JLabel("Артикул товара:"));
        panel.add(articleField);
        panel.add(new JLabel("Количество товара:"));
        panel.add(quantityField);
        panel.add(new JLabel("Дата поставки:"));
        panel.add(deliveryDateField);
        panel.add(new JLabel("Статус поставки:"));
        panel.add(statusComboBox);

        JButton addButton = new JButton("Добавить поставку");
        JButton cancelButton = new JButton("Отмена");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameField.getText().isEmpty() || articleField.getText().isEmpty() || quantityField.getText().isEmpty()
                        || deliveryDateField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(addDeliveryFrame, "Пожалуйста, заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    int quantity = Integer.parseInt(quantityField.getText());

                    Object[] rowData = {
                            nameField.getText(),
                            articleField.getText(),
                            quantity,
                            deliveryDateField.getText(),
                            statusComboBox.getSelectedItem()
                    };

                    DefaultTableModel model = (DefaultTableModel) subbliesTable.getModel();
                    model.addRow(rowData);

                    addDeliveryFrame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addDeliveryFrame, "Введите корректные числовые значения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDeliveryFrame.dispose();
            }
        });

        panel.add(addButton);
        panel.add(cancelButton);

        addDeliveryFrame.getContentPane().add(panel);
        addDeliveryFrame.setSize(600, 400);
        addDeliveryFrame.setLocationRelativeTo(this);
        addDeliveryFrame.setVisible(true);
    }


    private void handleShopTimerExpiration() {

        shopTimer.stop();

        JOptionPane.showMessageDialog(this, "Время работы магазина закончено", "Завершение работы", JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }

    private void showAddDiscountDialog() {
        JFrame addDiscountFrame = new JFrame("Добавление скидки на товар");
        addDiscountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2));

        JTextField nameField = new JTextField();
        JTextField articleField = new JTextField();
        JTextField priceBeforeDiscountField = new JTextField();
        JTextField discountField = new JTextField();
        JButton addButton = new JButton("Добавить скидку");
        JButton cancelButton = new JButton("Отмена");

        panel.add(new JLabel("Название:"));
        panel.add(nameField);
        panel.add(new JLabel("Артикул:"));
        panel.add(articleField);
        panel.add(new JLabel("Цена до скидки:"));
        panel.add(priceBeforeDiscountField);
        panel.add(new JLabel("Скидка:"));
        panel.add(discountField);
        panel.add(addButton);
        panel.add(cancelButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String article = articleField.getText();
                double priceBeforeDiscount = Double.parseDouble(priceBeforeDiscountField.getText());
                double discount = Double.parseDouble(discountField.getText());
                if (addDiscountToProduct(name, article, priceBeforeDiscount, discount)) {
                    addDiscountFrame.dispose();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDiscountFrame.dispose();
            }
        });

        addDiscountFrame.getContentPane().add(panel);
        addDiscountFrame.setSize(400, 200);
        addDiscountFrame.setLocationRelativeTo(this);
        addDiscountFrame.setVisible(true);
    }

    private boolean addDiscountToProduct(String name, String article, double priceBeforeDiscount, double discount) {
        DefaultTableModel storeTableModel = (DefaultTableModel) productsTable.getModel();
        DefaultTableModel salesTableModel = (DefaultTableModel) salesTable.getModel();

        for (int i = 0; i < storeTableModel.getRowCount(); i++) {
            if (article.equals(storeTableModel.getValueAt(i, 1))) {
                double priceAfterDiscount = priceBeforeDiscount - (priceBeforeDiscount * discount / 100);

                storeTableModel.setValueAt(priceAfterDiscount, i, 2);

                Object[] rowData = {name, article, priceBeforeDiscount, discount, priceAfterDiscount};
                salesTableModel.addRow(rowData);

                return true;
            }
        }


        JOptionPane.showMessageDialog(this, "Товар с указанным артикулом не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private void showPurchaseByArticleDialog() {
        JFrame purchaseByArticleFrame = new JFrame("Покупка товара по артикулу");
        purchaseByArticleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2));

        JTextField articleField = new JTextField();
        JTextField quantityField = new JTextField();
        JButton purchaseButton = new JButton("Купить");
        JButton cancelButton = new JButton("Отмена");

        panel.add(new JLabel("Артикул:"));
        panel.add(articleField);
        panel.add(new JLabel("Количество:"));
        panel.add(quantityField);
        panel.add(purchaseButton);
        panel.add(cancelButton);

        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                purchaseProductByArticle();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                purchaseByArticleFrame.dispose();
            }
        });

        purchaseByArticleFrame.getContentPane().add(panel);
        purchaseByArticleFrame.setSize(600, 300);
        purchaseByArticleFrame.setLocationRelativeTo(this);
        purchaseByArticleFrame.setVisible(true);
    }


    private void purchaseProductByArticle() {
        int selectedRow = productsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите товар для покупки", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = (String) productsTable.getValueAt(selectedRow, 0);
        String article = (String) productsTable.getValueAt(selectedRow, 1);
        String price = (String) productsTable.getValueAt(selectedRow, 2);
        String manufactureDate = (String) productsTable.getValueAt(selectedRow, 3);
        String expiryDate = (String) productsTable.getValueAt(selectedRow, 4);

        String quantityString = JOptionPane.showInputDialog(this, "Введите количество для покупки:", "Покупка", JOptionPane.PLAIN_MESSAGE);

        if (quantityString == null || quantityString.trim().isEmpty()) {
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityString);

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Введите положительное количество товара", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int currentQuantity = Integer.parseInt((String) productsTable.getValueAt(selectedRow, 5));
            if (quantity > currentQuantity) {
                JOptionPane.showMessageDialog(this, "Недостаточное количество товара на складе", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int newQuantity = currentQuantity - quantity;
            productsTable.setValueAt(Integer.toString(newQuantity), selectedRow, 5);

            addToPurchaseList(name, article, price, manufactureDate, expiryDate, Integer.toString(quantity));

            updateListOfPurchasesTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Введите корректное число", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateListOfPurchasesTable() {
        DefaultTableModel purchasesTableModel = (DefaultTableModel) listOfProductsTable.getModel();
        purchasesTableModel.setRowCount(0);

        for (int i = 0; i < listOfProductsTableModel.getRowCount(); i++) {
            Object[] rowData = {
                    listOfProductsTableModel.getValueAt(i, 0),
                    listOfProductsTableModel.getValueAt(i, 1),
                    listOfProductsTableModel.getValueAt(i, 2),
                    listOfProductsTableModel.getValueAt(i, 3),
                    listOfProductsTableModel.getValueAt(i, 4),
                    listOfProductsTableModel.getValueAt(i, 5)
            };
            purchasesTableModel.addRow(rowData);
        }
    }

    private void addToPurchaseList(String name, String article, String price, String manufactureDate, String expiryDate, String quantity) {

        for (int i = 0; i < listOfProductsTableModel.getRowCount(); i++) {
            if (article.equals(listOfProductsTableModel.getValueAt(i, 1)) &&
                    manufactureDate.equals(listOfProductsTableModel.getValueAt(i, 3))) {
                int existingQuantity = Integer.parseInt((String) listOfProductsTableModel.getValueAt(i, 5));
                int newQuantity = existingQuantity + Integer.parseInt(quantity);
                listOfProductsTableModel.setValueAt(Integer.toString(newQuantity), i, 5);
                return;
            }
        }

        Object[] rowData = {name, article, price, manufactureDate, expiryDate, quantity, "Покупка"};
        listOfProductsTableModel.addRow(rowData);
    }

    private void showRemoveFromStoreByArticleDialog() {
        JFrame removeFromStoreByArticleFrame = new JFrame("Удаление товара из торгового зала по артикулу");
        removeFromStoreByArticleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        JTextField articleField = new JTextField();
        JButton removeButton = new JButton("Удалить");
        JButton cancelButton = new JButton("Отмена");

        panel.add(new JLabel("Артикул:"));
        panel.add(articleField);
        panel.add(removeButton);
        panel.add(cancelButton);

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String article = articleField.getText();
                removeProductFromStoreByArticle(article);
                removeFromStoreByArticleFrame.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeFromStoreByArticleFrame.dispose();
            }
        });

        removeFromStoreByArticleFrame.getContentPane().add(panel);
        removeFromStoreByArticleFrame.setSize(600, 300);
        removeFromStoreByArticleFrame.setLocationRelativeTo(this);
        removeFromStoreByArticleFrame.setVisible(true);
    }

    private void removeProductFromStoreByArticle(String article) {
        DefaultTableModel storeTableModel = (DefaultTableModel) productsTable.getModel();

        for (int i = 0; i < storeTableModel.getRowCount(); i++) {
            if (article.equals(storeTableModel.getValueAt(i, 1))) {
                storeTableModel.removeRow(i);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Товар с указанным артикулом не найден в торговом зале", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    private boolean checkProductInDeliveries(String article, String manufactureDate) {
        DefaultTableModel deliveriesModel = (DefaultTableModel) subbliesTable.getModel();
        for (int row = 0; row < deliveriesModel.getRowCount(); row++) {
            String articleInDelivery = (String) deliveriesModel.getValueAt(row, 1);
            String manufactureDateInDelivery = (String) deliveriesModel.getValueAt(row, 3);

            if (article.equals(articleInDelivery) && manufactureDate.equals(manufactureDateInDelivery)) {
                return true;
            }
        }
        return false;
    }

    private void showRemoveByArticleDialog() {
        JFrame removeByArticleFrame = new JFrame("Удаление товара по артикулу");
        removeByArticleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        JTextField articleField = new JTextField();
        JButton removeButton = new JButton("Удалить");
        JButton cancelButton = new JButton("Отмена");

        panel.add(new JLabel("Артикул:"));
        panel.add(articleField);
        panel.add(removeButton);
        panel.add(cancelButton);

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String article = articleField.getText();
                removeProductByArticle(article);
                removeByArticleFrame.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeByArticleFrame.dispose();
            }
        });

        removeByArticleFrame.getContentPane().add(panel);
        removeByArticleFrame.setSize(300, 150);
        removeByArticleFrame.setLocationRelativeTo(this);
        removeByArticleFrame.setVisible(true);
    }

    private void removeProductByArticle(String article) {
        DefaultTableModel storageTableModel = (DefaultTableModel) storageTable.getModel();

        for (int i = 0; i < storageTableModel.getRowCount(); i++) {
            if (article.equals(storageTableModel.getValueAt(i, 1))) {
                storageTableModel.removeRow(i);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Товар с указанным артикулом не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void moveAllToStore() {
        DefaultTableModel storageTableModel = (DefaultTableModel) storageTable.getModel();
        DefaultTableModel storeTableModel = (DefaultTableModel) productsTable.getModel();

        int rowCount = storageTableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Object[] rowData = new Object[storageTableModel.getColumnCount()];
            for (int j = 0; j < storageTableModel.getColumnCount(); j++) {
                rowData[j] = storageTableModel.getValueAt(i, j);
            }

            storeTableModel.addRow(rowData);
        }
        storageTableModel.setRowCount(0);
    }

    private void showAddToStoreDialog() {
        JFrame addToStoreFrame = new JFrame("Добавление товара в торговый зал клиента");
        addToStoreFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2));

        JTextField articleField = new JTextField();
        JTextField quantityField = new JTextField();
        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");

        panel.add(new JLabel("Артикул:"));
        panel.add(articleField);
        panel.add(new JLabel("Количество:"));
        panel.add(quantityField);
        panel.add(addButton);
        panel.add(cancelButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String article = articleField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                addOrUpdateProductToStore(article, quantity);
                addToStoreFrame.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToStoreFrame.dispose();
            }
        });

        addToStoreFrame.getContentPane().add(panel);
        addToStoreFrame.setSize(300, 150);
        addToStoreFrame.setLocationRelativeTo(this);
        addToStoreFrame.setVisible(true);
    }

    private void addOrUpdateProductToStore(String article, int quantity) {
        DefaultTableModel productsTableModel = (DefaultTableModel) productsTable.getModel();
        DefaultTableModel clientStoreTableModel = (DefaultTableModel) clientStoreTable.getModel();

        for (int i = 0; i < clientStoreTableModel.getRowCount(); i++) {
            if (article.equals(clientStoreTableModel.getValueAt(i, 1))) {
                int currentQuantity = Integer.parseInt(clientStoreTableModel.getValueAt(i, 5).toString());
                clientStoreTableModel.setValueAt(currentQuantity + quantity, i, 5);
                return;
            }
        }

        for (int i = 0; i < productsTableModel.getRowCount(); i++) {
            if (article.equals(productsTableModel.getValueAt(i, 1))) {
                Object[] rowData = new Object[productsTableModel.getColumnCount()];
                for (int j = 0; j < productsTableModel.getColumnCount(); j++) {
                    rowData[j] = productsTableModel.getValueAt(i, j);
                }
                clientStoreTableModel.addRow(rowData);
                return;
            }
        }
    }



    private void showAddProductDialog() {
        JFrame addProductFrame = new JFrame("Добавление товара");
        addProductFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(15, 2));

        JTextField nameField = new JTextField();
        JTextField articleField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField manufactureDateField = new JTextField();
        JTextField expiryDateField = new JTextField();
        JTextField quantityField = new JTextField();

        panel.add(new JLabel("Название:"));
        panel.add(nameField);
        panel.add(new JLabel("Артикул:"));
        panel.add(articleField);
        panel.add(new JLabel("Цена:"));
        panel.add(priceField);
        panel.add(new JLabel("Дата изготовления:"));
        panel.add(manufactureDateField);
        panel.add(new JLabel("Срок годности:"));
        panel.add(expiryDateField);
        panel.add(new JLabel("Количество товара:"));
        panel.add(quantityField);

        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameField.getText().isEmpty() || articleField.getText().isEmpty() || priceField.getText().isEmpty()
                        || manufactureDateField.getText().isEmpty() || expiryDateField.getText().isEmpty() || quantityField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(addProductFrame, "Пожалуйста, заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!checkProductInDeliveries(articleField.getText(), manufactureDateField.getText())) {
                    JOptionPane.showMessageDialog(addProductFrame, "Товар с указанным артикулом и датой изготовления не найден в поставках", "Предупреждение", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    double price = Double.parseDouble(priceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());

                    Object[] rowData = {
                            nameField.getText(),
                            articleField.getText(),
                            String.format("%.2f", price),
                            manufactureDateField.getText(),
                            expiryDateField.getText(),
                            quantity
                    };

                    DefaultTableModel model = (DefaultTableModel) storageTable.getModel();
                    model.addRow(rowData);

                    addProductFrame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addProductFrame, "Введите корректные числовые значения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductFrame.dispose();
            }
        });

        panel.add(addButton);
        panel.add(cancelButton);

        addProductFrame.getContentPane().add(panel);
        addProductFrame.setSize(600, 400);
        addProductFrame.setLocationRelativeTo(this);
        addProductFrame.setVisible(true);
    }


    protected static ImageIcon createIcon(String path) {
        URL imgURL = MainFrame.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("File not found " + path);
            return null;
        }
    }

    public static void main(String[] args) {
        MainFrame dialog = new MainFrame();
        dialog.setMinimumSize(new Dimension(1000, 1000));
        dialog.pack();
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
