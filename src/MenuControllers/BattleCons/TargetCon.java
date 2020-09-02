package MenuControllers.BattleCons;

import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.MenuItem;
import MenuControllers.GeneralClasses.Menus;


public class TargetCon extends MainController {
    public static String myKey = "targetCon";

    private int limit;
    private int result;

    private String startingMessage;

    public TargetCon(Menus myMenu) {
        super(myMenu);

    }

    /*
    "Spell Source" has cast a spell:
    [Spell Description]

    1. Target #TargetNum: To cast the spell on the specified target
    2. Exit: To skip spell casting
     */


    @Override
    public void initializeItems() {
        clearMenuItems();

        addMenuItem(new MenuItem("Target #TargetNum", "To cast the spell on the specified target", args -> {
            try {
                int index = Integer.parseInt(args[0]);
                if (index >= 1 && index <= limit) {
                    result = index;
                    return "END TARGET";
                } else {
                    return Menus.RED + "Your Number Is Out Of Range" + Menus.RESET;
                }
            } catch (NumberFormatException e) {
                return Menus.RED + "Cannot Parse Your Number" + Menus.RESET;
            }
        }));


        addMenuItem(new MenuItem("Help", "", args -> help()));

        addMenuItem(new MenuItem("Exit", "To skip spell casting", args -> {
            result = -1;
            return "END TARGET";
        }));

    }

    @Override
    public String help() {
        return getStartingMessage() + "\n\n" + super.help();
    }

    @Override
    public void started() {
        printHelp();
    }


    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getStartingMessage() {
        return startingMessage;
    }

    public void setStartingMessage(String startingMessage) {
        this.startingMessage = startingMessage;
    }


    @Override
    public String getMyKey() {
        return myKey;
    }
}