package MenuControllers.BattleCons;

import Game.BattleManaging.Battle;
import Game.Enums.BattleState;
import Game.States.StateManager;
import MenuControllers.EditCons.DeckCon;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;


public class NextCon extends DeckCon {
    public static String myKey = "next";

    public NextCon(Menus myMenu) {
        super(myMenu);
        addMenuItem(new MenuItem("Next", "To go to next Battle !", args -> {
            Battle battle = getGameManager().getFirstBattleFor(BattleState.NotStarted);
            // Saving State For Mystic Item
            {
                StateManager.saveState("state.ser", getGameManager().getHumanPlayer().getBag().getDeck(),
                        getGameManager().getHumanPlayer().getInventory(),
                        getGameManager());
            }

            if (battle != null) {
                battle.startBattle(null);
                return "";
            }
            return Menus.CYAN + "You Wined All Battles!" + Menus.RESET;
        }));
    }

    @Override
    public String getMyKey() {
        return myKey;
    }
}
