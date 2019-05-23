package Core;

import Data.*;
import Utilities.*;
import View.*;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@ScriptManifest(name = "Account Trainer", author = "Dockerexe", version = 1.0, info = "", logo = "")

public class AccountTrainer extends Script {

    /* **************************************************
     *                                                  *
     *                      Fields                      *
     *                                                  *
     ****************************************************/
    // Static class references
    private static Data data;
    private static Gui gui;
    private static MuleHandler muleHandler;
    private static AttackStyleHandler attackStyleHandler;
    private static BankingHandler bank;
    private static RandomUtil randomUtil = new RandomUtil();

    // State handler
    private enum PlayerStates { MULE, ARMOREQUIPMENT, EQUIPARMOR, WEAPONEQUIPMENT, EVENTRPG, INVENTORY, BANKING, WALKTOSPOT, TRAIN, BREAKING }
    private PlayerStates currentState;
    private PlayerStates previousState;

    private boolean attackLevelUpDetected = false;
    private boolean strengthLevelUpDetected = false;

    // Areas
    private Area cowArea = new Area(
            new int[][]{
                    { 2916, 3291 },
                    { 2927, 3291 },
                    { 2927, 3287 },
                    { 2929, 3284 },
                    { 2929, 3281 },
                    { 2922, 3281 }
            }
    );

    private Area diangoArea = new Area(3076, 3254, 3085, 3245);

    /* **************************************************
     *                                                  *
     *                  Constructors                    *
     *                                                  *
     ****************************************************/

    /**
     * Create a new instance of Data, AttackStyleHandler and BankingHandler
     * and pass this script instance.
     *
     */
    public AccountTrainer() {
        data = new Data(this);
        attackStyleHandler = new AttackStyleHandler(this);
        bank = new BankingHandler(this);
    }

    /* **************************************************
     *                                                  *
     *                  Methods                         *
     *                                                  *
     ****************************************************/

    /**
     * Call GuiInitializer to initialise the GUI, get the current attack style using method found within
     * AttackStyleHandler class to get current attack style and set value within Data class.
     *
     * If the account is a fresh class then set our state to MULE to initiate trade with mule for starting items
     * otherwise set state to ARMOREQUIPMENT and begin checking/equipping gear required for account type.
     *
     * Set the attack and strength level milestones based on account type.
     */
    @Override
    public void onStart() {
        GuiInitializer();

        data.SetCurrentStyle(attackStyleHandler.GetAttackStyle());

        getBot().addPainter(new PaintHandler());

        if(myPlayer().getCombatLevel() == 3){
            currentState = PlayerStates.MULE;
        } else {
            currentState = PlayerStates.ARMOREQUIPMENT;
        }

        data.SetStartingAttackLevel(GetLevel(Skill.ATTACK));
        data.SetStartingStrengthLevel(GetLevel(Skill.STRENGTH));

        data.CalcAttackLevelMilestones();
        data.CalcStrengthLevelMilestones();

        previousState = currentState;
    }

    @Override
    public int onLoop() throws InterruptedException {
        log("Current " + currentState);
        log("Prev " + previousState);
        if (currentState != PlayerStates.BREAKING && CheckTimeTilBreak() == true){
            previousState = currentState;
            currentState = PlayerStates.BREAKING;
        } else {
            if (myPlayer().getHealthPercent() < randomUtil.gRandomBetween(15, 35)) {
                sleep(randomUtil.gRandomBetween(500, 3000));
                EatFood();
            }
            new ConditionalSleep(5000) {
                @Override
                public boolean condition() {
                    return !myPlayer().isAnimating();
                }
            }.sleep();

            if (settings.getRunEnergy() > random(20, 45) && !settings.isRunning() && !myPlayer().isUnderAttack()) {
                settings.setRunning(true);
            }
        }

        switch(currentState){
            case MULE:
                Muling();
                break;

            case ARMOREQUIPMENT:
                ArmorEquipmentSetup();
                break;

            case EQUIPARMOR:
                EquipArmor();
                break;

            case WEAPONEQUIPMENT:
                WeaponEquipmentSetup();
                break;
            case EVENTRPG:
                GetEventRpg();
                break;

            case INVENTORY:
                WithdrawInventory();
                break;
            case WALKTOSPOT:
                WalkToTrainingSpot();
                break;
            case TRAIN:
                TrainCombat();
                break;

            case BANKING:
                Banking();
                break;

            case BREAKING:
                sleep(random(300,500));
                break;

            default:
                break;
        }

        return randomUtil.gRandomBetween(200, 800); //The amount of time in milliseconds before the loop starts over
    }

