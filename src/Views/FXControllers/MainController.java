package Views.FXControllers;

import Game.BattleManaging.Battle;
import Game.GameManager;
import Game.PlayerClasses.Player;
import Game.Shops.AmuletShop;
import Game.Shops.CardShop;
import Game.Shops.ItemShop;
import Views.FXControllers.Map.Blocks;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainController {
    private FormMainCon mainFormCon;
    private BattleFormCon battleFormCon;
    private MapFormCon mapFormCon;
    private SettingFormCon settingFormCon;
    private ShopCon shopCon;
    private EditFormCon editFormCon;
    private NetworkCon networkCon;
    private CustomCon customCon;
    private EditSpellsCon editSpellsCon;
    private NewSpellCon newSpellCon;
    private NewGameCon newGameCon;

    private Stage mainFormStage;
    private Stage battleFormStage;
    private Stage mapFormStage;
    private Stage settingFormStage;
    private Stage shopStage;
    private Stage editFormStage;
    private Stage networkStage;
    private Stage customFormStage;
    private Stage editSpellsStage;
    private Stage newSpellStage;
    private Stage newGameStage;

    private GameManager gameManager;

    public MainController(FormMainCon mainFormCon, BattleFormCon battleFormCon, MapFormCon mapFormCon, SettingFormCon settingFormCon, ShopCon shopCon, EditFormCon editFormCon, NetworkCon networkCon
            , Stage mainFormStage, Stage battleFormStage, Stage mapFormStage, Stage settingFormStage, Stage shopStage, Stage editFormStage, Stage networkStage) {
        this.mainFormCon = mainFormCon;
        this.battleFormCon = battleFormCon;
        this.mapFormCon = mapFormCon;
        this.settingFormCon = settingFormCon;
        this.shopCon = shopCon;
        this.editFormCon = editFormCon;
        this.networkCon = networkCon;

        this.mainFormStage = mainFormStage;
        this.battleFormStage = battleFormStage;
        this.mapFormStage = mapFormStage;
        this.settingFormStage = settingFormStage;
        this.shopStage = shopStage;
        this.editFormStage = editFormStage;
        this.networkStage = networkStage;

        mainFormCon.setMainController(this);
        battleFormCon.setMainController(this);
        mapFormCon.setMainController(this);
        settingFormCon.setMainController(this);
        shopCon.setMainController(this);
        editFormCon.setMainController(this);
        networkCon.setMainController(this);
    }

    public void initCustomGameForms(CustomCon customCon, EditSpellsCon editSpellsCon, NewSpellCon newSpellCon, NewGameCon newGameCon
            , Stage customFormStage, Stage editSpellsStage, Stage newSpellStage, Stage newGameStage) {
        this.customCon = customCon;
        this.editSpellsCon = editSpellsCon;
        this.newSpellCon = newSpellCon;
        this.newGameCon = newGameCon;

        this.customFormStage = customFormStage;
        this.editSpellsStage = editSpellsStage;
        this.newSpellStage = newSpellStage;
        this.newGameStage = newGameStage;

        customCon.setMainController(this);
        editSpellsCon.setMainController(this);
        newSpellCon.setMainController(this);
        newGameCon.setMainController(this);
    }

    public void singlePlayer() {
        startMap();
    }

    public void startMain() {
        showStage(mainFormStage);
        mainFormCon.start();
    }

    public void startMap() {
        showStage(mapFormStage);
        mapFormCon.start();
    }

    public void startBattle(Blocks battleBlock) {
        showStage(battleFormStage);
        battleFormCon.startWithBattle(battleBlock);
        battleFormStage.setOnCloseRequest(event -> battleFormCon.onClose());
    }

    public void startNetworkBattle(Battle battle) {
        showStage(battleFormStage);
        battleFormCon.startWithBattleForSocket(battle);
        battleFormStage.setOnCloseRequest(event -> battleFormCon.onClose());
    }

    public void startSetting() {
        showStage(settingFormStage);
        settingFormCon.start();
    }

    public void startShop() {
        startShop(null, null, null, null);
    }

    public void startShop(Player targetPlayer, CardShop cardShop, ItemShop itemShop, AmuletShop amuletShop) {
        showStage(shopStage);
        if (targetPlayer == null) {
            shopCon.start();
        } else {
            shopCon.start(targetPlayer, cardShop, itemShop, amuletShop);
        }
        shopStage.setOnCloseRequest(event -> shopCon.onClose());
    }

    public void startEditForm() {
        showStage(editFormStage);
        editFormCon.start();
        editFormStage.setOnCloseRequest(event -> editFormCon.onClose());
    }


    public void startNetworkForm() {
        showStage(networkStage);
        networkCon.start();
        networkStage.setOnCloseRequest(event -> networkCon.onClose());
    }


    public void startCustomForm() {
        showStage(customFormStage);
        customCon.start();
    }

    public void startEditSpells() {
        showStage(editSpellsStage);
        editSpellsCon.start();
    }

    public void startNewSpell() {
        showStage(newSpellStage);
        newSpellCon.start();
    }

    public void startNewGame() {
        showStage(newGameStage);
        newGameCon.start();
    }

    private void showStage(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.show();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public FormMainCon getMainFormCon() {
        return mainFormCon;
    }

    public BattleFormCon getBattleFormCon() {
        return battleFormCon;
    }

    public MapFormCon getMapFormCon() {
        return mapFormCon;
    }

    public SettingFormCon getSettingFormCon() {
        return settingFormCon;
    }

    public ShopCon getShopCon() {
        return shopCon;
    }

    public EditFormCon getEditFormCon() {
        return editFormCon;
    }

    public NetworkCon getNetworkCon() {
        return networkCon;
    }

    public Stage getMainFormStage() {
        return mainFormStage;
    }

    public Stage getBattleFormStage() {
        return battleFormStage;
    }

    public Stage getMapFormStage() {
        return mapFormStage;
    }

    public Stage getSettingFormStage() {
        return settingFormStage;
    }

    public Stage getShopStage() {
        return shopStage;
    }

    public Stage getEditFormStage() {
        return editFormStage;
    }

    public Stage getNetworkStage() {
        return networkStage;
    }

    public CustomCon getCustomCon() {
        return customCon;
    }

    public Stage getCustomFormStage() {
        return customFormStage;
    }

    public EditSpellsCon getEditSpellsCon() {
        return editSpellsCon;
    }

    public NewSpellCon getNewSpellCon() {
        return newSpellCon;
    }

    public Stage getEditSpellsStage() {
        return editSpellsStage;
    }

    public Stage getNewSpellStage() {
        return newSpellStage;
    }

    public NewGameCon getNewGameCon() {
        return newGameCon;
    }

    public Stage getNewGameStage() {
        return newGameStage;
    }

    public GameManager getGameManager() {
        return gameManager;
    }


    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
        mainFormCon.setGameManager(gameManager);
        battleFormCon.setGameManager(gameManager);
        mapFormCon.setGameManager(gameManager);
        settingFormCon.setGameManager(gameManager);
        shopCon.setGameManager(gameManager);
        editFormCon.setGameManager(gameManager);
        networkCon.setGameManager(gameManager);
        customCon.setGameManager(gameManager);
        editSpellsCon.setGameManager(gameManager);
        newSpellCon.setGameManager(gameManager);
        newGameCon.setGameManager(gameManager);
    }
}
