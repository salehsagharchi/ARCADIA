package Views.FXControllers;

import Game.Assets.*;
import Game.Constants.Initializer_Statics;
import Game.Custom.GameClass;
import Game.Custom.SpellOwner;
import Game.Enums.CardType;
import Game.Enums.MonsterTribe;
import Game.Enums.MonsterType;
import Game.Enums.SpellType;
import Game.PlayerClasses.Deck;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author SALEHSAGHARCHI
 * Date: 2018-07-28
 * Time: 6:37 PM
 */
public class NewGameCon extends FormCon {
    // Card
    public ChoiceBox<String> cardClass;
    public ChoiceBox<String> cardType;
    public Button addCard;
    public Button deleteCard;
    public TextField cardName;
    public TextField cardPrice;
    public TextField cardMP;
    public ListView cardsList;
    public AnchorPane monsterGroup;
    public TextField cardHP;
    public TextField cardAP;
    public ChoiceBox<String> tribes;
    public ChoiceBox<String> spell1;
    public ChoiceBox<String> spell2;
    public ChoiceBox<String> spell3;
    public CheckBox cardDefensive;
    public CheckBox cardNimble;
    //
    // Item
    public Button addItem;
    public TextField itemName;
    public Button deleteItem;
    public TextField itemPrice;
    public ListView itemsList;
    public ChoiceBox<String> itemSpell;
    //
    // Amulet
    public Button addAmulet;
    public TextField amuletName;
    public Button deleteAmulet;
    public TextField amuletPrice;
    public ChoiceBox<String> amuletSpell;
    public ListView amuletsList;
    //
    public TextField gameName;
    public Button newGame;
    public Button goShop;

    private GameClass toCreate;


