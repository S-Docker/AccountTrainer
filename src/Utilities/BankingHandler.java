package Utilities;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;
import java.util.Map;
import java.util.Map.Entry;

import java.util.HashMap;

public class BankingHandler {
    private Area[] bankAreas = new Area[]{Banks.AL_KHARID, Banks.ARCEUUS_HOUSE, Banks.ARDOUGNE_NORTH, Banks.ARDOUGNE_SOUTH, Banks.CAMELOT, Banks.CANIFIS,
            Banks.CASTLE_WARS, Banks.CATHERBY, Banks.DRAYNOR, Banks.DUEL_ARENA, Banks.EDGEVILLE, Banks.FALADOR_EAST, Banks.FALADOR_WEST,
            Banks.GNOME_STRONGHOLD, Banks.GRAND_EXCHANGE, Banks.HOSIDIUS_HOUSE, Banks.LOVAKENGJ_HOUSE, Banks.LOVAKITE_MINE,
            Banks.LUMBRIDGE_LOWER, Banks.LUMBRIDGE_UPPER, Banks.PEST_CONTROL, Banks.PISCARILIUS_HOUSE, Banks.SHAYZIEN_HOUSE,
            Banks.TZHAAR, Banks.VARROCK_EAST, Banks.VARROCK_WEST, Banks.YANILLE};

    private Script script;

    public BankingHandler(Script script){
        this.script = script;
    }

    private void WalkToBank(){
        script.getWalking().webWalk(bankAreas);
    }

    public void WalkToBank(Area bank) {
        script.getWalking().webWalk(bank);
    }

    public void OpenBank() throws InterruptedException {
        if (!script.getBank().isOpen()) {
            script.log("Opening bank");
            script.getBank().open();
            new ConditionalSleep(15000) {
                @Override
                public boolean condition() {
                    return script.getBank().isOpen();
                }
            }.sleep();
        }
    }

    public void CloseBank() {
        script.getBank().close();
    }

    public void DepositInventory() {
        script.getBank().depositAll();
    }

    public boolean WithdrawEquipment(HashMap<String, Integer> map) throws InterruptedException {
        WalkToBank();
        OpenBank();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (script.getBank().contains(entry.getKey())) {
                script.getBank().withdraw(entry.getKey(), entry.getValue());
            } else {
                script.log("Armor not found");
                script.stop();
            }
        }

        CloseBank();
        return true;
    }

    public boolean WithdrawWeapon(String weapon) throws InterruptedException {
        WalkToBank();
        OpenBank();
        if (script.getBank().contains(weapon)){
            script.getBank().withdraw(weapon, 1);
        } else {
            script.log("Weapon not found");
            script.stop();
        }

        CloseBank();
        return true;
    }

    public boolean WithdrawInventory(HashMap<String, Integer> map) throws InterruptedException {
        WalkToBank();
        OpenBank();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (script.getBank().contains(entry.getKey())) {
                script.getBank().withdraw(entry.getKey(), entry.getValue());
            } else {
                script.log("Inventory Item " + entry.getKey() + " not found");
                script.stop();
            }
        }

        CloseBank();
        return true;
    }
}
