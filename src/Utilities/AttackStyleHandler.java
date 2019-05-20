package Utilities;

import Utilities.Enums;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.utility.ConditionalSleep;
import org.osbot.rs07.script.Script;


public class AttackStyleHandler {
    private static Script script;

    public AttackStyleHandler(Script script){
        this.script = script;
    }

    public Enums.Styles GetStartingStyle() {
        int attackOptions = script.getConfigs().get(43);
        switch (attackOptions){
            case 0:
                return Enums.Styles.ATTACK;

            case 1:
                return Enums.Styles.STRENGTH;

            case 2:
                RS2Widget strengthOption = script.getWidgets().get(593, 7);
                strengthOption.interact();

                new ConditionalSleep(5000) {
                    public boolean condition() {
                        return (script.getConfigs().get(43) == 1);
                    }
                }.sleep();
                return Enums.Styles.STRENGTH;

            case 3:
                return Enums.Styles.DEFENCE;
        }
        return null;
    }

    public void SwitchAttackStyle(Enums.Styles currentStyle, Enums.Styles newStyle){
        script.log("Switching attack style.");

        script.getTabs().open(Tab.ATTACK);

        if (currentStyle != newStyle){
            switch (newStyle){
                case ATTACK:
                    RS2Widget attackOption = script.getWidgets().get(593, 3);
                    attackOption.interact();

                    new ConditionalSleep(5000) {
                        public boolean condition() {
                            return (script.getConfigs().get(43) == 0);
                        }
                    }.sleep();
                    script.log("Switched to attack mode.");
                    break;

                case STRENGTH:
                    RS2Widget strengthOption = script.getWidgets().get(593, 7);
                    strengthOption.interact();

                    new ConditionalSleep(5000) {
                        public boolean condition() {
                            return (script.getConfigs().get(43) == 1);
                        }
                    }.sleep();
                    script.log("Switched to strength mode.");
                    break;

                case DEFENCE:
                    RS2Widget defenceOption = script.getWidgets().get(593, 15);
                    defenceOption.interact();

                    new ConditionalSleep(5000) {
                        public boolean condition() {
                            return (script.getConfigs().get(43) == 3);
                        }
                    }.sleep();
                    script.log("Switched to defence mode.");
                    break;
            }
        }
    }
}