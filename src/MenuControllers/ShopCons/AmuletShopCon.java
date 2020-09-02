package MenuControllers.ShopCons;

import MenuControllers.EditCons.EditAmuletsCon;
import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.MainShopController;

public class AmuletShopCon extends MainController {
    public static String myKey = "amuletShop";

    public AmuletShopCon(Menus myMenu) {
        super(myMenu);

    }

    /*#GilDetails
    1. Buy "Amulet Name" - #NumberToBuy: To buy a number of an amulet from the shop
    2. Sell "Amulet Name" - #NumberToSell: To sell a number of an amulet from amulet
    inventory
    3. Info "Amulet Name": To get full info on an amulet
    4. Edit Amulets: To equip or remove your ’heros amulet
    5. Exit: To exit to the shop menu*/

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Buy \"Amulet Name\" - #NumberToBuy", "To buy a number of an amulet from the shop", args -> {
            String res = getGameManager().getAmuletShop().buyAmulet(args[0], args[1], getGameManager(), getGameManager().getHumanPlayer()).trim();
            if (res.startsWith("Successfully")) {
                Menus.print("\n" + res + "\n");
                try {
                    Thread.yield();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                return amuletShopString();
            }
            return res;
        }));


        addMenuItem(new MenuItem("Sell \"Amulet Name\" - #NumberToSell", "To get full info on an amulet inventory", args -> {

            String res = getGameManager().getAmuletShop().sellAmulet(args[0], args[1], getGameManager(), getGameManager().getHumanPlayer()).trim();
            //System.out.println("res : " + res);
            if (res.startsWith("Successfully")) {
                Menus.print("\n" + res + "\n");
                try {
                    Thread.yield();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                return amuletShopString();
            }
            return res;

        }));

        addMenuItem(new MenuItem("Info \"Amulet Name\"", "To get more information about a amulet", args -> {
            try {
                return getGameManager().getAmuletShop().findAmulet(args[0]).toString();
            } catch (Exception e) {
                return "ERROR : Name !";
            }
        }));

        addMenuItem(new MenuItem("Edit Amulets", "To equip or remove your hero's amulet", args -> {
            goController(EditAmuletsCon.myKey, myKey);

            return "";
        }));

        addMenuItem(new MenuItem("Again", "Print Amulets Lists", args -> amuletShopString()));

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


    private String amuletShopString() {
        String res = "";
        res += "\nRemaining Gil: " + getGameManager().getHumanPlayer().getGil();
        res += "\n\n\tShop List :";
        res += "\n" + getGameManager().getAmuletShop().showList(false) + "\n";
        res += "\n" + "Equipped Amulet: " + getGameManager().getHumanPlayer().getBag().equippedAmulet() + "\n";
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
