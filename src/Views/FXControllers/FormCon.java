package Views.FXControllers;

import Game.GameManager;

public abstract class FormCon {
    private GameManager gameManager;
    private MainController mainController;


    public abstract void start();

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
