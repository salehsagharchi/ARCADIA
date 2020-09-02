package MenuControllers.EditCons;

import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.MainMenuController;

public class DeckCon extends MainController {
    public static String myKey = "deck";

    public DeckCon(Menus myMenu) {
        super(myMenu);

    }

        /*1. Add "Card Name" #CardSlotNum: To add cards to your deck
    2. Remove "Card Name" #CardSlotNum: To remove cards from your deck
    3. Info "Card Name": To get more information about a specific card
    4. Exit: To return to the previous section*/

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Add \"Card Name\" #CardSlotNum", "To add cards to your deck",
                args -> getGameManager().getHumanPlayer().getInventory().addCardToDeck(args[0], args[1])));


        addMenuItem(new MenuItem("Remove #CardSlotNum", "To remove cards from your deck",
                args -> getGameManager().getHumanPlayer().getInventory().removeCardFromDeck(args[0])));

        addMenuItem(new MenuItem("Info \"Card Name\"", "To get more information about a specific card", args -> {
            try {
                return getGameManager().getCardShop().findCard(args[0]).toString();
            } catch (Exception e) {
                return "ERROR : Name !";
            }
        }));

        addMenuItem(new MenuItem("Again", "Print Cards Lists", args -> editDeckString()));

        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "To return to the previous section", args -> {
            if (getTempMenu().equals("")) {
                goController(MainMenuController.myKey);
            } else {
                goController(getTempMenu());
            }
            return "";
        }));


    }


    private String editDeckString() {
        String res = "";

        res += "\nDeck :";
        res += "\n" + getGameManager().getHumanPlayer().getBag().getDeck().showList() + "\n";
        res += "\nOther Cards :";
        res += "\n" + getGameManager().getHumanPlayer().getInventory().showListOfCards() + "\n";
        return res;
    }

    @Override
    public void started() {
        Menus.print(editDeckString());
        printHelp();
    }

    @Override
    public String getMyKey() {
        return myKey;
    }
}
