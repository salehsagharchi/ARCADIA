package Views.FXControllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class CustomCon extends FormCon {
    public Button newGame;
    public ListView games;
    public Label lblExtend;
    public Label lblGames;
    public Button playGame;
    public Button deleteGame;
    public Button editSpl;
    private String selectedGame = "";

    @Override
    public void start() {
        refreshList();

        playGame.setOnAction(event -> {
            getGameManager().getCustomGamesManager().playGame(selectedGame);
            refreshList();
        });

        deleteGame.setOnAction(event -> {
            Platform.runLater(() -> {
                String res = getGameManager().getCustomGamesManager().deleteGame(selectedGame);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Delete Game");
                alert.setHeaderText("");
                alert.setContentText("Delete Game : " + selectedGame + "\n" + res);
                alert.showAndWait();
                refreshList();
            });
        });

        newGame.setOnAction(event -> {
            getGameManager().getCustomGamesManager().newGame(selectedGame);
            refreshList();
        });

        editSpl.setOnAction(event -> {
            getGameManager().getMainController().startEditSpells();
        });
    }

    public void refreshList() {
        Platform.runLater(() -> {
            ObservableList<String> items = getGameManager().getCustomGamesManager().getGameListItems();
            games.setItems(items);
            games.setCellFactory(new Callback<ListView, ListCell>() {
                @Override
                public ListCell call(ListView param) {
                    return (new ListCell() {
                        @Override
                        protected void updateItem(Object o, boolean empty) {
                            super.updateItem(o, empty);
                            String item = (String) o;
                            if (item != null) {
                                Button button = new Button();
                                button.setFont(new Font("Consolas", 8));
                                button.setStyle("-fx-background-color: #ff4860");
                                button.setText(item);
                                button.setOnAction(event -> {
                                    selectedGame = item.trim();
                                    lblExtend.setText("Extends : " + item.trim());
                                    playGame.setText("Play : " + item.trim());
                                    playGame.setDisable(false);
                                    deleteGame.setText("Delete : " + item.trim());
                                    deleteGame.setDisable(false);
                                });
                                setGraphic(button);
                            }
                            return;
                        }
                    });
                }
            });
        });
    }
}
