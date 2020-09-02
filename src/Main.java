import Game.GameManager;
import Views.FXControllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    MainController mainController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            initializeFormsAndStart(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }

        initializeModels();

    }

    private void initializeModels() {
        /*Setting setting = new Setting();
        setting.setVolume(true);
        mainController.setSetting(setting);

        GameScene gameScene = new GameScene();
        mainController.setGameScene(gameScene);

        ScoreManager scoreManager = new ScoreManager();
        scoreManager.loadScores();
        mainController.setScoreManager(scoreManager);
*/
    }

    private void initializeFormsAndStart(Stage primaryStage) throws Exception {
        FXMLLoader mainFormLoader = new FXMLLoader(getClass().getResource("Views/MainForm.fxml"));
        FXMLLoader battleLoader = new FXMLLoader(getClass().getResource("Views/BattleForm.fxml"));
        FXMLLoader mapLoader = new FXMLLoader(getClass().getResource("Views/MapForm.fxml"));
        FXMLLoader settingLoader = new FXMLLoader(getClass().getResource("Views/SettingForm.fxml"));
        FXMLLoader shopLoader = new FXMLLoader(getClass().getResource("Views/ShopForm.fxml"));
        FXMLLoader editFormLoader = new FXMLLoader(getClass().getResource("Views/EditForm.fxml"));
        FXMLLoader networkLoader = new FXMLLoader(getClass().getResource("Views/NetworkForm.fxml"));
        FXMLLoader customFormLoader = new FXMLLoader(getClass().getResource("Views/CustomForm.fxml"));
        FXMLLoader editSpellsLoader = new FXMLLoader(getClass().getResource("Views/EditSpellsForm.fxml"));
        FXMLLoader newSpellLoader = new FXMLLoader(getClass().getResource("Views/NewSpellForm.fxml"));
        FXMLLoader newGameLoader = new FXMLLoader(getClass().getResource("Views/NewGameForm.fxml"));

        Parent mainFormRoot = mainFormLoader.load();
        Parent battleRoot = battleLoader.load();
        Parent mapRoot = mapLoader.load();
        Parent settingRoot = settingLoader.load();
        Parent shopRoot = shopLoader.load();
        Parent editFormRoot = editFormLoader.load();
        Parent networkRoot = networkLoader.load();		
        Parent customFormRoot = customFormLoader.load();
        Parent editSpellsRoot = editSpellsLoader.load();
        Parent newSpellRoot = newSpellLoader.load();
        Parent newGameRoot = newGameLoader.load();


        Scene scene;

        String cssSH = getClass().getResource("resources/bootstrap3.css").toExternalForm();
        Image icon = new Image(getClass().getResource("resources/spells.png").toExternalForm());

        scene = new Scene(mainFormRoot);
        scene.getStylesheets().add(cssSH);
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ARCADIA - MainForm");
        primaryStage.setResizable(false);

        Stage stage2 = new Stage();
        scene = new Scene(battleRoot);
        scene.getStylesheets().add(cssSH);
        stage2.getIcons().add(icon);
        stage2.setScene(scene);
        stage2.setTitle("ARCADIA - Game!");
        stage2.setResizable(false);

        Stage stage3 = new Stage();
        scene = new Scene(mapRoot);
        scene.getStylesheets().add(cssSH);
        stage3.getIcons().add(icon);
        stage3.setScene(scene);
        stage3.setTitle("ARCADIA - MapForm");
        stage3.setResizable(false);

        Stage stage4 = new Stage();
        scene = new Scene(settingRoot);
        scene.getStylesheets().add(cssSH);
        stage4.getIcons().add(icon);
        stage4.setScene(scene);
        stage4.setTitle("ARCADIA - Setting");
        stage4.setResizable(false);

        Stage stage5 = new Stage();
        scene = new Scene(shopRoot);
        scene.getStylesheets().add(cssSH);
        stage5.getIcons().add(icon);
        stage5.setScene(scene);
        stage5.setTitle("ARCADIA - SHOP");
        stage5.setResizable(false);

        Stage stage6 = new Stage();
        scene = new Scene(editFormRoot);
        scene.getStylesheets().add(cssSH);
        stage6.getIcons().add(icon);
        stage6.setScene(scene);
        stage6.setTitle("ARCADIA - Edit Inventory");
        stage6.setResizable(false);


        Stage stage7 = new Stage();
        scene = new Scene(networkRoot);
        scene.getStylesheets().add(cssSH);
        stage7.getIcons().add(icon);
        stage7.setScene(scene);
        stage7.setTitle("ARCADIA - MultiPlayer");
        stage7.setResizable(false);

        Stage stage8 = new Stage();
        scene = new Scene(customFormRoot);
        scene.getStylesheets().add(cssSH);
        stage8.getIcons().add(icon);
        stage8.setScene(scene);
        stage8.setTitle("ARCADIA - Custom Game");
        stage8.setResizable(false);

        Stage stage9 = new Stage();
        scene = new Scene(editSpellsRoot);
        scene.getStylesheets().add(cssSH);
        stage9.getIcons().add(icon);
        stage9.setScene(scene);
        stage9.setTitle("ARCADIA - Edit Spells");
        stage9.setResizable(false);

        Stage stage10 = new Stage();
        scene = new Scene(newSpellRoot);
        scene.getStylesheets().add(cssSH);
        stage10.getIcons().add(icon);
        stage10.setScene(scene);
        stage10.setTitle("ARCADIA - Create New Spell");
        stage10.setResizable(false);

        Stage stage11 = new Stage();
        scene = new Scene(newGameRoot);
        scene.getStylesheets().add(cssSH);
        stage11.getIcons().add(icon);
        stage11.setScene(scene);
        stage11.setTitle("ARCADIA - Create New Game");
        stage11.setResizable(false);


        mainController = new MainController(mainFormLoader.getController(), battleLoader.getController(), mapLoader.getController(), settingLoader.getController(), shopLoader.getController(), editFormLoader.getController()
                , networkLoader.getController()
                , primaryStage, stage2, stage3, stage4, stage5, stage6
                , stage7);

        mainController.initCustomGameForms(customFormLoader.getController(), editSpellsLoader.getController(), newSpellLoader.getController(), newGameLoader.getController()
                , stage8, stage9, stage10, stage11);

        GameManager gameManager = new GameManager(mainController);

        mainController.setGameManager(gameManager);

        mainController.startMain();

    }
}
