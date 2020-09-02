package MainPackage;

import Game.GameManager;
import MenuControllers.BattleCons.*;
import MenuControllers.EditCons.DeckCon;
import MenuControllers.EditCons.EditAmuletsCon;
import MenuControllers.GeneralClasses.MainController;
import MenuControllers.GeneralClasses.Menus;
import MenuControllers.InventoryCons.AmuletInventoryCon;
import MenuControllers.InventoryCons.CardInventoryCon;
import MenuControllers.InventoryCons.InventoryCon;
import MenuControllers.InventoryCons.ItemInventoryCon;
import MenuControllers.MainMenuController;
import MenuControllers.MainShopController;
import MenuControllers.ShopCons.AmuletShopCon;
import MenuControllers.ShopCons.CardShopCon;
import MenuControllers.ShopCons.ItemShopCon;

import java.util.Map;
import java.util.Scanner;

public class Main implements Runnable {

    Menus myMenus;
    String endStr;

    public Main(Menus menus, String endStr) {
        this.myMenus = menus;
        this.endStr = endStr;
    }

    public static void main(String[] args) {

        Menus menu = new Menus();

        GameManager gameManager = new GameManager(null);


        menu.addController(MainMenuController.myKey, new MainMenuController(menu), true);
        menu.addController(MainShopController.myKey, new MainShopController(menu), false);

        menu.addController(CardShopCon.myKey, new CardShopCon(menu), false);
        menu.addController(ItemShopCon.myKey, new ItemShopCon(menu), false);
        menu.addController(AmuletShopCon.myKey, new AmuletShopCon(menu), false);

        menu.addController(InventoryCon.myKey, new InventoryCon(menu), false);
        menu.addController(CardInventoryCon.myKey, new CardInventoryCon(menu), false);
        menu.addController(ItemInventoryCon.myKey, new ItemInventoryCon(menu), false);
        menu.addController(AmuletInventoryCon.myKey, new AmuletInventoryCon(menu), false);

        menu.addController(DeckCon.myKey, new DeckCon(menu), false);

        menu.addController(EditAmuletsCon.myKey, new EditAmuletsCon(menu), false);


        menu.addController(NextCon.myKey, new NextCon(menu), false);
        menu.addController(BattleCon.myKey, new BattleCon(menu), false);
        menu.addController(CardUseCon.myKey, new CardUseCon(menu), false);
        menu.addController(TargetCon.myKey, new TargetCon(menu), false);
        menu.addController(ItemUseCon.myKey, new ItemUseCon(menu), false);


        for (Map.Entry<String, MainController> entry :
                menu.getControllers().entrySet()) {
            entry.getValue().setGameManager(gameManager);
        }


        menu.setNowController(menu.getFirstController());
        menu.getNowController().started();


        Main main = new Main(menu, "END GAME");
        new Thread(main).start();
    }


    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);

        String nowCMD = sc.nextLine();
        String result;
        while (!(result = myMenus.execute(nowCMD)).equals(endStr)) {
            if (!result.equals("")) {
                System.out.println(result);
            }
            nowCMD = sc.nextLine();
        }
    }
}

