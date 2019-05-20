package Data;

import Utilities.Enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.Script;

public class Data {
    private static Script script;

    // Account type and status
    private static Enums.AccountType type;
    private static Enums.AccountStatus status;

    // Equipment
    private HashMap<String, Integer> equipmentNeeded = new HashMap<String, Integer>();
    // Inventory
    private HashMap<String, Integer> inventoryItemsAndQuantity = new HashMap<String, Integer>();

    // Attack style
    Enums.Styles currentStyle;

    // Mule
    String mule;
    boolean mulingComplete;

    public Data(Script script){
        this.script = script;
        mulingComplete = false;
    }

    public void SetAccountType(Enums.AccountType type) {
        Data.type = type;
    }

    public void SetAccountStatus(Enums.AccountStatus status) {
        Data.status = status;
    }

    public Enums.AccountType GetAccountType(){
        return type;
    }

    public Enums.AccountStatus GetAccountStatus(){
        return status;
    }

    public Map GetInventory(){
        return inventoryItemsAndQuantity;
    }

    public void AddInventoryItem(String item, int quantity){
        inventoryItemsAndQuantity.put(item, quantity);
    }

    public Enums.Styles GetCurrentStyle() {
        return currentStyle;
    }

    public void SetCurrentStyle(Enums.Styles style) {
        currentStyle = style;
    }

    public String GetMule(){
        return mule;
    }

    public void SetMule(String mule){
        this.mule = mule;
    }

    public boolean GetMulingComplete(){
        return mulingComplete;
    }

    public void SetMulingComplete(boolean bool){
        this.mulingComplete = bool;
    }

    public void AddEquipmentToEquip(String item, Integer quantity){
        equipmentNeeded.put(item, quantity);
    }

    public HashMap GetEquipmentToEquip(){
        return equipmentNeeded;
    }
}
