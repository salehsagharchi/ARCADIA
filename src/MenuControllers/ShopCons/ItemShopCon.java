package MenuControllers.ShopCons;

import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.MainShopController;

public class ItemShopCon extends MainController {
    public static String myKey = "itemShop";

    public ItemShopCon(Menus myMenu) {
        super(myMenu);

    }

    /*#GilDetails
    1. Buy "Item Name" - #NumberToBuy: To buy an item from the shop
    2. Sell "Item Name" - #NumberToSell: To sell an item from your item inventory
    3. Info "Item Name": To view the full information of the item
    4. Exit: To exit back to the shop menu*/

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Buy \"Item Name\" - #NumberToBuy", "To buy an item from the shop", args -> {
            String res = getGameManager().getItemShop().buyItem(args[0], args[1], getGameManager(), getGameManager().getHumanPlayer()).trim();
            if (res.startsWith("Successfully")) {
                Menus.print("\n" + res + "\n");
                try {
                    Thread.yield();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                return itemShopString();
            }
            return res;
        }));


        addMenuItem(new MenuItem("Sell \"Item Name\" - #NumberToSell", "To sell an item from your item inventory", args -> {
            String res = getGameManager().getItemShop().sellItem(args[0], args[1], getGameManager(), getGameManager().getHumanPlayer()).trim();
            //System.out.println("res : " + res);
            if (res.startsWith("Successfully")) {
                Menus.print("\n" + res + "\n");
                try {
                    Thread.yield();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                return itemShopString();
            }
            return res;
        }));

        addMenuItem(new MenuItem("Info \"Item Name\"", "To view the full information of the item", args -> {
            try {
                return getGameManager().getItemShop().findItem(args[0]).toString();
            } catch (Exception e) {
                return "ERROR : Name !";
            }
        }));

        addMenuItem(new MenuItem("Again", "Print Items Lists", args -> itemShopString()));

        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "To return to shop menu", args -> {
            goController(MainShopController.myKey);
            return "";
        }));


    }


    @Override
    public String help() {
        String remain = "Remaining Gil: " + getGameManager().getHumanPlayer().getGil();
        return (remain + "\n" + super.help());
    }


    private String itemShopString() {
        String res = "";
        res += "\nRemaining Gil: " + getGameManager().getHumanPlayer().getGil();
        res += "\n\n\tShop List :";
        res += "\n" + getGameManager().getItemShop().showList(false) + "\n";
        res += "\n\tItem Inventory :";
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
