package Views.FXControllers;

import Game.Assets.Amulet;
import Game.Assets.Card;
import Game.Assets.Item;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.util.Optional;

public class EditFormCon extends FormCon {
    public ListView cardInven;
    public ListView itemInven;
    public ListView amuletInven;
    public Label gil;
    public TextArea detail;
    public Label amuletE;
    public ListView deck;
    public Button add;
    public Button remove;
    public Button exit;
    public Button equip;
    public Button removeAmulet;

    private String entryName = "";
    private int slotNum;
    private Class entryType = null;
    private boolean isAdding;
    private boolean equipping;

    @Override
    public void start() {
        setListsBounds();

        entryName = "";
        isAdding = false;
        equipping = false;
        slotNum = -1;
        refreshButtons();

        detail.setStyle("-fx-text-fill: #0030ff; -fx-font-size: 13;");
        detail.setText("");

        refreshGil();
        refreshLists();

        exit.setOnAction(event -> {
            onClose();
            getMainController().getEditFormStage().close();
        });

        add.setOnAction(event -> {
            addAnEntry();
        });

        equip.setOnAction(event -> {
            addAnEntry();
        });

        remove.setOnAction(event -> {
            removeAnEntry();
        });

        removeAmulet.setOnAction(event -> {
            String res = getGameManager().getHumanPlayer().getInventory().removeEquippedAmulet();
            if (!res.startsWith("Successfully")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Remove Amulet : ");
                alert.setContentText(res);
                alert.showAndWait();
            }
            Thread.yield();
            refreshGil();
            refreshLists();
            refreshDetails(entryType, entryName);
        });
    }

    public void onClose() {
        getGameManager().getHumanPlayer().getPlayingGameClass().setPlayer(getGameManager().getHumanPlayer());
        getGameManager().getHumanPlayer().getPlayingGameClass().setItemShop(getGameManager().getItemShop());
        getGameManager().getHumanPlayer().getPlayingGameClass().setAmuletShop(getGameManager().getAmuletShop());
        getGameManager().getHumanPlayer().getPlayingGameClass().setCardShop(getGameManager().getCardShop());
        getGameManager().getCustomGamesManager().makeNewGame(getGameManager().getHumanPlayer().getPlayingGameClass());
    }


