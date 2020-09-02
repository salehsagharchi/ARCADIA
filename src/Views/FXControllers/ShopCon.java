package Views.FXControllers;

import Game.Assets.Amulet;
import Game.Assets.Card;
import Game.Assets.Item;
import Game.PlayerClasses.Player;
import Game.Shops.AmuletShop;
import Game.Shops.CardShop;
import Game.Shops.ItemShop;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.util.Optional;

public class ShopCon extends FormCon {


    public Label gil;
    public TextArea detail;
    public ListView cardList;
    public ListView amuletInven;
    public ListView itemInven;
    public ListView cardInven;
    public ListView amuletList;
    public ListView itemList;
    public AnchorPane pane;
    public Button exit;
    public Button buy;
    public Button sell;


    private String entryName = "";
    private Class entryType = null;
    private boolean isSelling = false;

    private Player targetPlayer;
    private CardShop cardShop;
    private ItemShop itemShop;
    private AmuletShop amuletShop;

    @Override
    public void start() {
        start(getGameManager().getHumanPlayer(), getGameManager().getCardShop(), getGameManager().getItemShop(), getGameManager().getAmuletShop());
    }


    public void start(Player targetPlayer, CardShop cardShop, ItemShop itemShop, AmuletShop amuletShop) {
        this.targetPlayer = targetPlayer;
        this.cardShop = cardShop;
        this.itemShop = itemShop;
        this.amuletShop = amuletShop;

        setListsBounds();

        entryName = "";
        refreshButtons();

        detail.setStyle("-fx-text-fill: #0030ff; -fx-font-size: 13;");
        detail.setText("");

        refreshGil();
        refreshShopLists();
        refreshInventoryLists();

        exit.setOnAction(event -> {
            onClose();
            getMainController().getShopStage().close();
        });

        buy.setOnAction(event -> {
            buyAnEntry();
        });

        sell.setOnAction(event -> {
            sellAnEntry();
        });
    }

    public void onClose() {
        if (targetPlayer == getGameManager().getHumanPlayer()) {
            targetPlayer.getPlayingGameClass().setPlayer(targetPlayer);
            targetPlayer.getPlayingGameClass().setItemShop(itemShop);
            targetPlayer.getPlayingGameClass().setAmuletShop(amuletShop);
            targetPlayer.getPlayingGameClass().setCardShop(cardShop);
            getGameManager().getCustomGamesManager().makeNewGame(targetPlayer.getPlayingGameClass());
        }
    }


