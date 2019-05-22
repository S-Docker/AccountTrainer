package Utilities;

import Utilities.RandomUtil;
import org.osbot.rs07.api.Trade;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.utility.ConditionalSleep;

public class MuleHandler {
    /* **************************************************
     *                                                  *
     *                      Fields                      *
     *                                                  *
     ****************************************************/
    private static Script script;
    private String mule;
    RandomUtil gRandom = new RandomUtil();

    /* **************************************************
     *                                                  *
     *                  Constructors                    *
     *                                                  *
     ****************************************************/

    /*
     * Set script equal to the script that instantiated this class
     * Set mule equal to the name of mule provided during instantiation
     *
     * @param script - Reference to the script that created an instance
     * @param mule - Reference to the name of mule provided during instantiation
     *
     */
    public MuleHandler(Script script, String mule){
        this.script = script;
        this.mule = mule;
    }

    /* **************************************************
     *                                                  *
     *                  Methods                         *
     *                                                  *
     ****************************************************/
    private boolean MuleFound(){
        boolean found = false;

        for (final Player player : script.getPlayers().getAll()){
            if (player.getName().equals(mule)){
                found = true;
                break;
            }
        }
        return found;
    }

    private boolean isTrading(){
        return script.getTrade().isCurrentlyTrading() || script.getTrade().isFirstInterfaceOpen() || script.getTrade().isSecondInterfaceOpen();
    }

    public boolean InteractWithMule() throws InterruptedException {
        boolean mulingComplete = false;
        if (MuleFound()){
            if(!isTrading()) {
                String mulePlayer = script.getPlayers().closest(mule).getName();

                if (!script.getTrade().isFirstInterfaceOpen()) {
                    script.getPlayers().closest(mule).interact("Trade with");
                    new ConditionalSleep(15000) {
                        @Override
                        public boolean condition() {
                            return script.getTrade().isFirstInterfaceOpen();
                        }
                    }.sleep();
                }
            } else if(script.getTrade().isFirstInterfaceOpen()) {
                if (script.getTrade().didOtherAcceptTrade()) {
                    script.getTrade().acceptTrade();
                }
            } else if (script.getTrade().isSecondInterfaceOpen()) {
                if(script.getTrade().didOtherAcceptTrade()) {
                    script.getTrade().acceptTrade();
                    mulingComplete = true;
                }
            }
                    /**
                    if (type == Enums.AccountType.OBBY_MAUL) {
                        script.log("test");
                        if (!script.getTrade().getOurOffers().contains("Shrimps")) {
                            script.getTrade().offer("Shrimps", Trade.OFFER_1);
                        }
                    } else if (type == Enums.AccountType.F2P_MELEE){
                        if (!script.getTrade().getOurOffers().contains("Bronze arrow")) {
                            script.getTrade().offer("Bronze arrow", 1);
                        }
                    }
                     */
        }
        RandomUtil rand = new RandomUtil();
        script.sleep(rand.gRandomBetween(350, 700));
        return mulingComplete;
    }
}
