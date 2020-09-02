package MenuControllers.InventoryCons;

import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;


public class AmuletInventoryCon extends MainController {
    public static String myKey = "amuletInventory";

    public AmuletInventoryCon(Menus myMenu) {
        super(myMenu);

    }

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Info \"Amulet Name\"", "To get more information about a amulet", args -> {
            try {
                return getGameManager().getAmuletShop().findAmulet(args[0]).toString();
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

    private String amuletShopString() {
        String res = "";
        res += "Equipped Amulet: " + getGameManager().getHumanPlayer().getBag().equippedAmulet() + "\n";
        res += "\n\tAmulet Inventory :";
        res += "\n" + getGameManager().getHumanPlayer().getInventory().showListOfAmulets() + "\n";
        return res;
    }

    @Override
    public void started() {
        Menus.print(amuletShopString());
        printHelp();
    }

    @Override
    public String getMyKey() {
        return myKey;
    }
}
