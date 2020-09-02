package Views.FXControllers;

import Game.Assets.Card;
import Game.Assets.Item;
import Game.Assets.MonsterCard;
import Game.Assets.SpellCard;
import Game.BattleManaging.Battle;
import Game.BattleManaging.CanBeTarget;
import Game.Elements.BagElement;
import Game.Enums.BattleState;
import Game.Enums.MonsterType;
import Game.Exceptions.DeadPlayerException;
import Game.PlayerClasses.*;
import Game.States.StateManager;
import Views.FXControllers.Map.Blocks;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class BattleFormCon extends FormCon {
    public Pane temp;
    public AnchorPane form;
    public Label Hgrave;
    public TextArea detail;
    public TextArea messages;
    public AnchorPane hand_s1;
    public AnchorPane hand_s2;
    public AnchorPane hand_s3;
    public AnchorPane hand_s4;
    public AnchorPane hand_s5;
    public Label nameHuman;
    public Label HlblMP;
    public ProgressBar HprogMP;
    public Label HlblHP;
    public ProgressBar HprogHP;
    public Label ElblMP;
    public ProgressBar EprogMP;
    public Label ElblHP;
    public ProgressBar EprogHP;
    public Label Hdeck;
    public Label Egrave;
    public Label Edeck;
    public Label nameEnemy;
    public ListView items;
    public AnchorPane Espel_s1;
    public AnchorPane Espel_s2;
    public AnchorPane Espel_s3;
    public AnchorPane Emon_s2;
    public AnchorPane Emon_s3;
    public AnchorPane Emon_s4;
    public AnchorPane Emon_s5;
    public AnchorPane Emon_s1;
    public AnchorPane Hmon_s2;
    public AnchorPane Hmon_s3;
    public AnchorPane Hmon_s4;
    public AnchorPane Hspel_s1;
    public AnchorPane Hspel_s2;
    public AnchorPane Hspel_s3;
    public AnchorPane Hmon_s5;
    public AnchorPane Hmon_s1;

    public Label Eamu;
    public Label Hamu;
    public Button useItem;
    public Button attack;
    public Button dospell;
    public Button draw;
    public Button endTurn;
    public Label turnNumber;


    public ArrayList<AnchorPane> handSlots;
    public ArrayList<AnchorPane> enemyMonF;
    public ArrayList<AnchorPane> enemySplF;
    public ArrayList<AnchorPane> humanMonF;
    public ArrayList<AnchorPane> humanSplF;
    public Button exit;
    public AnchorPane tempSlot;
    private ArrayList<CardSlotPane> panePool;
    private Battle battle;
    private Blocks battleBlock;
    private boolean isMyTurn;

    private void initializeNodeArrays() {
        handSlots = new ArrayList<>();
        handSlots.add(hand_s1);
        handSlots.add(hand_s2);
        handSlots.add(hand_s3);
        handSlots.add(hand_s4);
        handSlots.add(hand_s5);
        humanMonF = new ArrayList<>();
        humanMonF.add(Hmon_s1);
        humanMonF.add(Hmon_s2);
        humanMonF.add(Hmon_s3);
        humanMonF.add(Hmon_s4);
        humanMonF.add(Hmon_s5);
        humanSplF = new ArrayList<>();
        humanSplF.add(Hspel_s1);
        humanSplF.add(Hspel_s2);
        humanSplF.add(Hspel_s3);
        enemyMonF = new ArrayList<>();
        enemyMonF.add(Emon_s1);
        enemyMonF.add(Emon_s2);
        enemyMonF.add(Emon_s3);
        enemyMonF.add(Emon_s4);
        enemyMonF.add(Emon_s5);
        enemySplF = new ArrayList<>();
        enemySplF.add(Espel_s1);
        enemySplF.add(Espel_s2);
        enemySplF.add(Espel_s3);
    }

    private void initializeEvents() {
        detail.setStyle("-fx-text-fill: #0030ff; -fx-font-size: 13;");
        detail.setText("");

        messages.setStyle("-fx-text-fill: #0b510f; -fx-font-size: 13;");
        messages.setText("");


        CardSlotPane pane = new CardSlotPane(null, temp);
        pane.setRotate(0);
        tempSlot.getChildren().clear();
        tempSlot.getChildren().add(pane.asPane());
        pane.changeXY(0, 0);
        tempSlot.setVisible(false);

        panePool = new ArrayList<>();

        endTurn.setOnAction(event -> {
            if (battle.isNetworkGaming()) {
                battle.nextPlayerNetwork();
            } else {
                battle.nextPlayer();
            }
        });

        Hgrave.setOnMouseClicked(event -> {
            refreshDetail(null, battle.getHumanPlayer().getBag().getGraveyard().showList());
        });

        Egrave.setOnMouseClicked(event -> {
            refreshDetail(null, battle.getEnemyPlayer().getBag().getGraveyard().showList());
        });

        exit.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("EXIT");
            alert.setHeaderText("Exit From Battle");
            alert.setContentText("Do You Want To Exit Game Without Saving ?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.OK) {
                onClose();
            }

        });

    }

    public void startWithBattleForSocket(Battle battle) {
        this.battleBlock = null;
        this.battle = battle;
        battle.settingSocketGaming(this);
        start();
    }

    public void startWithBattle(Blocks battleBlock) {
        this.battleBlock = battleBlock;
        int num = -1;
        switch (battleBlock) {
            case BATTLE1:
                num = 0;
                break;
            case BATTLE2:
                num = 1;
                break;
            case BATTLE3:
                num = 2;
                break;
            case LUCIFER:
                num = 3;
                break;
        }
        if (num == -1) {
            getMainController().getBattleFormStage().close();
        }
        for (int i = 0; i < num; i++) {
            getGameManager().getBattle(i).setState(BattleState.Ended);
        }
        battle = getGameManager().getBattle(num);
        // Saving State For Mystic Item
        {
            StateManager.saveState("state.ser", getGameManager().getHumanPlayer().getBag().getDeck(),
                    getGameManager().getHumanPlayer().getInventory(),
                    getGameManager());
        }
        start();
    }

    @Override
    public void start() {
        initializeNodeArrays();
        battle.startBattle(this);
        refreshAll();
        initializeEvents();

    }


    public void onClose() {
        System.out.println("onclose");
        battle.endBattleWithoutSaving();
        if (battle.isNetworkGaming()) {
            battle.endNetworkBattle(true);
        } else {
            getMainController().getBattleFormStage().close();
        }
    }


    public void battleWon() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Congratulations!");
            alert.setHeaderText(battle.getBattleName());
            alert.setContentText("You Won \"" + battle.getBattleName() + "\" Battle Successfully !");
            alert.showAndWait();
            if (!battle.isNetworkGaming()) {
                getMainController().getMapFormCon().convert(battleBlock, Blocks.CHAMAN);
                if (battleBlock == Blocks.BATTLE1) {
                    getMainController().getMapFormCon().convert(Blocks.CANPASS1, Blocks.CHAMAN);
                }
                if (battleBlock == Blocks.BATTLE2) {
                    getMainController().getMapFormCon().convert(Blocks.CANPASS2, Blocks.CHAMAN);
                }
                if (battleBlock == Blocks.BATTLE3) {
                    getMainController().getMapFormCon().convert(Blocks.CANPASS3, Blocks.CHAMAN);
                }
                getMainController().getBattleFormStage().close();
                getMainController().getMapFormStage().toFront();
            } else {
                battle.endNetworkBattle(true);
            }
        });
    }

    public void battleDraw() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(":-|");
            alert.setHeaderText(battle.getBattleName());
            alert.setContentText("\"" + battle.getBattleName() + "\" Battle Is Draw :-|");
            alert.showAndWait();
            battle.endBattleWithoutSaving();
            if (!battle.isNetworkGaming()) {
                getMainController().getBattleFormStage().close();
                getMainController().getMapFormStage().toFront();
            } else {
                battle.endNetworkBattle(true);
            }
        });
    }

    public void battleLose(boolean endGame) {
        Platform.runLater(() -> {
            if (endGame) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("GAME OVER!");
                alert.setHeaderText(battle.getBattleName());
                if (battle.isNetworkGaming()) {
                    alert.setContentText("Game Over!");
                    alert.showAndWait();
                    battle.endNetworkBattle(true);
                } else {
                    alert.setContentText("You Are Out Of \"MysticHourglass\"\nGame Over!\n\n");
                    alert.showAndWait();
                    Platform.exit();
                    System.exit(0);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Recovery !");
                alert.setHeaderText(battle.getBattleName());
                alert.setContentText(":-)\n" + getGameManager().getItemShop().findItem("MysticHourglass").toString() + "\n\n");
                alert.showAndWait();
                getMainController().getBattleFormStage().close();
                getMainController().getMapFormStage().toFront();
            }
        });
    }

    private void refreshPlayers() {
        Platform.runLater(() -> {
            Player player = getGameManager().getHumanPlayer();
            nameHuman.setText(player.getName());
            HlblMP.setText("MP : " + player.getMP() + " / " + player.getMaxMP());
            HlblHP.setText("HP : " + player.getHP() + " / " + player.getMaxHP());
            HprogHP.setProgress((double) player.getHP() / (double) player.getMaxHP());
            HprogMP.setProgress((double) player.getMP() / (double) player.getMaxMP());
            Hamu.setText("Amulet : " + (player.getBag().getAmulet() == null ? "NULL" : player.getBag().getAmulet().getName()));
            Hdeck.setText("DECK :\n" + player.getBag().getDeck().getFullSlots());
            Hgrave.setText("GRAVE :\n" + player.getBag().getGraveyard().getLimit());
            Player finalPlayer = player;
            nameHuman.setOnMouseClicked(event -> {
                refreshDetail(finalPlayer);
            });

            player = battle.getEnemyPlayer();
            nameEnemy.setText(player.getName());
            ElblMP.setText("MP : " + player.getMP() + " / " + player.getMaxMP());
            ElblHP.setText("HP : " + player.getHP() + " / " + player.getMaxHP());
            EprogHP.setProgress((double) player.getHP() / (double) player.getMaxHP());
            EprogMP.setProgress((double) player.getMP() / (double) player.getMaxMP());
            Eamu.setText("Amulet : " + (player.getBag().getAmulet() == null ? "NULL" : player.getBag().getAmulet().getName()));
            Edeck.setText("DECK :\n" + player.getBag().getDeck().getFullSlots());
            Egrave.setText("GRAVE :\n" + player.getBag().getGraveyard().getLimit());
            Player finalPlayer1 = player;
            nameEnemy.setOnMouseClicked(event -> {
                refreshDetail(finalPlayer1);
            });
        });
        Runtime.getRuntime().gc();
    }

    private void refreshSpellField() {
        // FOR HUMAN
        SpellField field = getGameManager().getHumanPlayer().getBag().getSpellField();
        for (int i = 1; i <= 3; i++) {
            SpellCard slotCard = field.getSlotCard(i);
            CardSlotPane pane = new CardSlotPane(slotCard, temp);
            pane.setRotate(humanSplF.get(i - 1).getRotate());
            humanSplF.get(i - 1).getChildren().clear();
            humanSplF.get(i - 1).getChildren().add(pane.asPane());
            pane.changeXY(0, 0);
            int finalI = i;
            pane.setOnClick(event -> {
                refreshDetail(pane.getOwner(), (slotCard != null ? slotCard.getSummaryInfo() : ""));
            });
        }

        // FOR ENEMY
        field = battle.getEnemyPlayer().getBag().getSpellField();
        for (int i = 1; i <= 3; i++) {
            SpellCard slotCard = field.getSlotCard(i);
            CardSlotPane pane = new CardSlotPane(slotCard, temp);
            pane.setRotate(enemySplF.get(i - 1).getRotate());
            enemySplF.get(i - 1).getChildren().clear();
            enemySplF.get(i - 1).getChildren().add(pane.asPane());
            pane.changeXY(0, 0);
            int finalI = i;
            pane.setOnClick(event -> {
                refreshDetail(pane.getOwner(), (slotCard != null ? slotCard.getSummaryInfo() : ""));
            });
        }
        Runtime.getRuntime().gc();
    }

    private void refreshMonsterField() {
        // FOR HUMAN
        final MonsterField field = getGameManager().getHumanPlayer().getBag().getMonsterField();
        for (int i = 1; i <= 5; i++) {
            MonsterCard slotCard = field.getSlotCard(i);
            CardSlotPane pane = new CardSlotPane(slotCard, temp);
            pane.setRotate(humanMonF.get(i - 1).getRotate());
            humanMonF.get(i - 1).getChildren().clear();
            humanMonF.get(i - 1).getChildren().add(pane.asPane());
            pane.changeXY(0, 0);
            int finalI = i;

            {
                ContextMenu contextMenu = new ContextMenu();

                MenuItem item1 = new MenuItem("Attack");

                item1.setOnAction(event -> {
                    try {
                        field.useCard(String.valueOf(finalI), "attack");
                        battle.updateAndSendToServer(false);
                    } catch (DeadPlayerException e) {

                    }
                });
                if (slotCard != null) {
                    if ((!slotCard.isSleeping()) && slotCard.isCanAttack()) {
                        item1.setDisable(false);
                    } else {
                        item1.setDisable(true);
                    }

                } else {
                    item1.setDisable(true);
                }


                MenuItem item2 = new MenuItem("Use Spell");
                item2.setOnAction(event -> {
                    try {
                        field.useCard(String.valueOf(finalI), "cast");
                        battle.updateAndSendToServer(false);
                    } catch (DeadPlayerException e) {
                    }
                });
                if (slotCard != null) {
                    if (slotCard.getMonsterType() == MonsterType.Hero || slotCard.getMonsterType() == MonsterType.SpellCaster) {
                        if (!slotCard.isUsedSpell() && !slotCard.isSleeping()) {
                            item2.setDisable(false);
                        } else {
                            item2.setDisable(true);
                        }
                    } else {
                        item2.setDisable(true);
                    }
                } else {
                    item2.setDisable(true);
                }

                contextMenu.getItems().addAll(item1, item2);

                humanMonF.get(i - 1).setOnContextMenuRequested(event -> {
                    if (slotCard != null && isMyTurn) {
                        contextMenu.show(humanMonF.get(finalI - 1), event.getScreenX(), event.getScreenY());
                    }
                });
            }
            pane.setOnClick(event -> {
                refreshDetail(pane.getOwner(), (slotCard != null ? slotCard.getSummaryInfo() : ""));
               /* if (slotCard != null && isMyTurn) {
                    //            message += "\nIs Sleeping : " + (card.isSleeping() ? Menus.RED : Menus.GREEN) + String.valueOf(card.isSleeping()).toUpperCase() + Menus.RESET + "\n";
                    //            message += "Can Attack : " + ((card.isSleeping() || !card.isCanAttack()) ? Menus.RED : Menus.GREEN) + String.valueOf((!card.isSleeping()) && card.isCanAttack()).toUpperCase() + Menus.RESET + "\n";
                    //            if (card.getMonsterType() == MonsterType.Hero || card.getMonsterType() == MonsterType.SpellCaster) {
                    //                message += "Can Cast : " + ((card.isUsedSpell() || card.isSleeping()) ? Menus.RED : Menus.GREEN) + String.valueOf(!card.isUsedSpell() && !card.isSleeping()).toUpperCase() + Menus.RESET + "\n";
                    //            }

                    if ((!slotCard.isSleeping()) && slotCard.isCanAttack()) {
                        attack.setDisable(false);
                        attack.setOnAction(event1 -> {
                            try {
                                field.useCard(String.valueOf(finalI), "attack");
                            } catch (DeadPlayerException e) {

                            }
                        });
                    } else {
                        attack.setDisable(true);
                    }

                    if (slotCard.getMonsterType() == MonsterType.Hero || slotCard.getMonsterType() == MonsterType.SpellCaster) {
                        if (!slotCard.isUsedSpell() && !slotCard.isSleeping()) {
                            dospell.setDisable(false);
                            dospell.setOnAction(event1 -> {
                                try {
                                    field.useCard(String.valueOf(finalI), "cast");
                                } catch (DeadPlayerException e) {
                                }
                            });
                        } else {
                            dospell.setDisable(true);
                        }
                    } else {
                        dospell.setDisable(true);
                    }
                } else {
                    refreshDetail(null);
                }*/
            });


        }


        // FOR ENEMY
        MonsterField field2 = battle.getEnemyPlayer().getBag().getMonsterField();
        for (int i = 1; i <= 5; i++) {
            MonsterCard slotCard = field2.getSlotCard(i);
            CardSlotPane pane = new CardSlotPane(slotCard, temp);
            pane.setRotate(enemyMonF.get(i - 1).getRotate());
            enemyMonF.get(i - 1).getChildren().clear();
            enemyMonF.get(i - 1).getChildren().add(pane.asPane());
            pane.changeXY(0, 0);
            int finalI = i;
            pane.setOnClick(event -> {
                refreshDetail(pane.getOwner(), (slotCard != null ? slotCard.getSummaryInfo() : ""));
            });
        }
        Runtime.getRuntime().gc();
    }

    private void refreshHand() {
        Hand hand = getGameManager().getHumanPlayer().getBag().getHand();
        panePool.clear();
        for (int i = 1; i <= 5; i++) {
            Card slotCard = hand.getSlotCard(i);
            CardSlotPane pane = new CardSlotPane(slotCard, temp);
            panePool.add(pane);
            pane.setRotate(handSlots.get(i - 1).getRotate());
            handSlots.get(i - 1).getChildren().clear();
            handSlots.get(i - 1).getChildren().add(pane.asPane());
            pane.changeXY(0, 0);
            int finalI = i;
            pane.setOnClick(event -> {
                refreshDetail(pane.getOwner());
                if (slotCard != null && isMyTurn) {
                    for (CardSlotPane p : panePool) {
                        p.stopAnim();
                    }
                    pane.startAnim();
                    handSlots.get(finalI - 1).toFront();
                    draw.setDisable(false);
                    draw.setOnAction(event1 -> {
                        int to = -1;
                        Object toField;

                        if (slotCard instanceof MonsterCard) {
                            to = getGameManager().getHumanPlayer().getBag().getMonsterField().findFirstEmptySlotIndex();
                            if (to == -1) {
                                to = 5;
                            }
                            toField = getGameManager().getHumanPlayer().getBag().getMonsterField();
                        } else {
                            to = getGameManager().getHumanPlayer().getBag().getSpellField().findFirstEmptySlotIndex();
                            if (to == -1) {
                                to = 3;
                            }
                            toField = getGameManager().getHumanPlayer().getBag().getSpellField();
                        }


                        String res = hand.drawCardTo(getGameManager().getHumanPlayer(), finalI, to);


                        if (res.startsWith("ERROR")) {
                            showPopup(res, true, 3500);
                            battle.setupForAnimate(null, null, null, 0, 0);
                            refreshAll();
                        } else {
                            handSlots.get(finalI - 1).toBack();
                            if (res.contains("was set")) {
                                animateCard(slotCard, hand, toField, finalI, to, null);
                                battle.setupForAnimate(slotCard, "me-hand", (toField instanceof MonsterField) ? "me-mon" : "me-spl", finalI, to);
                            } else {
                                animateCard(slotCard, hand, getGameManager().getHumanPlayer().getBag().getGraveyard(), finalI, to, null);
                                battle.setupForAnimate(slotCard, "me-hand", "me-grave", finalI, to);
                            }

                        }

                        draw.setDisable(true);


                        battle.updateAndSendToServer(true);
                    });
                } else {
                    refreshDetail(null);
                }
            });
        }


        Runtime.getRuntime().gc();
    }

    public Thread animateCard(Card card, Object from, Object to, int fromN, int toN, Runnable runnable) {
        Thread thread = new Thread(() -> {
            Runnable toRun = () -> {


                Timeline timeline = new Timeline();
                timeline.setCycleCount(1);


                if (!(to instanceof Graveyard)) {
                    Point2D point = null;
                    AnchorPane target = null;
                    double targetRotate;
                    AnchorPane toAnchor = null;

                    if (to instanceof MonsterField) {
                        if (((MonsterField) to).getPlayer() == getGameManager().getHumanPlayer()) {
                            toAnchor = humanMonF.get(toN - 1);
                        } else {
                            toAnchor = enemyMonF.get(toN - 1);
                        }
                    } else if (to instanceof SpellField) {
                        if (((SpellField) to).getPlayer() == getGameManager().getHumanPlayer()) {
                            toAnchor = humanSplF.get(toN - 1);
                        } else {
                            toAnchor = enemySplF.get(toN - 1);
                        }
                    } else if (to instanceof Hand) {
                        if (((Hand) to).getPlayer() == getGameManager().getHumanPlayer()) {
                            toAnchor = handSlots.get(toN - 1);
                        } else {
                            toAnchor = handSlots.get(toN - 1);
                        }
                    }
                    point = new Point2D(toAnchor.getLayoutX(), toAnchor.getLayoutY());
                    target = toAnchor;
                    target.setVisible(false);
                    targetRotate = target.getRotate();


                    CardSlotPane slotPane = new CardSlotPane(null, temp);
                    form.getChildren().add(slotPane.asPane());
                    Pane tempPane = slotPane.asPane();
                    tempPane.setVisible(false);


                    if (from instanceof Hand) {
                        if (((Hand) from).getPlayer() == getGameManager().getHumanPlayer()) {
                            target.setLayoutX(handSlots.get(fromN - 1).getLayoutX());
                            target.setLayoutY(handSlots.get(fromN - 1).getLayoutY());
                            target.setRotate(handSlots.get(fromN - 1).getRotate());
                        } else {
                            target.setLayoutX(nameEnemy.getParent().getLayoutX());
                            target.setLayoutY(nameEnemy.getParent().getLayoutY());
                            target.setRotate(nameEnemy.getParent().getRotate());
                        }
                    }
                    if (from instanceof Deck) {
                        target.setLayoutX(Hdeck.getParent().getLayoutX());
                        target.setLayoutY(Hdeck.getParent().getLayoutY());
                        target.setRotate(Hdeck.getParent().getRotate());
                    }
                    target.setVisible(true);

                    tempPane.setLayoutX(point.getX());
                    tempPane.setLayoutY(point.getY());
                    tempPane.setRotate(targetRotate);
                    tempPane.setVisible(true);
                    tempPane.toBack();

                    target.toFront();

                    refreshAll();
                    KeyValue k1 = new KeyValue(target.layoutXProperty(), point.getX(), Interpolator.EASE_BOTH);
                    KeyValue k2 = new KeyValue(target.layoutYProperty(), point.getY(), Interpolator.EASE_BOTH);
                    KeyValue k3 = new KeyValue(target.rotateProperty(), targetRotate, Interpolator.EASE_BOTH);
                    KeyFrame kf = new KeyFrame(Duration.millis(1000), k1, k2, k3);
                    timeline.setDelay(Duration.millis(500));
                    timeline.getKeyFrames().add(kf);
                    timeline.play();

                    timeline.setOnFinished(event2 -> {
                        tempPane.setVisible(false);
                        form.getChildren().remove(tempPane);
                        if (runnable != null) {
                            runnable.run();
                        }
                        refreshAll();
                    });


                } else {
                    Point2D point = null;
                    double targetRotate = 0;

                    CardSlotPane slotPane = new CardSlotPane(card, temp);
                    form.getChildren().add(slotPane.asPane());
                    Pane tempPane = slotPane.asPane();
                    tempPane.setVisible(false);


                    if (from instanceof Hand) {
                        if (((Hand) from).getPlayer() == getGameManager().getHumanPlayer()) {
                            slotPane.changeXY(handSlots.get(fromN - 1).getLayoutX(), handSlots.get(fromN - 1).getLayoutY());
                            slotPane.changeRotate(handSlots.get(fromN - 1).getRotate());
                            point = new Point2D(Hgrave.getParent().getLayoutX(), Hgrave.getParent().getLayoutY());
                        } else {

                            slotPane.changeXY(nameEnemy.getParent().getLayoutX(), nameEnemy.getParent().getLayoutY());
                            slotPane.changeRotate(nameEnemy.getParent().getRotate());
                            point = new Point2D(Egrave.getParent().getLayoutX(), Egrave.getParent().getLayoutY());
                        }
                    } else if (from instanceof MonsterField) {
                        if (((MonsterField) from).getPlayer() == getGameManager().getHumanPlayer()) {
                            slotPane.changeXY(humanMonF.get(fromN - 1).getLayoutX(), humanMonF.get(fromN - 1).getLayoutY());
                            slotPane.changeRotate(humanMonF.get(fromN - 1).getRotate());
                            point = new Point2D(Hgrave.getParent().getLayoutX(), Hgrave.getParent().getLayoutY());
                        } else {
                            slotPane.changeXY(enemyMonF.get(fromN - 1).getLayoutX(), enemyMonF.get(fromN - 1).getLayoutY());
                            slotPane.changeRotate(enemyMonF.get(fromN - 1).getRotate());
                            point = new Point2D(Egrave.getParent().getLayoutX(), Egrave.getParent().getLayoutY());
                        }
                    } else if (from instanceof SpellField) {
                        if (((SpellField) from).getPlayer() == getGameManager().getHumanPlayer()) {
                            slotPane.changeXY(humanSplF.get(fromN - 1).getLayoutX(), humanSplF.get(fromN - 1).getLayoutY());
                            slotPane.changeRotate(humanSplF.get(fromN - 1).getRotate());
                            point = new Point2D(Hgrave.getParent().getLayoutX(), Hgrave.getParent().getLayoutY());
                        } else {
                            slotPane.changeXY(enemySplF.get(fromN - 1).getLayoutX(), enemySplF.get(fromN - 1).getLayoutY());
                            slotPane.changeRotate(enemySplF.get(fromN - 1).getRotate());
                            point = new Point2D(Egrave.getParent().getLayoutX(), Egrave.getParent().getLayoutY());
                        }
                    }

                    tempPane.setVisible(true);
                    tempPane.toFront();

                    refreshAll();


                    KeyValue k1 = new KeyValue(tempPane.layoutXProperty(), point.getX(), Interpolator.EASE_BOTH);
                    KeyValue k2 = new KeyValue(tempPane.layoutYProperty(), point.getY(), Interpolator.EASE_BOTH);
                    KeyValue k3 = new KeyValue(tempPane.rotateProperty(), targetRotate, Interpolator.EASE_BOTH);
                    KeyFrame kf = new KeyFrame(Duration.millis(1000), k1, k2, k3);
                    timeline.setDelay(Duration.millis(500));
                    timeline.getKeyFrames().add(kf);
                    timeline.play();

                    timeline.setOnFinished(event2 -> {
                        tempPane.setVisible(false);
                        form.getChildren().remove(tempPane);
                        if (runnable != null) {
                            runnable.run();
                        }
                        refreshAll();
                    });

                }

            };

            if (Platform.isFxApplicationThread()) {
                toRun.run();
            } else {
                Platform.runLater(toRun);
            }

        });
        thread.start();
        return thread;

    }

    private void refreshItems() {
        items.setItems(getGameManager().getHumanPlayer().getInventory().getListOfItems());
        items.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return (new ListCell() {
                    @Override
                    protected void updateItem(Object o, boolean empty) {
                        super.updateItem(o, empty);
                        String item = (String) o;
                        if (item != null) {
                            Button button = new Button();
                            if (item.contains(";")) {
                                button.setStyle("-fx-background-color: " + item.substring(0, item.indexOf(";")));
                            } else {
                                button.setStyle("-fx-background-color: #47ff4d");
                            }
                            button.setText(item.substring(item.indexOf(";") + 1));
                            button.setFont(new Font("Consolas", 13));
                            button.setOnAction(event -> {
                                if (item.contains("\"")) {
                                    String key = item.substring(item.indexOf("\"") + 1, item.lastIndexOf("\""));
                                    refreshDetail(null, getGameManager().getItemShop().findItem(key).toString());
                                    if (isMyTurn) {
                                        useItem.setDisable(false);
                                        useItem.setOnAction(event1 -> {
                                            if (battle.isNetworkGaming()) {
                                                showPopup("Use Item Error : You Cannot Use Items In Multiplayer Game", true, 2500);
                                            } else {
                                                BagElement<Item> entry = getGameManager().getHumanPlayer().getInventory().findBagElement(getGameManager().getHumanPlayer().getInventory().getItems(), key);
                                                if (entry.getNumber() > 0) {
                                                    String res = entry.getObject().runMagicCommand(getGameManager().getHumanPlayer());
                                                    useItem.setDisable(true);
                                                    if (!res.contains("ERROR")) {
                                                        getGameManager().getHumanPlayer().getInventory().removeItem(key, 1);
                                                    } else {
                                                        showPopup("Use Item : " + key + "\n" + res, true, 2500);
                                                    }
                                                } else {
                                                    showPopup("Use Item : " + key + "\nNumber Is Zero!", true, 2500);
                                                }
                                                refreshAll();
                                            }
                                        });
                                    }
                                }
                            });
                            setGraphic(button);
                        }
                        return;
                    }
                });
            }
        });

    }

    private void refreshDetail(CanBeTarget target, String sarRiz) {
        Platform.runLater(() -> {
            if (target == null) {
                if (!sarRiz.equals("")) {
                    detail.setText(sarRiz.trim());
                }
            } else if (target instanceof MonsterCard || target instanceof SpellCard) {
                detail.setText(getGameManager().getCardShop().findCard(target.getName()).toString() + "\n" + sarRiz);
            } else if (target instanceof Player) {
                String res = "";
                res += "Player : " + target.getName() + "\n";
                res += ("Current MP: " + ((Player) target).getMP() + " - MaxMP: " + ((Player) target).getMaxMP() + "\n");
                res += ("Amulet: " + (((Player) target).getBag().getAmulet() == null ? "NULL" : ((Player) target).getBag().getAmulet().getName()) + "\n");
                res += "Player Health: " + ((Player) target).getHP() + "\n\n";
                detail.setText(res);
            }
        });

        refreshButtons();

        Runtime.getRuntime().gc();
    }

    private void refreshDetail(CanBeTarget target) {
        refreshDetail(target, "");
    }

    private void refreshButtons() {
        attack.setDisable(true);
        dospell.setDisable(true);
        attack.setVisible(false);
        dospell.setVisible(false);
        draw.setDisable(true);
        useItem.setDisable(true);
        if (battle.getNowPlayer() == battle.getHumanPlayer()) {
            endTurn.setDisable(false);
            endTurn.setText("END TURN");
        } else {
            endTurn.setDisable(true);
            endTurn.setText("ENEMY TURN");
        }
        turnNumber.setText("TURN NUMBER : " + battle.getTurnNumber());

        for (CardSlotPane p : panePool) {
            p.stopAnim();
        }
    }

    public void refreshAll() {
        Platform.runLater(() -> {
            refreshDetail(null);
            refreshItems();
            refreshPlayers();
            refreshHand();
            refreshMonsterField();
            refreshSpellField();
            Runtime.getRuntime().gc();
        });
    }

    public void showPopup(String msg, boolean error, long delay) {
        Platform.runLater(() -> {
            Tooltip tooltip = new Tooltip(msg);
            if (error) {
                tooltip.setStyle("-fx-text-fill: #ff2100; -fx-font-size: 16;");
            } else {
                tooltip.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
            }
            tooltip.show(getMainController().getBattleFormStage()
                    , getMainController().getBattleFormStage().getX() + nameHuman.getParent().getLayoutX(),
                    getMainController().getBattleFormStage().getY() + nameHuman.getParent().getLayoutY());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(tooltip::hide);
                }
            }, delay);
        });
    }

    public void log(String toLog) {
        Platform.runLater(() -> {

            messages.appendText(toLog + "\n");
            messages.selectPositionCaret(messages.getLength());
            messages.deselect();
            System.out.println(toLog);
            refreshAll();

        });
    }

    public void notMyTurn() {
        isMyTurn = false;
        refreshAll();
    }

    public void myTurn() {
        isMyTurn = true;
        refreshAll();
    }

    public Battle getBattle() {
        return battle;
    }


}
