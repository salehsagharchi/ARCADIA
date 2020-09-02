package MenuControllers;


import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.ShopCons.AmuletShopCon;
import MenuControllers.ShopCons.CardShopCon;
import MenuControllers.ShopCons.ItemShopCon;

public class MainShopController extends MainController {
    public static String myKey = "shop";

    public MainShopController(Menus myMenu) {
        super(myMenu);

    }

//    myMenu.setNowController(myMenu.getControllers().get("game"));
//                myMenu.getNowController().started();

    /*    Remaining Gil: #GilAmount Gil
    1. Card Shop
    2. Item Shop
    3. Amulet Shop
    4. Exit*/

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Card Shop", "", args -> {
            goController(CardShopCon.myKey);
            return "";
        }));


        addMenuItem(new MenuItem("Item Shop", "", args -> {
            goController(ItemShopCon.myKey);
            return "";
        }));

        addMenuItem(new MenuItem("Amulet Shop", "", args -> {
            goController(AmuletShopCon.myKey);
            return "";
        }));

        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "Back to Main Menu", args -> {
            goController("main");
            return "";
        }));


    }


    @Override
    public String help() {
        String remain = "Remaining Gil: " + getGameManager().getHumanPlayer().getGil();
        return (remain + "\n" + super.help());
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