    private void buyAnEntry() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Number To Buy");
        dialog.setHeaderText("Buy Entry : " + entryName);
        dialog.setContentText("Please enter Number To Buy : ");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            String res = "";
            if (entryType == Card.class) {
                res = cardShop.buyCard(entryName, name, getGameManager(), targetPlayer).trim();
            } else if (entryType == Item.class) {
                res = itemShop.buyItem(entryName, name, getGameManager(), targetPlayer).trim();
            } else if (entryType == Amulet.class) {
                res = amuletShop.buyAmulet(entryName, name, getGameManager(), targetPlayer).trim();
            }
            if (res.startsWith("Successfully")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("OK");
                alert.setHeaderText("Buy Entry : " + entryName);
                alert.setContentText(res);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Buy Entry : " + entryName);
                alert.setContentText(res);
                alert.showAndWait();
            }
        });

        Thread.yield();

        refreshGil();
        refreshShopLists();
        refreshInventoryLists();

        refreshDetails(entryType, entryName);
    }

    private void sellAnEntry() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Number To Sell");
        dialog.setHeaderText("Sell Entry : " + entryName);
        dialog.setContentText("Please enter Number To Sell : ");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            String res = "";
            if (entryType == Card.class) {
                res = cardShop.sellCard(entryName, name, getGameManager(), targetPlayer).trim();
            } else if (entryType == Item.class) {
                res = itemShop.sellItem(entryName, name, getGameManager(), targetPlayer).trim();
            } else if (entryType == Amulet.class) {
                res = amuletShop.sellAmulet(entryName, name, getGameManager(), targetPlayer).trim();
            }
            if (res.startsWith("Successfully")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("OK");
                alert.setHeaderText("Buy Entry : " + entryName);
                alert.setContentText(res);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Buy Entry : " + entryName);
                alert.setContentText(res);
                alert.showAndWait();
            }
        });

        Thread.yield();

        refreshGil();
        refreshShopLists();
        refreshInventoryLists();

        refreshDetails(entryType, entryName);
    }

    public void refreshDetails(Class type, String name) {
        if (!name.contains("\"")) {
            detail.setText("");
            entryName = "";
            refreshButtons();
            return;
        }
        String objectName = name.substring(name.indexOf("\"") + 1, name.lastIndexOf("\""));
        entryName = objectName;
        entryType = type;
        Platform.runLater(() -> {
            if (entryName.equals("")) {
                return;
            }
            if (type == Amulet.class) {
                detail.setText(amuletShop.findAmulet(objectName).toString());
            }
            if (type == Card.class) {
                detail.setText(cardShop.findCard(objectName).toString());
            }
            if (type == Item.class) {
                detail.setText(itemShop.findItem(objectName).toString());
            }
        });
        refreshButtons();
    }

    public void refreshButtons() {
        if (entryName.equals("")) {
            buy.setDisable(true);
            sell.setDisable(true);
        } else {
            if (isSelling) {
                buy.setDisable(true);
                sell.setDisable(false);
            } else {
                buy.setDisable(false);
                sell.setDisable(true);
            }
        }
    }

    public void refreshShopLists() {
        cardList.setItems(cardShop.getCardList(true));
        cardList.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ColorCell();
            }
        });
        cardList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isSelling = false;
                refreshDetails(Card.class, newValue.toString());
            }
        });


        itemList.setItems(itemShop.getItemList(true));
        itemList.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ColorCell();
            }
        });
        itemList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isSelling = false;
                refreshDetails(Item.class, newValue.toString());
            }
        });

        amuletList.setItems(amuletShop.getAmuletList(true));
        amuletList.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ColorCell();
            }
        });
        amuletList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isSelling = false;
                refreshDetails(Amulet.class, newValue.toString());
            }
        });
    }

    public void refreshInventoryLists() {
        cardInven.setItems(targetPlayer.getInventory().getListOfCard());
        cardInven.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ColorCell();
            }
        });
        cardInven.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isSelling = true;
                refreshDetails(Card.class, newValue.toString());
            }
        });


        itemInven.setItems(targetPlayer.getInventory().getListOfItems());
        itemInven.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ColorCell();
            }
        });
        itemInven.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isSelling = true;
                refreshDetails(Item.class, newValue.toString());
            }
        });

        amuletInven.setItems(targetPlayer.getInventory().getListOfAmulets());
        amuletInven.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ColorCell();
            }
        });
        amuletInven.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == null) {
                    return;
                }
                isSelling = true;
                refreshDetails(Amulet.class, newValue.toString());
            }
        });
    }

    public void refreshGil() {

        Platform.runLater(() -> {
            gil.setText("Remaining Gil : " + targetPlayer.getGil());
            amuletInven.getSelectionModel().clearSelection();
            cardInven.getSelectionModel().clearSelection();
            itemInven.getSelectionModel().clearSelection();
            amuletList.getSelectionModel().clearSelection();
            cardList.getSelectionModel().clearSelection();
            itemList.getSelectionModel().clearSelection();
        });
    }

    private void setListsBounds() {
        cardList.setPrefWidth(cardList.getParent().getBoundsInLocal().getWidth());
        cardList.setPrefHeight(cardList.getParent().getBoundsInLocal().getHeight());
        itemList.setPrefWidth(itemList.getParent().getBoundsInLocal().getWidth());
        itemList.setPrefHeight(itemList.getParent().getBoundsInLocal().getHeight());
        amuletList.setPrefWidth(amuletList.getParent().getBoundsInLocal().getWidth());
        amuletList.setPrefHeight(amuletList.getParent().getBoundsInLocal().getHeight());
        cardInven.setPrefWidth(cardInven.getParent().getBoundsInLocal().getWidth());
        cardInven.setPrefHeight(cardInven.getParent().getBoundsInLocal().getHeight());
        itemInven.setPrefWidth(itemInven.getParent().getBoundsInLocal().getWidth());
        itemInven.setPrefHeight(itemInven.getParent().getBoundsInLocal().getHeight());
        amuletInven.setPrefWidth(amuletInven.getParent().getBoundsInLocal().getWidth());
        amuletInven.setPrefHeight(amuletInven.getParent().getBoundsInLocal().getHeight());
    }

    static class ColorCell extends ListCell<String> {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                Label label = new Label();
                if (item.contains(";")) {
                    label.setTextFill(Color.valueOf(item.substring(0, item.indexOf(";"))));
                }
                label.setText(item.substring(item.indexOf(";") + 1));
                label.setFont(new Font("Consolas", 13));
                setGraphic(label);

            }
            return;
        }
    }
}


