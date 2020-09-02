package Views.FXControllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class EditSpellsCon extends FormCon {
    public Button newSpell;
    public ListView spellsList;
    public TextField mgcCmd;
    public TextField ownerTxt;

    @Override
    public void start() {
        refreshList();

        newSpell.setOnAction(event -> {
            getGameManager().getMainController().startNewSpell();
            refreshList();
        });
    }

    public void refreshList() {
        Platform.runLater(() -> {
            ObservableList<String> items = getGameManager().getCustomGamesManager().getSpellsListItems();
            spellsList.setItems(items);
            spellsList.setCellFactory(new Callback<ListView, ListCell>() {
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
                                button.setStyle("-fx-background-color: #009eff");
                                button.setText(item);
                                button.setOnAction(event -> {
                                    mgcCmd.setText(getGameManager().getCustomGamesManager().getMagicCommandOfSpell(item));
                                    ownerTxt.setText(getGameManager().getCustomGamesManager().getOwnerOfSpell(item));
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
