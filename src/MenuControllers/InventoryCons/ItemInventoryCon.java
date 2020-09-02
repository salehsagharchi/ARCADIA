package MenuControllers.InventoryCons;

import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;

public class ItemInventoryCon extends MainController {
    public static String myKey = "itemInventory";

    public ItemInventoryCon(Menus myMenu) {
        super(myMenu);

    }

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Info \"Item Name\"", "To view the full information of the item", args -> {
            try {
                return getGameManager().getItemShop().findItem(args[0]).toString();
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

    private String itemShopString() {
        String res = "";
        res += "\tItem Inventory :";
        res += "\n" + getGameManager().getHumanPlayer().getInventory().showListOfItems() + "\n";
        return res;
    }

    @Override
    public void started() {
        Menus.print(itemShopString());
        printHelp();
    }

    @Override
    public String getMyKey() {
        return myKey;
    }
}
