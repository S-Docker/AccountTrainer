package Core;

import Data.*;
import Utilities.*;
import View.*;

import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@ScriptManifest(name = "Account Trainer", author = "Dockerexe", version = 1.0, info = "", logo = "")

public class AccountTrainer extends Script {

    // Static class references
    private static Data data;
    private static Gui gui;
    private static MuleHandler muleHandler;
    private static AttackStyleHandler attackStyleHandler;
    private static BankingHandler bank;
    private static RandomUtil randomUtil = new RandomUtil();

    // State handler
    enum PlayerStates { MULE, ARMOREQUIPMENT, EQUIPARMOR, WEAPONEQUIPMENT, EVENTRPG, INVENTORY, BANKING, TRAIN };
    PlayerStates currentState;


    /** ____________________
     * |                    |
     * |      Methods       |
     * |____________________|
     */
    public AccountTrainer() {
        data = new Data(this);
        attackStyleHandler = new AttackStyleHandler(this);
        bank = new BankingHandler(this);
    }

    @Override
    public void onStart() throws InterruptedException {
        GuiInitializer();

        Enums.Styles currentStyle = attackStyleHandler.GetStartingStyle();
        data.SetCurrentStyle(currentStyle);

        getBot().addPainter(new PaintHandler());

        if(data.GetAccountStatus() == Enums.AccountStatus.FRESH_ACCOUNT){
            currentState = PlayerStates.MULE;
        } else {
            currentState = PlayerStates.ARMOREQUIPMENT;
        }

    }

    @Override
    public int onLoop() throws InterruptedException {
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

            case INVENTORY:
                WithdrawInventory();
                break;

            default:
                break;
        }
        if(data.GetAccountStatus() == Enums.AccountStatus.FRESH_ACCOUNT) {

        }

        return randomUtil.gRandomBetween(200, 800); //The amount of time in milliseconds before the loop starts over
    }

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

    void Muling() throws InterruptedException {
        if(!Banks.LUMBRIDGE_UPPER.contains(myPosition())) {
            getWalking().webWalk(Banks.LUMBRIDGE_UPPER);
        }

        if (!getInventory().isEmpty()){
            bank.OpenBank();
            sleep(randomUtil.gRandomBetween(200, 500));
            bank.DepositInventory();
            sleep(randomUtil.gRandomBetween(200, 500));
            bank.CloseBank();
            sleep(randomUtil.gRandomBetween(200, 500));
        }

        if(!data.GetMulingComplete()) {
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

    void ArmorEquipmentSetup() throws InterruptedException {
        if (!equipment.isWearingItem(EquipmentSlot.CAPE, "Team-13 cape")){
            data.AddEquipmentToEquip("Team-13 cape", 1);
        }

        if (!equipment.isWearingItem(EquipmentSlot.HAT, "Iron Full Helm")){
            data.AddEquipmentToEquip("Iron Full Helm", 1);
        }

        if (!equipment.isWearingItem(EquipmentSlot.AMULET, "Amulet of power")){
            data.AddEquipmentToEquip("Amulet of power", 1);
        }

        if (!equipment.isWearingItem(EquipmentSlot.CHEST, "Iron Platebody")){
            data.AddEquipmentToEquip("Iron platebody", 1);
        }

        if (!equipment.isWearingItem(EquipmentSlot.LEGS, "Iron platelegs")){
            data.AddEquipmentToEquip("Iron platelegs", 1);
        }

        if(data.GetAccountType() != Enums.AccountType.OBBY_MAUL){
            if (!equipment.isWearingItem(EquipmentSlot.SHIELD, "Iron kiteshield")){
                data.AddEquipmentToEquip("Iron kiteshield", 1);
            }
        }

        if (data.GetEquipmentToEquip().isEmpty()){
            currentState = PlayerStates.WEAPONEQUIPMENT;
        } else {
            if (bank.WithdrawEquipment(data.GetEquipmentToEquip())) {
                currentState = PlayerStates.EQUIPARMOR;
            }
        }
    }

    public void EquipArmor() throws InterruptedException {
        List<Item> armor = inventory.filter( item -> item.hasAction("Wear"));
        for (Item item : armor){
            item.interact("Wear");
            sleep(randomUtil.gRandomBetween(100, 300));
        }
        sleep(randomUtil.gRandomBetween(300, 500));
        currentState = PlayerStates.WEAPONEQUIPMENT;
    }

    public void WeaponEquipmentSetup() throws InterruptedException {
        String weaponName;
        if(data.GetAccountType() == Enums.AccountType.OBBY_MAUL) {
            if (!getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Event rpg")) {
                if (bank.WithdrawWeapon("Event rpg")) {
                    getInventory().getItem("Event rpg").interact("Wield");
                } else {
                    currentState = PlayerStates.EVENTRPG;
                }
            } else {
                currentState = PlayerStates.INVENTORY;
            }
        } else if (data.GetAccountType() == Enums.AccountType.F2P_MELEE){
            int attackLevel = getSkills().getStatic(Skill.ATTACK);
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
                if (bank.WithdrawWeapon(weaponName)) {
                    getInventory().getItem(weaponName).interact("Wield");
                    currentState = PlayerStates.INVENTORY;
                } else {
                    log("Weapon " + weaponName + " not found, stopping script.");
                    stop();
                }
            } else {
                currentState = PlayerStates.INVENTORY;
            }
        }
    }

    public void EquipWeapon() throws InterruptedException {
        Item weapon = getInventory().getItem(item -> item.hasAction("Wield"));
        weapon.interact("Wield");
        sleep(randomUtil.gRandomBetween(300, 500));
        currentState = PlayerStates.WEAPONEQUIPMENT;
    }

    public void WithdrawInventory(){

    }

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
}