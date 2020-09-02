package MenuControllers.BattleCons;

import Game.Enums.BattleState;
import Game.Exceptions.DeadPlayerException;
import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;

import java.util.Scanner;

public class BattleCon extends MainController {
    public static String myKey = "battleCon";

    public BattleCon(Menus myMenu) {
        super(myMenu);

    }


    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Use #SlotNum", "To use a specific card which is on the Monster Field", args -> {
            try {
                getGameManager().getHumanPlayer().getBag().getMonsterField().useCard(args[0]);
            } catch (DeadPlayerException e) {

            }
            return "";
        }));


        addMenuItem(new MenuItem("Set #HandIndex to #SlotNum", "To set a card which is on the hand , in the field", args -> {
            String res = getGameManager().getHumanPlayer().getBag().getHand().drawCardTo(getGameManager().getHumanPlayer(), args[0], args[1]);
            res += "\n";
            if (res.startsWith("ERROR")) {
                return Menus.RED + res + Menus.RESET;
            } else {
                return Menus.CYAN + res + Menus.RESET;
            }
        }));

        addMenuItem(new MenuItem("Use Items", "To use a specific item which is on the Inventory", args -> {
            goController(ItemUseCon.myKey, myKey);
            return "";
        }));

        addMenuItem(new MenuItem("Show Players", "To view the Players Information", args -> {
            return getGameManager().getFirstBattleFor(BattleState.Playing).showPlayers();
        }));

        addMenuItem(new MenuItem("View Hand", "To view the cards in your hand", args -> {
            String res = Menus.CYAN + "Your Hand :\n" + Menus.RESET;
            res += getGameManager().getHumanPlayer().getBag().getHand().showList();
            return res;
        }));

        addMenuItem(new MenuItem("View Graveyard", "To view the cards in your graveyard", args -> {
            String res = Menus.CYAN + "Your Graveyard :\n" + Menus.RESET;
            res += getGameManager().getHumanPlayer().getBag().getGraveyard().showList();
            res += Menus.CYAN + "\nEnemy Graveyard :\n" + Menus.RESET;
            res += getGameManager().getHumanPlayer().getOpposite().getBag().getGraveyard().showList();
            return res;
        }));

        addMenuItem(new MenuItem("View SpellField", "To view the cards in both player's spell fields", args -> {
            String res = Menus.CYAN + "Your SpellField :\n" + Menus.RESET;
            res += getGameManager().getHumanPlayer().getBag().getSpellField().showList();
            res += Menus.CYAN + "\nEnemy SpellField :\n" + Menus.RESET;
            res += getGameManager().getHumanPlayer().getOpposite().getBag().getSpellField().showList();
            return res;
        }));

        addMenuItem(new MenuItem("View MonsterField", "To view the cards in both player's monster fields", args -> {
            String res = Menus.CYAN + "Your MonsterField :\n" + Menus.RESET;
            res += getGameManager().getHumanPlayer().getBag().getMonsterField().showList();
            res += Menus.CYAN + "\nEnemy MonsterField :\n" + Menus.RESET;
            res += getGameManager().getHumanPlayer().getOpposite().getBag().getMonsterField().showList();
            return res;
        }));


        addMenuItem(new MenuItem("Info \"Card Name\"", "To get more information about a specific card", args -> {
            try {
                return getGameManager().getCardShop().findCard(args[0]).toString();
            } catch (Exception e) {
                return "ERROR : Name !";
            }
        }));

        addMenuItem(new MenuItem("Done", "To end your turn", args -> {
            getGameManager().getFirstBattleFor(BattleState.Playing).nextPlayer();
            return "";
        }));

        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "To EXIT From Battle", args -> {
            System.out.println("Do You Want To Exit Game Without Saving ? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            String res = scanner.nextLine();
            if (res.equalsIgnoreCase("y") || res.equalsIgnoreCase("yes")) {
                getGameManager().getFirstBattleFor(BattleState.Playing).endBattleWithoutSaving();
            } else {
                return help();
            }
            return "";
        }));


    }


    @Override
    public void started() {
        printHelp();
    }

    @Override
    public String getMyKey() {
        return myKey;
    }
}