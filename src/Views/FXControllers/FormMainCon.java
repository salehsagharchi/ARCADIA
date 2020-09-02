package Views.FXControllers;

import javafx.application.Platform;
import javafx.scene.control.Button;

public class FormMainCon extends FormCon {
    public Button single;
    public Button custom;
    public Button multi;
    public Button setting;
    public Button exit;

    @Override
    public void start() {
        initializeActions();

    }

    private void initializeActions() {
        exit.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });

        single.setOnAction(event -> {
            getGameManager().getCustomGamesManager().playGame("Single Player");
        });

        setting.setOnAction(event -> {
            getMainController().startSetting();
        });

        multi.setOnAction(event -> {
            getMainController().startNetworkForm();
        });

        custom.setOnAction(event -> {
            getMainController().startCustomForm();
        });
    }
}
