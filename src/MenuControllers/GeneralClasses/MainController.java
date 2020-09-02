package MenuControllers.GeneralClasses;

import Game.GameManager;

import java.util.ArrayList;
import java.util.List;


public abstract class MainController {
    protected Menus myMenu;
    private GameManager gameManager;

    private String tempMenu;

    private ArrayList<MenuItem> menuItems;

    protected abstract void initializeItems();

    public abstract void started();

    protected MainController(Menus myMenu) {
        this.myMenu = myMenu;
        menuItems = new ArrayList<>();
        initializeItems();
    }


    public ArrayList<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void addMenuItem(MenuItem menuItem) {
        this.menuItems.add(menuItem);
    }

    public void clearMenuItems() {
        this.menuItems.clear();
    }

    public void goController(String key) {
        myMenu.setNowController(myMenu.getControllers().get(key));
        myMenu.getNowController().started();
        myMenu.getNowController().setTempMenu("");
    }

    public void goController(String key, String sender) {
        myMenu.setNowController(myMenu.getControllers().get(key));
        myMenu.getNowController().started();
        myMenu.getNowController().setTempMenu(sender);
    }


    public String help() {
        StringBuilder res = new StringBuilder();
        int index = 1;
        for (MenuItem item :
                getMenuItems()) {
            res.append(Menus.BLUE).append(index).append(". ").append(Menus.RESET).append(item.toString()).append("\n");
            index++;
        }
        return res.toString();
    }


    public abstract String getMyKey();

    public void printHelp() {
        Menus.print(help());
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public String getTempMenu() {
        return tempMenu;
    }

    public void setTempMenu(String tempMenu) {
        this.tempMenu = tempMenu;
    }
}
