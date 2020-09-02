package MenuControllers.ShopCons;

import MenuControllers.EditCons.DeckCon;
import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.MainShopController;

public class CardShopCon extends MainController {
    public static String myKey = "cardShop";

    public CardShopCon(Menus myMenu) {
        super(myMenu);

    }

    /*#GilDetails
    1. Buy "Card Name" - #NumberToBuy: To buy a certain number of a card from shop
    2. Sell "Card Name" - #NumberToSell: To sell a certain number of a card from
                inventory
    3. Info "Card Name": To get more information about a card
    4. Edit Deck: To edit Deck and remove and add cards to it
    5. Exit: To return to shop menu*/

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Buy \"Card Name\" - #NumberToBuy", "To buy a certain number of a card from shop", args -> {
            String res = getGameManager().getCardShop().buyCard(args[0], args[1], getGameManager(), getGameManager().getHumanPlayer()).trim();
            if (res.startsWith("Successfully")) {
                Menus.print("\n" + res + "\n");
                try {
                    Thread.yield();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                return cardShopString();
            }
            return res;
        }));


        addMenuItem(new MenuItem("Sell \"Card Name\" - #NumberToSell", "To sell a certain number of a card from", args -> {

            String res = getGameManager().getCardShop().sellCard(args[0], args[1], getGameManager(), getGameManager().getHumanPlayer()).trim();
            //System.out.println("res : " + res);
            if (res.startsWith("Successfully")) {
                Menus.print("\n" + res + "\n");
                try {
                    Thread.yield();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                return cardShopString();
            }
            return res;
        }));

        addMenuItem(new MenuItem("Info \"Card Name\"", "To get more information about a card", args -> {
            try {
                return getGameManager().getCardShop().findCard(args[0]).toString();
            } catch (Exception e) {
                return "ERROR : Name !";
            }
        }));

        addMenuItem(new MenuItem("Edit Deck", "To edit Deck and remove and add cards to it", args -> {
            goController(DeckCon.myKey, myKey);
            return "";
        }));

        addMenuItem(new MenuItem("Again", "Print Cards Lists", args -> cardShopString()));

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


    private String cardShopString() {
        String res = "";
        res += "\nRemaining Gil: " + getGameManager().getHumanPlayer().getGil();
        res += "\n\n\tShop List :";
        res += "\n" + getGameManager().getCardShop().showList(false) + "\n";
        res += "\n\tCard Inventory :";
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
