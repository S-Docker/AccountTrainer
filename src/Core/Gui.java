package Core;

import Data.*;

import Utilities.Enums;
import org.osbot.rs07.script.Script;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.HashMap;

public class Gui {
    private final JDialog mainDialog;
    private Data data;
    private boolean started;

    Map<String, Integer> selectedInventory = new HashMap<String, Integer>();
    Map<String, Integer> itemsNeeded = new HashMap<>();
    String accountType;
    String muleName;
    Enums.AccountStatus accountStatus;
    final JComboBox<Enums.AccountStatus> accountStatusSelector;
    final JComboBox<Enums.AccountType> accountTypeSelector;

    public Gui(Script script, Data data){
        this.data = data;
        mainDialog = new JDialog();
        mainDialog.setTitle("Account Trainer");
        mainDialog.setModal(true);
        mainDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainDialog.getContentPane().add(mainPanel);

        /**
         * Account Selection
         */
        JPanel accountTypeSelectionPanel = new JPanel();
        accountTypeSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel accountTypeSelectionLabel = new JLabel("Select account type: ");
        accountTypeSelectionPanel.add(accountTypeSelectionLabel);

        accountTypeSelector = new JComboBox<>(Enums.AccountType.values());
        accountTypeSelectionPanel.add(accountTypeSelector);

        mainPanel.add(accountTypeSelectionPanel);

        /**
         * Account Status Selection
         */
        JPanel accountStatusSelectionPanel = new JPanel();
        accountStatusSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel accountStatusSelectionLabel = new JLabel("Select account status: ");
        accountStatusSelectionPanel.add(accountStatusSelectionLabel);

        accountStatusSelector = new JComboBox<>(Enums.AccountStatus.values());
        accountStatusSelectionPanel.add(accountStatusSelector);

        mainPanel.add(accountStatusSelectionPanel);

        /**
         * Mule Selection
         */
        JPanel muleSelectionPanel = new JPanel();
        muleSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel muleSelectionLabel = new JLabel("Mule: ");
        muleSelectionPanel.add(muleSelectionLabel);

        JTextField muleNameField = new JTextField(20);
        muleSelectionPanel.add((muleNameField));

        mainPanel.add(muleSelectionPanel);

        /**
         *
         * Inventory Item Selection

        JPanel inventorySelectionPanel = new JPanel();
        inventorySelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel inventorySelectionLabel = new JLabel("Select inventory item: ");
        inventorySelectionPanel.add(inventorySelectionLabel);

        JTextField chooseInventoryItems = new JTextField(20);
        inventorySelectionPanel.add((chooseInventoryItems));

        JLabel quantitySelectionLabel = new JLabel("Select quantity: ");
        inventorySelectionPanel.add(quantitySelectionLabel);

        JTextField chooseInventoryQuantity = new JTextField(2);
        chooseInventoryQuantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });
        inventorySelectionPanel.add(chooseInventoryQuantity);

        JPanel addedItemsPanel = new JPanel();
        addedItemsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JTextArea addedItemsTextArea = new JTextArea(5, 35);
        addedItemsPanel.add(addedItemsTextArea);

        addedItemsPanel.add(new JSeparator(SwingConstants.VERTICAL));

        JTextArea addedQuantityTextArea = new JTextArea(5, 35);
        addedItemsPanel.add(addedQuantityTextArea);

        JButton inventorySubmitButton = new JButton("Add to inventory");
        inventorySubmitButton.addActionListener(e -> {
            selectedInventory.put(chooseInventoryItems.getText(), Integer.parseInt(chooseInventoryQuantity.getText()));
            if (!addedItemsTextArea.getText().equals("")){
                addedItemsTextArea.append("\n");
            }
            addedItemsTextArea.append("Item: " + chooseInventoryItems.getText());

            if (!addedQuantityTextArea.getText().equals("")){
                addedQuantityTextArea.append("\n");
            }
            addedQuantityTextArea.append("Quantity: " + chooseInventoryQuantity.getText());

            chooseInventoryItems.setText("");
            chooseInventoryQuantity.setText("");
        });
        inventorySelectionPanel.add(inventorySubmitButton);

        mainPanel.add(inventorySelectionPanel);
        mainPanel.add(addedItemsPanel);
         */
        /**
         * Start button
         */
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            started = true;
            muleName = muleNameField.getText();
            Close();
        });
        mainPanel.add(startButton);
        mainDialog.pack();
    }

    public boolean isStarted() {
        return started;
    }

    public Enums.AccountType getSelectedAccountType() {
        return (Enums.AccountType) accountTypeSelector.getSelectedItem();
    }

    public Enums.AccountStatus getSelectedAccountStatus() {
        return (Enums.AccountStatus) accountStatusSelector.getSelectedItem();
    }

    public void Open(){
        mainDialog.setVisible(true);
    }

    public void Close(){
        mainDialog.setVisible(false);
        mainDialog.dispose();
    }

    // HashMap temporaryInventory = new HashMap(String, Int)
    // temporaryInventory.put
    // Return to AccountTrainer

    public void PassData(){
        data.SetAccountType(getSelectedAccountType());
        data.SetAccountStatus(getSelectedAccountStatus());
        //data.SetInventory(selectedInventory);
        data.SetMule(muleName);
    }
}
