package MenuControllers.EditCons;


import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.MainMenuController;

public class EditAmuletsCon extends MainController {
    public static String myKey = "editAmulets";

    public EditAmuletsCon(Menus myMenu) {
        super(myMenu);

    }

    /*1. Equip "Amulet Name": To equip the player with an amulet
    2. Remove Amulet: To remove the amulet equipped with the player (if the player is equipped with one)
    3. Info "Amulet Name": To get more information about a specific amulet
    4. Exit: To return to the previous section*/

    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Equip \"Amulet Name\"", "To equip the player with an amulet", args -> getGameManager().getHumanPlayer().getInventory().equipAmulet(args[0])));


        addMenuItem(new MenuItem("Remove Amulet", "To remove the amulet equipped with the player (if the player is equipped with one)", args -> getGameManager().getHumanPlayer().getInventory().removeEquippedAmulet()));

        addMenuItem(new MenuItem("Info \"Amulet Name\"", "To get more information about a specific amulet", args -> {
            try {
                return getGameManager().getAmuletShop().findAmulet(args[0]).toString();
            } catch (Exception e) {
                return "ERROR : Name !";
            }
        }));

        addMenuItem(new MenuItem("Again", "Print Amulets Lists", args -> editAmuletString()));

        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "To return to the previous section", args -> {
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
        return super.help();
    }


    private String editAmuletString() {
        String res = "";
        res += "\nAmulets :";
        res += "\n" + getGameManager().getHumanPlayer().getInventory().showListOfAmulets() + "\n";
        String equipped = getGameManager().getHumanPlayer().getBag().equippedAmulet();
        if (!equipped.startsWith("NULL")) {
            res += "\nPlayer is equipped with \"" + equipped + "\"";
        }
        return res;
    }

    @Override
    public void started() {
        Menus.print(editAmuletString());
        printHelp();
    }

    @Override
    public String getMyKey() {
        return myKey;
    }
}