    /**
     * Invoke the GUI and wait until GUI is closed, if GUI closed without pressing start then stop script, otherwise,
     * pass data inputted into GUI to our Data instance
     *
     */
    private void GuiInitializer(){
        try {
            SwingUtilities.invokeAndWait(() -> {
                gui = new Gui(this, data);
                gui.Open();
            });
        } catch (InterruptedException | InvocationTargetException e){
            e.printStackTrace();
            stop();
            return;
        }

        if (!gui.isStarted()){
            stop();
            return;
        }

        gui.PassData();
    }

    /**
     * Check for players position within the Lumbridge back, if they are not found then walk to the bank.
     *
     * If player is in the bank then deposit all items into the bank.
     *
     * If muling has not been completed then create an instance of MuleHandler, pass the script and mule name entered
     * in GUI to the constructor, run the InteractWithMule function found within the MuleHandler instance and pass the
     * return to data instance confirming whether successful.
     *
     * If mulingComplete value stored in data returns true then deposit all items received into the bank and change
     * state to ARMOREQUIPMENT.
     */
    private void Muling() throws InterruptedException {
        if(!Banks.LUMBRIDGE_UPPER.contains(myPosition())) {
            getWalking().webWalk(Banks.LUMBRIDGE_UPPER);
        } else if (!getInventory().isEmpty()){
            bank.OpenBank();
            sleep(randomUtil.gRandomBetween(200, 500));
            bank.DepositInventory();
            sleep(randomUtil.gRandomBetween(200, 500));
            bank.CloseBank();
            sleep(randomUtil.gRandomBetween(200, 500));
        } else if(!data.GetMulingComplete()) {
            muleHandler = new MuleHandler(this, data.GetMule());
            data.SetMulingComplete(muleHandler.InteractWithMule());
            sleep(randomUtil.gRandomBetween(700, 1500));

            if(data.GetMulingComplete()){
                bank.OpenBank();
                bank.DepositInventory();
                new ConditionalSleep(15000) {
                    @Override
                    public boolean condition() {
                        return getInventory().isEmpty();
                    }
                }.sleep();
                sleep(randomUtil.gRandomBetween(300, 1500));
                currentState = PlayerStates.ARMOREQUIPMENT;
            }
        }
    }

    /**
     * Check if each armor item required is currently being worn by the player, if not then pass the item needed to
     * the Data instance to be stored in HashMap for withdrawal.
     *
     * If the HashMap is empty then all armor needed is currently worn so skip to checking for weapon, otherwise,
     * deposit all items in inventory and withdraw all armor needed.
     */
    private void ArmorEquipmentSetup() throws InterruptedException {
        if (!equipment.isWearingItem(EquipmentSlot.CAPE, "Team-13 cape")) {
            data.AddEquipmentToEquip("Team-13 cape", 1);
        }

        if (!equipment.isWearingItem(EquipmentSlot.HAT, "Iron Full Helm")) {
            data.AddEquipmentToEquip("Iron Full Helm", 1);
        }

        if (!equipment.isWearingItem(EquipmentSlot.AMULET, "Amulet of power")) {
            data.AddEquipmentToEquip("Amulet of power", 1);
        }

        if (!equipment.isWearingItem(EquipmentSlot.CHEST, "Iron Platebody")) {
            data.AddEquipmentToEquip("Iron platebody", 1);
        }

        if (!equipment.isWearingItem(EquipmentSlot.LEGS, "Iron platelegs")) {
            data.AddEquipmentToEquip("Iron platelegs", 1);
        }

        if (data.GetAccountType() != Enums.AccountType.OBBY_MAUL) {
            if (!equipment.isWearingItem(EquipmentSlot.SHIELD, "Iron kiteshield")) {
                data.AddEquipmentToEquip("Iron kiteshield", 1);
            }
        }

        if (data.GetEquipmentToEquip().isEmpty()) {
            sleep(randomUtil.gRandomBetween(300, 500));
            currentState = PlayerStates.WEAPONEQUIPMENT;
        } else {
            if (inventory.getEmptySlotCount() < 28){
                bank.WalkToBank();
                bank.OpenBank();
                sleep(randomUtil.gRandomBetween(300, 500));
                bank.DepositInventory();
                sleep(randomUtil.gRandomBetween(300, 500));
                bank.CloseBank();
            }
            log("Withdrawing armor");
            if (bank.WithdrawEquipment(data.GetEquipmentToEquip())) { // Attempt to withdraw items needed and return true if successful
                sleep(randomUtil.gRandomBetween(300, 500));
                currentState = PlayerStates.EQUIPARMOR;
            }
        }
    }

    private void EquipArmor() throws InterruptedException {
        List<Item> armor = inventory.filter( item -> item.hasAction("Wear"));
        for (Item item : armor){
            item.interact("Wear");
            sleep(randomUtil.gRandomBetween(200, 400));
        }
        sleep(randomUtil.gRandomBetween(300, 500));
        currentState = PlayerStates.WEAPONEQUIPMENT;
    }

