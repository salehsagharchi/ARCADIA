package Game;

import Game.BattleManaging.*;
import Game.Constants.Initializer_Statics;
import Game.Custom.CustomGamesManager;
import Game.Custom.GameClass;
import Game.Enums.BattleState;
import Game.Enums.PlayerType;
import Game.Network.NetworkManager;
import Game.PlayerClasses.Player;
import Game.Shops.AmuletShop;
import Game.Shops.CardShop;
import Game.Shops.ItemShop;
import Views.FXControllers.MainController;


public class GameManager {
    private AmuletShop amuletShop;
    private CardShop cardShop;
    private ItemShop itemShop;
    private Player humanPlayer;
    private NetworkManager networkManager;
    private CustomGamesManager customGamesManager;
    private Battle[] battles;
    private Views.FXControllers.MainController mainController;

    public GameManager(Views.FXControllers.MainController mainController) {
        this.mainController = mainController;

        // Shop
        amuletShop = new AmuletShop();
        cardShop = new CardShop();
        itemShop = new ItemShop();
        Initializer_Statics.initializeAllShopElements(cardShop, itemShop, amuletShop);

        // Players
        humanPlayer = new Player(PlayerType.Human, "SALEH", 20000);

        battles = new Battle[4];
        battles[0] = new Battle1(humanPlayer);
        battles[1] = new Battle2(humanPlayer);
        battles[2] = new Battle3(humanPlayer);
        battles[3] = new Battle4(humanPlayer);
        battles[0].setState(BattleState.NotStarted);
        battles[1].setState(BattleState.NotStarted);
        battles[2].setState(BattleState.NotStarted);
        battles[3].setState(BattleState.NotStarted);


        // Network
        networkManager = new NetworkManager(this);

        // Custom
        customGamesManager = new CustomGamesManager(this);
    }

    public void initializeCustomGame(GameClass gameClass) {
        cardShop = gameClass.getCardShop();
        itemShop = gameClass.getItemShop();
        amuletShop = gameClass.getAmuletShop();
        humanPlayer = gameClass.getPlayer();
        humanPlayer.setPlayingGameClass(gameClass);
        for (Battle battle : battles) {
            battle.setHumanPlayer(humanPlayer);
        }
        Initializer_Statics.setCardList(gameClass.getCardMap());
        Initializer_Statics.setAmuletList(gameClass.getAmuletMap());
        Initializer_Statics.setItemList(gameClass.getItemMap());
    }

    public Battle getFirstBattleFor(BattleState state) {

        if (battles[0].getState() == state) {
            return battles[0];
        } else if (battles[1].getState() == state) {
            return battles[1];
        } else if (battles[2].getState() == state) {
            return battles[2];
        } else if (battles[3].getState() == state) {
            return battles[3];
        }
        return null;
    }

    public int getStartingBattle() {
        if (battles[0].getState() == BattleState.NotStarted) {
            return 0;
        } else if (battles[1].getState() == BattleState.NotStarted) {
            return 1;
        } else if (battles[2].getState() == BattleState.NotStarted) {
            return 2;
        } else if (battles[3].getState() == BattleState.NotStarted) {
            return 3;
        }
        return -1;
    }


    public Battle getBattle(int index) {
        if (index >= 0 && index <= 3) {
            return battles[index];
        }
        return null;
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public AmuletShop getAmuletShop() {
        return amuletShop;
    }

    public CardShop getCardShop() {
        return cardShop;
    }

    public ItemShop getItemShop() {
        return itemShop;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setHumanPlayer(Player humanPlayer) {
        this.humanPlayer = humanPlayer;
    }

    public CustomGamesManager getCustomGamesManager() {
        return customGamesManager;
    }

    public void setCustomGamesManager(CustomGamesManager customGamesManager) {
        this.customGamesManager = customGamesManager;
    }
}
