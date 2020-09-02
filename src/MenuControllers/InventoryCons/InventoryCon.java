package MenuControllers.InventoryCons;

import MenuControllers.EditCons.DeckCon;
import MenuControllers.EditCons.EditAmuletsCon;
import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.GeneralClasses.RunAction;
import MenuControllers.MainMenuController;

public class InventoryCon extends MainController {
    public static String myKey = "inventory";

    public InventoryCon(Menus myMenu) {
        super(myMenu);

    }

    /*1. Card Inventory: To view your cards
    2. Item Inventory: To view your items
    3. Amulet Inventory: To view your amulets
    4. Edit Deck: To edit your card deck
    5. Edit Amulets: To equip or remove your amulets
    6. Exit: To exit to previous menu*/

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Card Inventory", "To view your cards", new RunAction() {
            @Override
            public String run(String[] args) {
                goController(CardInventoryCon.myKey, myKey);
                return "";
            }
        }));


        addMenuItem(new MenuItem("Item Inventory", "To view your items", args -> {
            goController(ItemInventoryCon.myKey, myKey);
            return "";
        }));

        addMenuItem(new MenuItem("Amulet Inventory", "To view your amulets", args -> {
            goController(AmuletInventoryCon.myKey, myKey);
            return "";
        }));

        addMenuItem(new MenuItem("Edit Deck", "To edit your card deck", args -> {
            goController(DeckCon.myKey, myKey);
            return "";
        }));

        addMenuItem(new MenuItem("Edit Amulets", "To equip or remove your amulets", args -> {
            goController(EditAmuletsCon.myKey, myKey);
            return "";
        }));

        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "To exit to previous menu", args -> {
            if (getTempMenu().equals("")) {
                goController(MainMenuController.myKey);
            } else {
                goController(getTempMenu());
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