    /**
     * If the account type selected during GUI setup is OBBY_MAUL then:
     * Check Event rpg is equipped > Check if Event rpg is in inventory and equip > check bank for Event rpg and
     * equip > go to Draynor bank, deposit all and start EVENTRPG state.
     *
     * if the account type selected during GUI setup is F2P_MELEE then:
     * Get players attack level > set weaponName equip to best weapon for level > check if weapon is equipped >
     * Check if weapon is in inventory and equip > check bank for weapon and equip ELSE stop script
     */
    private void WeaponEquipmentSetup() throws InterruptedException {
        String weaponName;
        if(data.GetAccountType() == Enums.AccountType.OBBY_MAUL) {
        attackStyleHandler.SwitchAttackStyle(data.GetCurrentStyle(), Enums.Styles.STRENGTH);
        data.SetCurrentStyle(Enums.Styles.STRENGTH);
            sleep(randomUtil.gRandomBetween(300, 500));
            if (!getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Event rpg")) {
                if (inventory.contains("Event rpg")){
                    getInventory().getItem("Event rpg").interact("Wield");
                } else if (bank.WithdrawWeapon("Event rpg", false)) {
                    getInventory().getItem("Event rpg").interact("Wield");
                } else {
                    bank.WalkToBank(Banks.DRAYNOR);
                    bank.OpenBank();
                    bank.DepositInventory();
                    currentState = PlayerStates.EVENTRPG;
                }
            } else {
                currentState = PlayerStates.INVENTORY;
            }
        } else if (data.GetAccountType() == Enums.AccountType.F2P_MELEE){
            int attackLevel = GetLevel(Skill.ATTACK);
            if (attackLevel < 10){
                weaponName = "Iron sword";
            } else if (attackLevel >= 10 && attackLevel < 20){
                weaponName = "Black sword";
            } else if (attackLevel >= 20 && attackLevel < 30) {
                weaponName = "Mithril sword";
            } else {
                weaponName = "Adamant sword";
            }

            if (!getEquipment().isWearingItem(EquipmentSlot.WEAPON, weaponName)){
                log("Withdrawing weapon");
                if (inventory.contains(weaponName)) {
                    getInventory().getItem(weaponName).interact("Wield");
                    currentState = PlayerStates.INVENTORY;
                } else if (bank.WithdrawWeapon(weaponName, true)){
                    getInventory().getItem(weaponName).interact("Wield");
                    currentState = PlayerStates.INVENTORY;
                }  else {
                    log("Weapon " + weaponName + " not found, stopping script.");
                    stop();
                }
            } else {
                currentState = PlayerStates.INVENTORY;
            }
        }
    }

    /**
     * Check if inventory contains 225g for Event rpg otherwise withdraw x coins needed > Check if NPC area contains
     * player otherwise walk to area > if store isn't open and player is not moving then open store > buy Event rpg
     */
    private void GetEventRpg() throws InterruptedException {
        if (!inventory.contains("Coins") || inventory.getAmount("Coins") < 225) {
            getBank().withdraw("Coins", 225 - (int) inventory.getAmount("Coins"));
            sleep(randomUtil.gRandomBetween(300, 500));
        } else if (!diangoArea.contains(myPosition())) {
            getWalking().webWalk(diangoArea);

            new ConditionalSleep(5000) {
                @Override
                public boolean condition() {
                    return diangoArea.contains(myPosition());
                }
            }.sleep();
            sleep(randomUtil.gRandomBetween(300, 500));
        } else if (!store.isOpen() && !myPlayer().isMoving()) {
            log("open shop");
            NPC diango = npcs.closest("Diango");
            if (diango != null) {
                diango.interact("Trade");

                new ConditionalSleep(5000) {
                    @Override
                    public boolean condition() {
                        return store.isOpen();
                    }
                }.sleep();
                sleep(randomUtil.gRandomBetween(300, 500));
            }
        } else if (store.isOpen()){
            store.buy("Event rpg", 1);
            new ConditionalSleep(5000) {
                @Override
                public boolean condition() {
                    return inventory.contains("Event rpg");
                }
            }.sleep();
            sleep(randomUtil.gRandomBetween(300, 500));
            store.close();
            currentState = PlayerStates.WEAPONEQUIPMENT;
        }
    }


    private void WithdrawInventory() throws  InterruptedException {
        log("Withdrawing inventory");
        data.AddInventoryItem("Tuna", 28);
        if (!inventory.contains("Tuna")){
            bank.WalkToBank();
            bank.OpenBank();
            sleep(randomUtil.gRandomBetween(300, 500));
            bank.DepositInventory();

            if (!data.GetInventory().isEmpty()) {
                bank.WithdrawInventory(data.GetInventory());
            }
        }
        sleep(randomUtil.gRandomBetween(300, 500));
        currentState = PlayerStates.WALKTOSPOT;
    }