    @Override
    public void start() {
        refreshLists();
        refreshChoices();

        cardName.textProperty().addListener((observable, oldValue, newValue) -> refreshCardInfo(newValue));
        itemName.textProperty().addListener((observable, oldValue, newValue) -> refreshItemInfo(newValue));
        amuletName.textProperty().addListener((observable, oldValue, newValue) -> refreshAmuInfo(newValue));

        deleteCard.setOnAction(event -> {
            if (toCreate.getCardMap().containsKey(cardName.getText())) {
                toCreate.getCardMap().remove(cardName.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Delete Card");
                alert.setHeaderText("");
                alert.setContentText("Delete Card : " + cardName.getText() + "\n" + "OK");
                alert.showAndWait();
                refreshLists();
                refreshChoices();
                resetShop();
            }
        });

        deleteItem.setOnAction(event -> {
            if (toCreate.getItemMap().containsKey(itemName.getText())) {
                toCreate.getItemMap().remove(itemName.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Delete Item");
                alert.setHeaderText("");
                alert.setContentText("Delete Item : " + itemName.getText() + "\n" + "OK");
                alert.showAndWait();
                refreshLists();
                refreshChoices();
                resetShop();
            }
        });

        deleteAmulet.setOnAction(event -> {
            if (toCreate.getAmuletMap().containsKey(amuletName.getText())) {
                toCreate.getAmuletMap().remove(amuletName.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Delete Amulet");
                alert.setHeaderText("");
                alert.setContentText("Delete Amulet : " + amuletName.getText() + "\n" + "OK");
                alert.showAndWait();
                refreshLists();
                refreshChoices();
                resetShop();
            }
        });

        addCard.setOnAction(event -> {
            if (!getValueOfChoiceBox(cardClass).equals("") && !cardName.getText().trim().equals("")) {
                if (!getValueOfChoiceBox(cardType).equals("")) {
                    if (spell1.isDisabled() || !getValueOfChoiceBox(spell1).equals("")) {
                        if (spell2.isDisabled() || !getValueOfChoiceBox(spell2).equals("")) {
                            if (spell3.isDisabled() || !getValueOfChoiceBox(spell3).equals("")) {
                                if (validNumber(cardPrice) && validNumber(cardMP)) {
                                    if (cardClass.getValue().startsWith("Mon")) {
                                        if (!getValueOfChoiceBox(tribes).equals("") && validNumber(cardHP) && validNumber(cardAP)) {
                                            String magicCmd = "";
                                            magicCmd += getGameManager().getCustomGamesManager().getMagicCommandOfSpell(getValueOfChoiceBox(spell1)) + "*";
                                            magicCmd += getGameManager().getCustomGamesManager().getMagicCommandOfSpell(getValueOfChoiceBox(spell2)) + "*";
                                            magicCmd += getGameManager().getCustomGamesManager().getMagicCommandOfSpell(getValueOfChoiceBox(spell3));
                                            if (magicCmd.equals("**")) {
                                                magicCmd = "";
                                            }
                                            MonsterCard newCard = new MonsterCard(MonsterTribe.valueOf(tribes.getValue().trim()), cardName.getText().trim(),
                                                    Integer.parseInt(cardHP.getText().trim()), Integer.parseInt(cardAP.getText().trim()),
                                                    Integer.parseInt(cardMP.getText().trim()), MonsterType.valueOf(cardType.getValue().trim()), cardDefensive.isSelected(), cardNimble.isSelected(),
                                                    getValueOfChoiceBox(spell1), getValueOfChoiceBox(spell2), getValueOfChoiceBox(spell3),
                                                    magicCmd, "", Integer.parseInt(cardPrice.getText().trim()));

                                            toCreate.getCardMap().put(newCard.getName(), newCard);
                                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                            alert.setTitle("Create/Update Card");
                                            alert.setHeaderText("");
                                            alert.setContentText("Create/Update Card : " + newCard.getName() + "\n" + "OK");
                                            alert.showAndWait();
                                            refreshLists();
                                            refreshChoices();
                                            System.out.println(magicCmd);
                                            resetShop();
                                        }
                                    } else {
                                        SpellCard newCard = new SpellCard(cardName.getText().trim(), Integer.parseInt(cardMP.getText().trim()), SpellType.valueOf(cardType.getValue().trim()), getValueOfChoiceBox(spell1),
                                                getGameManager().getCustomGamesManager().getMagicCommandOfSpell(getValueOfChoiceBox(spell1)), "",
                                                Integer.parseInt(cardPrice.getText().trim()));

                                        toCreate.getCardMap().put(newCard.getName(), newCard);
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Create/Update Card");
                                        alert.setHeaderText("");
                                        alert.setContentText("Create/Update Card : " + newCard.getName() + "\n" + "OK");
                                        alert.showAndWait();
                                        refreshLists();
                                        refreshChoices();
                                        resetShop();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });


        addItem.setOnAction(event -> {
            if (!itemName.getText().trim().equals("")) {
                if (!getValueOfChoiceBox(itemSpell).equals("")) {
                    if (validNumber(itemPrice)) {
                        Item newItem = new Item(itemName.getText().trim(), Integer.parseInt(itemPrice.getText().trim()),
                                getGameManager().getCustomGamesManager().getMagicCommandOfSpell(getValueOfChoiceBox(itemSpell)), getValueOfChoiceBox(itemSpell));
                        toCreate.getItemMap().put(newItem.getName(), newItem);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Create/Update Item");
                        alert.setHeaderText("");
                        alert.setContentText("Create/Update Item : " + newItem.getName() + "\n" + "OK");
                        alert.showAndWait();
                        refreshLists();
                        refreshChoices();
                        resetShop();
                    }
                }
            }
        });

        addAmulet.setOnAction(event -> {
            if (!amuletName.getText().trim().equals("")) {
                if (!getValueOfChoiceBox(amuletSpell).equals("")) {
                    if (validNumber(amuletPrice)) {
                        Amulet newAmulet = new Amulet(amuletName.getText().trim(), Integer.parseInt(amuletPrice.getText().trim()),
                                getGameManager().getCustomGamesManager().getMagicCommandOfSpell(getValueOfChoiceBox(amuletSpell)), getValueOfChoiceBox(amuletSpell));
                        toCreate.getAmuletMap().put(newAmulet.getName(), newAmulet);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Create/Update Amulet");
                        alert.setHeaderText("");
                        alert.setContentText("Create/Update Amulet : " + newAmulet.getName() + "\n" + "OK");
                        alert.showAndWait();
                        refreshLists();
                        refreshChoices();
                        resetShop();
                    }
                }
            }
        });

        goShop.setOnAction(event -> {
            getMainController().startShop(toCreate.getPlayer(), toCreate.getCardShop(), toCreate.getItemShop(), toCreate.getAmuletShop());
        });

        newGame.setOnAction(event -> {
            if (!gameName.getText().trim().equals("")) {
                toCreate.setGameName(gameName.getText().trim());
                ArrayList<GameClass> gameClasses = getGameManager().getCustomGamesManager().getGameClasses();
                AtomicBoolean find = new AtomicBoolean(false);
                gameClasses.forEach(gameClass -> {
                    if (gameClass.getFileName().equals(toCreate.getFileName())) {
                        find.set(true);
                    }
                });
                if (!find.get()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Create Game");
                    alert.setHeaderText("");
                    alert.setContentText("Create Game : " + toCreate.getGameName() + "\n" + "OK");
                    alert.showAndWait();
                    getGameManager().getCustomGamesManager().makeNewGame(toCreate);
                    getMainController().getNewGameStage().close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Create Game");
                    alert.setHeaderText("");
                    alert.setContentText("Create Game : " + toCreate.getGameName() + "\n" + "Error : Name Isn't Unique");
                    alert.showAndWait();
                }
            }
        });

    }

    private void resetShop() {
        toCreate.getPlayer().getInventory().clearCards();
        toCreate.getPlayer().getInventory().clearAmulet();
        toCreate.getPlayer().getInventory().clearItems();
        toCreate.getPlayer().getInventory().removeEquippedAmulet();
        toCreate.getPlayer().getBag().setDeck(new Deck(false));
        toCreate.getPlayer().setGil(248000); // Price Of Default Inventory
        toCreate.getCardShop().clearCards();
        toCreate.getCardMap().forEach((s, card) -> {
            toCreate.getCardShop().addCard(card);
        });
        toCreate.getItemShop().clearItems();
        toCreate.getItemMap().forEach((s, item) -> {
            toCreate.getItemShop().addItem(item);
        });
        toCreate.getAmuletShop().clearAmulets();
        toCreate.getAmuletMap().forEach((s, amu) -> {
            toCreate.getAmuletShop().addAmulet(amu);
        });

        Initializer_Statics.setCardList(toCreate.getCardMap());
        Initializer_Statics.setAmuletList(toCreate.getAmuletMap());
        Initializer_Statics.setItemList(toCreate.getItemMap());
    }

    private void refreshChoices() {
        spell1.getItems().clear();
        spell2.getItems().clear();
        spell3.getItems().clear();
        itemSpell.getItems().clear();
        amuletSpell.getItems().clear();
        spell1.getItems().addAll(getGameManager().getCustomGamesManager().getSpellsListFor(SpellOwner.ForCard));
        spell2.getItems().addAll(getGameManager().getCustomGamesManager().getSpellsListFor(SpellOwner.ForCard));
        spell3.getItems().addAll(getGameManager().getCustomGamesManager().getSpellsListFor(SpellOwner.ForCard));
        itemSpell.getItems().addAll(getGameManager().getCustomGamesManager().getSpellsListFor(SpellOwner.ForItem));
        amuletSpell.getItems().addAll(getGameManager().getCustomGamesManager().getSpellsListFor(SpellOwner.ForAmulet));

        cardClass.getItems().clear();
        cardClass.getItems().add("MonsterCard");
        cardClass.getItems().add("SpellCard");
        cardClass.setOnAction(event -> {
            if (getValueOfChoiceBox(cardClass).startsWith("Mon")) {
                monsterGroup.setVisible(true);
                cardType.getItems().clear();
                cardType.getItems().addAll(MonsterType.Normal.toString(), MonsterType.SpellCaster.toString(), MonsterType.General.toString(), MonsterType.Hero.toString());
                tribes.getItems().clear();
                tribes.getItems().addAll(MonsterTribe.Atlantian.toString(), MonsterTribe.DragonBreed.toString(), MonsterTribe.Elven.toString(), MonsterTribe.Demonic.toString(),
                        MonsterTribe.Vampiric.toString(), MonsterTribe.Ogre.toString(), MonsterTribe.Goblin.toString());
            } else {
                monsterGroup.setVisible(false);
                cardType.getItems().clear();
                cardType.getItems().add(SpellType.Instant.toString());
                cardType.getItems().add(SpellType.Aura.toString());
                cardType.getItems().add(SpellType.Continuous.toString());
                tribes.getItems().clear();
            }
        });

        cardType.setOnAction(event -> {
            switch (getValueOfChoiceBox(cardType)) {
                case "Normal":
                    setSpellsEnable(0, 0, 0);
                    break;
                case "SpellCaster":
                    setSpellsEnable(0, 1, 0);
                    break;
                case "Hero":
                    setSpellsEnable(1, 1, 1);
                    break;
                case "General":
                    setSpellsEnable(1, 0, 1);
                    break;
                case "Instant":
                    setSpellsEnable(1, 0, 0);
                    break;
                case "Aura":
                    setSpellsEnable(1, 0, 0);
                    break;
                case "Continuous":
                    setSpellsEnable(1, 0, 0);
                    break;
                default:
                    setSpellsEnable(0, 0, 0);
                    break;
            }
        });


    }

    private void refreshCardInfo(String name) {
        if (toCreate.getCardMap().containsKey(name)) {
            Card card = toCreate.getCardMap().get(name);
            cardName.setText(name);
            cardPrice.setText(String.valueOf(card.getPrice()));
            cardMP.setText(String.valueOf(card.getCostMP()));
            if (card.getType() == CardType.Monster) {
                MonsterCard monsterCard = (MonsterCard) card;
                cardClass.setValue("MonsterCard");
                cardHP.setText(String.valueOf(monsterCard.getDefaultHP()));
                cardAP.setText(String.valueOf(monsterCard.getDefaultAP()));
                cardType.setValue(monsterCard.getMonsterType().toString());
                tribes.setValue(monsterCard.getTribe().toString());
                spell1.setValue(monsterCard.getBattleCryDet());
                spell2.setValue(monsterCard.getSpellDet());
                spell3.setValue(monsterCard.getWillDet());
                cardDefensive.setSelected(monsterCard.isDefensive());
                cardNimble.setSelected(monsterCard.isNimble());
            } else {
                SpellCard spellCard = (SpellCard) card;
                cardClass.setValue("SpellCard");
                cardType.setValue(spellCard.getSpellType().toString());
                spell1.setValue(spellCard.getSpellDet());
            }
            deleteCard.setDisable(false);
            addCard.setText("Update Card");
        } else {
            cardName.setText(name);
            deleteCard.setDisable(true);
            addCard.setText("Add Card");
        }
    }

    private void refreshItemInfo(String name) {
        if (toCreate.getItemMap().containsKey(name)) {
            Item item = toCreate.getItemMap().get(name);
            itemName.setText(name);
            itemPrice.setText(String.valueOf(item.getPrice()));
            itemSpell.setValue(item.getDescription());
            deleteItem.setDisable(false);
            addItem.setText("Update Item");
        } else {
            itemName.setText(name);
            deleteItem.setDisable(true);
            addItem.setText("Add Item");
        }
    }

    private void refreshAmuInfo(String name) {
        if (toCreate.getAmuletMap().containsKey(name)) {
            Amulet amulet = toCreate.getAmuletMap().get(name);
            amuletName.setText(name);
            amuletPrice.setText(String.valueOf(amulet.getPrice()));
            amuletSpell.setValue(amulet.getDescription());
            deleteAmulet.setDisable(false);
            addAmulet.setText("Update Amulet");
        } else {
            amuletName.setText(name);
            deleteAmulet.setDisable(true);
            addAmulet.setText("Add Amulet");
        }
    }

    private String getValueOfChoiceBox(ChoiceBox choiceBox) {
        if (choiceBox.getValue() == null) {
            return "";
        } else {
            return choiceBox.getValue().toString().trim();
        }
    }

    private void setSpellsEnable(int s1, int s2, int s3) {
        spell1.setDisable(s1 == 0);
        spell2.setDisable(s2 == 0);
        spell3.setDisable(s3 == 0);
        spell1.setValue(null);
        spell2.setValue(null);
        spell3.setValue(null);
    }

    private void refreshLists() {
        Platform.runLater(() -> {
            ObservableList<String> cards = convertMapToList(toCreate.getCardMap());
            cardsList.setItems(cards);
            cardsList.setCellFactory(new Callback<ListView, ListCell>() {
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
                                button.setStyle("-fx-background-color: #47d8ff");
                                button.setText(item);
                                button.setOnAction(event -> {
                                    refreshCardInfo(item.trim());
                                });
                                setGraphic(button);
                            }
                            return;
                        }
                    });
                }
            });
            ObservableList<String> items = convertMapToList(toCreate.getItemMap());
            itemsList.setItems(items);
            itemsList.setCellFactory(new Callback<ListView, ListCell>() {
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
                                button.setStyle("-fx-background-color: #47d8ff");
                                button.setText(item);
                                button.setOnAction(event -> {
                                    refreshItemInfo(item.trim());
                                });
                                setGraphic(button);
                            }
                            return;
                        }
                    });
                }
            });
            ObservableList<String> amus = convertMapToList(toCreate.getAmuletMap());
            amuletsList.setItems(amus);
            amuletsList.setCellFactory(new Callback<ListView, ListCell>() {
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
                                button.setStyle("-fx-background-color: #47d8ff");
                                button.setText(item);
                                button.setOnAction(event -> {
                                    refreshAmuInfo(item.trim());
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

    private <T> ObservableList<String> convertMapToList(Map<String, T> map) {
        ObservableList<String> resultList = FXCollections.observableArrayList();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            resultList.add(entry.getKey());
        }
        return resultList;
    }

    private boolean validNumber(TextField field) {
        String text = field.getText();
        try {
            int val = Integer.parseInt(text);
            if (val > 0) {
                return true;
            }
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public GameClass getToCreate() {
        return toCreate;
    }

    public void setToCreate(GameClass toCreate) {
        this.toCreate = toCreate;
    }
}
