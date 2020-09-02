package MenuControllers.InventoryCons;

import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;

public class CardInventoryCon extends MainController {
    public static String myKey = "cardInventory";

    public CardInventoryCon(Menus myMenu) {
        super(myMenu);

    }

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Info \"Card Name\"", "To get more information about a card", args -> {
            try {
                return getGameManager().getCardShop().findCard(args[0]).toString();
            } catch (Exception e) {
                return "ERROR : Name !";
            }
        }));

        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "To exit to previous menu", args -> {
            if (getTempMenu().equals("")) {
                goController(InventoryCon.myKey);
            } else {
                goController(getTempMenu());
            }
            return "";
        }));


    }

    private String cardShopString() {
        String res = "";
        res += "\tCard Inventory :";
        res += "\n" + getGameManager().getHumanPlayer().getInventory().showListOfCards() + "\n";
        return res;
    }

    @Override
    public void started() {
        Menus.print(cardShopString());
        printHelp();
    }

    @Override
    public String getMyKey() {
        return myKey;
    }
}
