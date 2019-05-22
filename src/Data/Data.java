package Data;

import Utilities.Enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.Script;

public class Data {
    /* **************************************************
     *                                                  *
     *                      Fields                      *
     *                                                  *
     ****************************************************/
    private Script script;

    // Account type and status
    private static Enums.AccountType type;
    private static Enums.AccountStatus status;

    // Equipment
    private HashMap<String, Integer> equipmentNeeded = new HashMap<>();

    // Inventory
    private HashMap<String, Integer> inventoryItemsAndQuantity = new HashMap<>();

    // Attack style
    private Enums.Styles currentStyle;

    // Mule
    private String mule;
    private boolean mulingComplete;

    // Combat stats
    private int startingAttackLevel;
    private int startingStrengthLevel;
    private int goalAttackLevel;
    private int goalStrengthLevel;
    private List<Integer> attackLevelMilestones = new ArrayList<>();
    private List<Integer> strengthLevelMilestones = new ArrayList<>();

    /* **************************************************
     *                                                  *
     *                  Constructors                    *
     *                                                  *
     ****************************************************/

    /*
     * Set script equal to the script that instantiated this class
     * set MulingComplete to false
     *
     * @param script - Reference to the script that created an instance
     *
     */
    public Data(Script script){
        this.script = script;
        mulingComplete = false;
    }

    /* **************************************************
     *                                                  *
     *                  Methods                         *
     *                                                  *
     ****************************************************/
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

    public HashMap GetInventory(){
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

    public int GetStartingAttackLevel(){
        return startingAttackLevel;
    }

    public void SetStartingAttackLevel(int level){
        this.startingAttackLevel = level;
    }

    public int GetStartingStrengthLevel(){
        return startingStrengthLevel;
    }

    public void SetStartingStrengthLevel(int level){
        this.startingStrengthLevel = level;
    }

    public int GetGoalAttackLevel(){
        return goalAttackLevel;
    }

    public void SetGoalAttackLevel(int level){
        this.goalAttackLevel = level;
    }

    public int GetGoalStrengthLevel(){
        return goalStrengthLevel;
    }

    public void SetGoalStrengthLevel(int level){
        this.goalStrengthLevel = level;
    }

    public void CalcAttackLevelMilestones(){
        for (int i = 1; ((i + 10) / 10) * 10 <= goalAttackLevel; i += 10){
            attackLevelMilestones.add(((i + 10) / 10) * 10);
        }
    }

    public void CalcStrengthLevelMilestones(){
        for (int i = 1; ((i + 10) / 10) * 10 <= goalStrengthLevel; i += 10){
            strengthLevelMilestones.add(((i + 10) / 10) * 10);
        }
    }

    public List<Integer> GetAttackLevelMilestones(){
        return attackLevelMilestones;
    }

    public List<Integer> GetStrengthLevelMilestones(){
        return strengthLevelMilestones;
    }

    public boolean CheckAttackLevelMilestone(int level){
        return attackLevelMilestones.contains(level);
    }

    public boolean CheckStrengthLevelMilestone(int level){
        return strengthLevelMilestones.contains(level);
    }
}
