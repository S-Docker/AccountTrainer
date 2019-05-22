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

class Gui {
    /* **************************************************
     *                                                  *
     *                      Fields                      *
     *                                                  *
     ****************************************************/
    private final JDialog mainDialog;
    private Data data;
    private boolean started;

    Map<String, Integer> selectedInventory = new HashMap<>();
    Map<String, Integer> itemsNeeded = new HashMap<>();

    String accountType;
    private String muleName;
    Enums.AccountStatus accountStatus;
    //final JComboBox<Enums.AccountStatus> accountStatusSelector;
    private final JComboBox<Enums.AccountType> accountTypeSelector;

    /* **************************************************
     *                                                  *
     *                  Constructors                    *
     *                                                  *
     ****************************************************/

    /*
     * Set script equal to the script that instantiated this class
     * Create new GUI layout with necessary panels, text fields and buttons
     * using java swing and then pack pack.
     *
     * @param script - Reference to the script that created an instance
     * @param data - Reference to the data instance instantiated by script
     *
     */
    Gui(Script script, Data data){
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
         *
        JPanel accountStatusSelectionPanel = new JPanel();
        accountStatusSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel accountStatusSelectionLabel = new JLabel("Select account status: ");
        accountStatusSelectionPanel.add(accountStatusSelectionLabel);

        accountStatusSelector = new JComboBox<>(Enums.AccountStatus.values());
        accountStatusSelectionPanel.add(accountStatusSelector);

        mainPanel.add(accountStatusSelectionPanel);
         */

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
         *  Goal levels Selection
         *
         JPanel goalAttackLevelPanel = new JPanel();
         goalAttackLevelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

         JLabel goalAttackLevelLabel = new JLabel("Attack level goal: ");
         goalAttackLevelPanel.add(goalAttackLevelLabel);

         JTextField goalAttackLevelField = new JTextField(2);
         goalAttackLevelField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
        e.consume();
        }
        }
        });
         goalAttackLevelPanel.add(goalAttackLevelField);

         mainPanel.add(goalAttackLevelPanel);

         JPanel goalStrengthLevelPanel = new JPanel();
         goalStrengthLevelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

         JLabel goalStrengthLevelLabel = new JLabel("Strength level goal: ");
         goalStrengthLevelPanel.add(goalStrengthLevelLabel);

         JTextField goalStrengthLevelField = new JTextField(2);
         goalStrengthLevelField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
        e.consume();
        }
        }
        });
         goalStrengthLevelPanel.add(goalStrengthLevelField);

         mainPanel.add(goalStrengthLevelPanel);

         /**
         * Inventory manager
         *
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

    /* **************************************************
     *                                                  *
     *                  Methods                         *
     *                                                  *
     ****************************************************/
    boolean isStarted() {
        return started;
    }

    private Enums.AccountType getSelectedAccountType() {
        return (Enums.AccountType) accountTypeSelector.getSelectedItem();
    }

    /**
    public Enums.AccountStatus getSelectedAccountStatus() {
        return (Enums.AccountStatus) accountStatusSelector.getSelectedItem();
    }*/

    void Open(){
        mainDialog.setVisible(true);
    }

    private void Close(){
        mainDialog.setVisible(false);
        mainDialog.dispose();
    }

    // HashMap temporaryInventory = new HashMap(String, Int)
    // temporaryInventory.put
    // Return to AccountTrainer

    void PassData(){
        data.SetAccountType(getSelectedAccountType());
        //data.SetAccountStatus(getSelectedAccountStatus());
        //data.SetInventory(selectedInventory);
        data.SetMule(muleName);

        if (getSelectedAccountType() == Enums.AccountType.OBBY_MAUL){
            data.SetGoalAttackLevel(1);
            data.SetGoalStrengthLevel(60);
        } else if (getSelectedAccountType() == Enums.AccountType.F2P_MELEE){
            data.SetGoalAttackLevel(40);
            data.SetGoalStrengthLevel(60);
        }
    }
}