    private void addAnEntry() {
        if (entryName.equals("")) {
            return;
        }
        if (entryType == Amulet.class) {
            String res = "";
            res = getGameManager().getHumanPlayer().getInventory().equipAmulet(entryName).trim();
            if (res.startsWith("Successfully")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("OK");
                alert.setHeaderText("Equip Amulet : " + entryName);
                alert.setContentText(res);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Equip Amulet : " + entryName);
                alert.setContentText(res);
                alert.showAndWait();
            }

            Thread.yield();

            refreshGil();
            refreshLists();

            refreshDetails(entryType, "");
            return;
        }


        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Slot");
        dialog.setHeaderText("Add Entry : " + entryName);
        dialog.setContentText("Please enter Slot To Fill : ");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            String res;
            res = getGameManager().getHumanPlayer().getInventory().addCardToDeck(entryName, name);
            if (!res.startsWith("Successfully")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Add Entry : " + entryName);
                alert.setContentText(res);
                alert.showAndWait();
            }
        });

        Thread.yield();

        refreshGil();
        refreshLists();

        refreshDetails(entryType, "");
    }

    private void removeAnEntry() {
        System.out.println(slotNum);
        if (slotNum == -1) {
            return;
        }

        String res = getGameManager().getHumanPlayer().getInventory().removeCardFromDeck(String.valueOf(slotNum));
        if (!res.startsWith("Successfully")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Remove Slot : " + slotNum);
            alert.setContentText(res);
            alert.showAndWait();
        }

        Thread.yield();
        refreshGil();
        refreshLists();
        refreshDetails(entryType, "");
    }

    public void refreshButtons() {
        if (slotNum == -1) {
            remove.setDisable(true);
        } else {
            add.setDisable(true);
            remove.setDisable(false);
            equip.setDisable(true);
            return;
        }

        if (equipping) {
            add.setDisable(true);
            equip.setDisable(false);
        } else {
            if (isAdding) {
                add.setDisable(false);
            } else {
                add.setDisable(true);
            }
            equip.setDisable(true);
        }

    }

    public void refreshDetails(Class type, String name) {
        if (!name.contains("\"")) {
            detail.setText("");
            entryName = "";
            isAdding = false;
            equipping = false;
            slotNum = -1;
            refreshButtons();
            return;
        }
        if (name.contains(";")) {
            name = name.substring(name.indexOf(";") + 1);
        }

        if (name.startsWith("Slot")) {
            slotNum = Integer.parseInt(name.split(":")[0].replace("Slot", "").trim());
        } else {
            slotNum = -1;
        }

        String objectName = name.substring(name.indexOf("\"") + 1, name.lastIndexOf("\""));
        entryName = objectName;
        entryType = type;


        Platform.runLater(() -> {
            if (entryName.equals("")) {
                return;
            }
            if (type == Amulet.class) {
                detail.setText(getGameManager().getAmuletShop().findAmulet(entryName).toString());
            }
            if (type == Card.class) {
                detail.setText(getGameManager().getCardShop().findCard(entryName).toString());
            }
            if (type == Item.class) {
                detail.setText(getGameManager().getItemShop().findItem(entryName).toString());
            }
        });
        refreshButtons();
    }

    public void refreshLists() {
        String equipped = getGameManager().getHumanPlayer().getBag().equippedAmulet();
        Platform.runLater(() -> {
            if (!equipped.startsWith("NULL")) {
                amuletE.setText("Player Equipped Amulet : \"" + equipped + "\"");
                amuletE.setTextFill(Color.YELLOW);
                removeAmulet.setDisable(false);
            } else {
                amuletE.setText("No Equipped Amulet");
                amuletE.setTextFill(Color.RED);
                removeAmulet.setDisable(true);
            }
        });


        cardInven.setItems(getGameManager().getHumanPlayer().getInventory().getListOfCard());
        cardInven.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ShopCon.ColorCell();
            }
        });
        cardInven.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isAdding = true;
                equipping = false;
                slotNum = -1;
                amuletInven.getSelectionModel().clearSelection();
                itemInven.getSelectionModel().clearSelection();
                refreshDetails(Card.class, newValue.toString());
            }
        });


        itemInven.setItems(getGameManager().getHumanPlayer().getInventory().getListOfItems());
        itemInven.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ShopCon.ColorCell();
            }
        });
        itemInven.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isAdding = false;
                equipping = false;
                slotNum = -1;
                amuletInven.getSelectionModel().clearSelection();
                cardInven.getSelectionModel().clearSelection();
                refreshDetails(Item.class, newValue.toString());
            }
        });

        amuletInven.setItems(getGameManager().getHumanPlayer().getInventory().getListOfAmulets());
        amuletInven.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ShopCon.ColorCell();
            }
        });
        amuletInven.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isAdding = false;
                equipping = true;
                slotNum = -1;
                cardInven.getSelectionModel().clearSelection();
                itemInven.getSelectionModel().clearSelection();
                refreshDetails(Amulet.class, newValue.toString());
            }
        });

        deck.setItems(getGameManager().getHumanPlayer().getBag().getDeck().getList());
        deck.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ShopCon.ColorCell();
            }
        });
        deck.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isAdding = false;
                equipping = false;
                refreshDetails(Card.class, newValue.toString());
            }
        });
    }

    public void refreshGil() {
        Platform.runLater(() -> {
            gil.setText("Remaining Gil : " + getGameManager().getHumanPlayer().getGil());
            amuletInven.getSelectionModel().clearSelection();
            cardInven.getSelectionModel().clearSelection();
            itemInven.getSelectionModel().clearSelection();
            deck.getSelectionModel().clearSelection();
        });
    }

    private void setListsBounds() {
        cardInven.setPrefWidth(cardInven.getParent().getBoundsInLocal().getWidth());
        cardInven.setPrefHeight(cardInven.getParent().getBoundsInLocal().getHeight());
        itemInven.setPrefWidth(itemInven.getParent().getBoundsInLocal().getWidth());
        itemInven.setPrefHeight(itemInven.getParent().getBoundsInLocal().getHeight());
        amuletInven.setPrefWidth(amuletInven.getParent().getBoundsInLocal().getWidth());
        amuletInven.setPrefHeight(amuletInven.getParent().getBoundsInLocal().getHeight());
    }

}
