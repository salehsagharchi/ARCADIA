package MenuControllers;


import MenuControllers.BattleCons.NextCon;
import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.GeneralClasses.RunAction;
import MenuControllers.InventoryCons.InventoryCon;

public class MainMenuController extends MainController {
    public static String myKey = "main";

    public MainMenuController(Menus myMenu) {
        super(myMenu);

    }

    /* 1.   Enter Shop: To enter shop and buy or sell cards and items
    2. Edit Inventory: To edit your amulet or deck
    3. Next: To go to deck and amulet customization*/

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Enter Shop", "To enter shop and buy or sell cards and items", new RunAction() {
            @Override
            public String run(String[] args) {
                goController(MainShopController.myKey);
                return "";
            }
        }));


        addMenuItem(new MenuItem("Edit Inventory", "To edit your amulet or deck", new RunAction() {
            @Override
            public String run(String[] args) {
                goController(InventoryCon.myKey);
                return "";
            }
        }));

        addMenuItem(new MenuItem("Next", "To go to deck and amulet customization", new RunAction() {
            @Override
            public String run(String[] args) {
                goController(NextCon.myKey);
                return "";
            }
        }));

        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "End The Game", args -> "END GAME"));

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
