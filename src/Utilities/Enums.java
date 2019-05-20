package Utilities;

public class Enums {
    public enum Styles {
        ATTACK("Attack"),
        STRENGTH("Strength"),
        DEFENCE("Defence");

        public final String label;

        private Styles(String label){
            this.label = label;
        }

        @Override
        public String toString(){
            return label;
        }
    }

    // Account type handling variables
    public enum AccountType {
        OBBY_MAUL("Obby Maul"),
        F2P_MELEE("F2P Melee");

        public final String label;

        private AccountType(String label){
            this.label = label;
        }

        @Override
        public String toString(){
            return label;
        }
    }

    public enum AccountStatus {
        FRESH_ACCOUNT("Fresh Account"),
        EXISTING_ACCOUNT("Existing Account");

        public final String label;

        private AccountStatus(String label){
            this.label = label;
        }

        @Override
        public String toString(){
            return label;
        }
    }
}
