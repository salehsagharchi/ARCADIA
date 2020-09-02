package MenuControllers.BattleCons;

import Game.Assets.Item;
import Game.Elements.BagElement;
import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.MainMenuController;

import java.util.Map;

public class ItemUseCon extends MainController {
    public static String myKey = "itemUseCon";

    public ItemUseCon(Menus myMenu) {
        super(myMenu);

    }

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Use #ItemNumber", "To run magic command of item", args -> {
            try {
                int index = Integer.parseInt(args[0]);
                Map<String, BagElement<Item>> items = getGameManager().getHumanPlayer().getInventory().getItems();
                if (index >= 1 && index <= items.size()) {
                    int i = 1;
                    for (Map.Entry<String, BagElement<Item>> entry : items.entrySet()) {
                        Item myEntry = entry.getValue().getObject();
                        if (i == index) {
                            if (entry.getValue().getNumber() > 0) {
                                System.out.println(Menus.CYAN + myEntry.toString() + "\n" + Menus.RESET);
                                String res = myEntry.runMagicCommand(getGameManager().getHumanPlayer());
                                if (res.equals("ok")) {
                                    getGameManager().getHumanPlayer().getInventory().removeItem(entry.getKey(), 1);
                                }
                            } else {
                                return "ERROR";
                            }
                        }
                        i += 1;
                    }

                } else {
                    return Menus.RED + "Your Number Is Out Of Range" + Menus.RESET;
                }
            } catch (NumberFormatException e) {
                return Menus.RED + "Cannot Parse Your Number" + Menus.RESET;
            }
            return "";
        }));

        addMenuItem(new MenuItem("Info \"Item Name\"", "To view the full information of the item", args -> {
            try {
                return getGameManager().getItemShop().findItem(args[0]).toString();
            } catch (Exception e) {
                return "ERROR : Name !";
            }
        }));


        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "To go back to Play Menu", args -> {
            if (getTempMenu().equals("")) {
                goController(MainMenuController.myKey);
            } else {
                goController(getTempMenu());
            }
            return "";
        }));
    }

    @Override
    public String help() {
        return getStartText() + "\n\n" + super.help();
    }

    @Override
    public void started() {
        printHelp();
    }

    @Override
    public String getMyKey() {
        return myKey;
    }

    public String getStartText() {
        String res = "";
        res += "\n\tItem Inventory :";
        res += "\n" + getGameManager().getHumanPlayer().getInventory().showListOfItems() + "\n";
        return res;
    }


}