    private void WalkToTrainingSpot(){
        log("Walking to training spot");
        if (!cowArea.contains(myPosition())){
            getWalking().webWalk(cowArea);
        }
        currentState = PlayerStates.TRAIN;
    }


    private void TrainCombat() throws InterruptedException{
        IfChangeAttackStyleNeeded();

        NPC cow = npcs.closest(new Filter<NPC>() {
            @Override
            public boolean match(NPC npc) {
                return npc != null && npc.getName().contains("Cow") && !npc.getName().contains("Dairy")
                        && !npc.isUnderAttack() && npc.isAttackable() && cowArea.contains(npc);

            }
        });

        if (!myPlayer().isMoving() && !myPlayer().isUnderAttack() && cow != null && !getCombat().isFighting()) {
            sleep(randomUtil.gRandomBetween(300, 500));
            cow.interact("Attack");
            new ConditionalSleep(5000) {
                @Override
                public boolean condition() {
                    return getCombat().isFighting();
                }
            }.sleep();
        }

        if(!myPlayer().isUnderAttack()){
            if (!inventory.contains("Tuna")){
                sleep(randomUtil.gRandomBetween(300, 500));
                currentState = PlayerStates.BANKING;
            }
        }
    }
    private void SimulateAfk(PlayerStates previousState) throws InterruptedException {
        sleep(randomUtil.gRandomBetween(1000, 30000));
        currentState = previousState;
    }

    @Override
    public void onMessage(Message msg){
        String message = msg.getMessage();
        if (message.contains("just advanced your Attack level")) {
            attackLevelUpDetected = true;
        } else if (message.contains("just advanced your Strength level")) {
            strengthLevelUpDetected = true;
        } else if (message.contains("Welcome to Old School RuneScape")){
            currentState = previousState;
        }
    }

    private int GetLevel(Skill skill){
        return getSkills().getStatic(skill);
    }

    private void IfChangeAttackStyleNeeded() throws InterruptedException{
        if (attackLevelUpDetected == true){
            randomUtil.gRandomBetween(1000,2500);
            if(data.GetAccountType() != Enums.AccountType.OBBY_MAUL){
                if (attackStyleHandler.GetAttackStyle() == Enums.Styles.ATTACK){
                    if (data.CheckAttackLevelMilestone(GetLevel(Skill.ATTACK))){
                        if (GetLevel(Skill.STRENGTH) < data.GetGoalStrengthLevel()) {
                            attackStyleHandler.SwitchAttackStyle(Enums.Styles.ATTACK, Enums.Styles.STRENGTH);
                            data.SetCurrentStyle(Enums.Styles.STRENGTH);
                        }
                        new ConditionalSleep(10000) {
                            @Override
                            public boolean condition() {
                                return !myPlayer().isUnderAttack();
                            }
                        }.sleep();
                        WeaponEquipmentSetup();
                        currentState = PlayerStates.WEAPONEQUIPMENT;
                    }
                }
            }
            attackLevelUpDetected = false;
        }
        if(strengthLevelUpDetected == true) {
            randomUtil.gRandomBetween(1000,2500);
            if (attackStyleHandler.GetAttackStyle() == Enums.Styles.STRENGTH) {
                if (data.CheckStrengthLevelMilestone(GetLevel(Skill.STRENGTH))) {
                    if (GetLevel(Skill.ATTACK) < data.GetGoalAttackLevel()) {
                        attackStyleHandler.SwitchAttackStyle(Enums.Styles.STRENGTH, Enums.Styles.ATTACK);
                        data.SetCurrentStyle(Enums.Styles.ATTACK);
                    }
                }
            }
        }
    }

    private void EatFood() throws InterruptedException {
        List<Item> food = inventory.filter( item -> item.hasAction("Eat") );
        if (!food.isEmpty()){
            food.get(0).interact("Eat");
        }
        sleep(500);
    }

    private void Banking() throws InterruptedException {
        WeaponEquipmentSetup();
        WithdrawInventory();
        currentState = PlayerStates.WALKTOSPOT;
    }

    private boolean CheckTimeTilBreak(){
        int minsUntilBreak = getBot().getRandomExecutor().getTimeUntilBreak();
        if(minsUntilBreak == 0){
            log("Break approaching");
            return true;
        }
        return false;
    }

    /**
    public void SelectStartingInventory(){
        switch(data.GetAccountType()) {
            case F2P_MELEE:
                data.AddInventoryItem("Tuna", 28);
                break;

            case OBBY_MAUL:
                data.AddInventoryItem("Tuna", 28);
                break;
        }
    }
     */
}