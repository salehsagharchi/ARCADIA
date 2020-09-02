package Views.FXControllers;

import Game.Assets.Card;
import Game.Assets.MonsterCard;
import Game.Assets.SpellCard;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class CardSlotPane extends Pane {
    private Card owner;
    private Label hplbl;
    private Label aplbl;
    private Label sleeplbl;
    private Pane pane;
    private Timeline timeline;


    public CardSlotPane(Card card, Pane temp) {
        if (card == null) {
            this.owner = card;
            Pane pane = new Pane();
            this.pane = pane;
            String color = "#828282";
            String style = "-fx-background-color : " + color + "; -fx-background-radius: 20";
            pane.setStyle(style);
            pane.setPrefWidth(temp.getWidth());
            pane.setPrefHeight(temp.getHeight());

            for (Node node :
                    temp.getChildren()) {
                if (node instanceof Label) {
                    Label tempLBL = (Label) node;
                    Label l = new Label("");

                    l.setTextFill(tempLBL.getTextFill());


                    l.setRotate(node.getRotate());
                    l.setWrapText(tempLBL.isWrapText());

                    l.setEffect(node.getEffect());
                    l.setTextAlignment(tempLBL.getTextAlignment());
                    l.setContentDisplay(tempLBL.getContentDisplay());
                    l.setAlignment(tempLBL.getAlignment());

                    l.setFont(tempLBL.getFont());

                    l.setPrefHeight(tempLBL.getPrefHeight());
                    l.setPrefWidth(tempLBL.getPrefWidth());

                    l.setLayoutX(node.getLayoutX());
                    l.setLayoutY(node.getLayoutY());


                    if (tempLBL.getText().equals("NAME")) {
                        l.setText("EMPTY SLOT");
                        l.setTextFill(Color.WHITE);
                        pane.getChildren().add(l);
                    }

                }
            }
        } else {
            this.owner = card;
            Pane pane = new Pane();
            this.pane = pane;

            String color = "";
            if (card instanceof SpellCard) {
                color = "#dbffef";
            } else {
                MonsterCard mcard = (MonsterCard) card;

                switch (mcard.getTribe()) {
                    case Atlantian:
                        color = "#008912";
                        break;
                    case Elven:
                        color = "#377599";
                        break;
                    case DragonBreed:
                        color = "#e02fed";
                        break;
                    case Demonic:
                        color = "#844862";
                        break;
                    case Vampiric:
                        color = "#756634";
                        break;
                    case Goblin:
                        color = "#a84b41";
                        break;
                    case Ogre:
                        color = "#ccc82a";
                        break;
                }
            }

            String style = "-fx-background-color : " + color + "; -fx-background-radius: 20";
            pane.setStyle(style);
            DropShadow e = (DropShadow) temp.getEffect();
            DropShadow effect = new DropShadow(e.getRadius(), Color.valueOf(color));
            effect.setWidth(e.getWidth());
            effect.setHeight(e.getHeight());
            effect.setSpread(e.getSpread());
            pane.setEffect(effect);
            pane.setPrefWidth(temp.getWidth());
            pane.setPrefHeight(temp.getHeight());

            for (Node node :
                    temp.getChildren()) {
                if (node instanceof Label) {
                    Label tempLBL = (Label) node;
                    Label l = new Label("");

                    l.setTextFill(tempLBL.getTextFill());

                    if (tempLBL.getText().equals("DEFENSIVE")) {
                        if (card instanceof MonsterCard) {
                            if (((MonsterCard) card).isDefensive()) {
                                l.setText("DEFENSIVE");
                            }
                        } else {
                            l.setTextFill(Color.valueOf("#75a7ff"));
                            l.setText(((SpellCard) card).getSpellType().toString().toUpperCase());
                        }
                    }
                    if (tempLBL.getText().startsWith("HP")) {
                        if (card instanceof MonsterCard) {
                            l.setText("HP:" + ((MonsterCard) card).getHP());
                            this.hplbl = l;
                        }
                    }
                    if (tempLBL.getText().startsWith("AP")) {
                        if (card instanceof MonsterCard) {
                            l.setText("AP:" + ((MonsterCard) card).getAP());
                            this.aplbl = l;
                        }
                    }
                    if (tempLBL.getText().equals("M")) {
                        l.setText(String.valueOf(card.getCostMP()));
                    }

                    if (tempLBL.getText().equals("NAME")) {
                        l.setText(card.getName());
                    }

                    if (tempLBL.getText().startsWith("Zzz")) {
                        if (card instanceof MonsterCard) {
                            if (((MonsterCard) card).isSleeping()) {
                                l.setText("Zzz");
                            }
                            this.sleeplbl = l;
                        }
                    }


                    l.setRotate(node.getRotate());
                    l.setWrapText(tempLBL.isWrapText());

                    l.setEffect(node.getEffect());
                    l.setTextAlignment(tempLBL.getTextAlignment());
                    l.setContentDisplay(tempLBL.getContentDisplay());
                    l.setAlignment(tempLBL.getAlignment());

                    l.setFont(tempLBL.getFont());

                    l.setPrefHeight(tempLBL.getPrefHeight());
                    l.setPrefWidth(tempLBL.getPrefWidth());

                    l.setLayoutX(node.getLayoutX());
                    l.setLayoutY(node.getLayoutY());


                    pane.getChildren().add(l);
                }
            }
            refresh();
        }


    }

    public void startAnim() {
        Platform.runLater(() -> {
            timeline = new Timeline();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.setAutoReverse(true);
            ((DropShadow) pane.getEffect()).setSpread(0.4);
            KeyValue kv = new KeyValue(((DropShadow) pane.getEffect()).spreadProperty(), 1);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(400), kv));
            timeline.play();
        });
    }

    public void stopAnim() {
        try {
            timeline.stop();
            ((DropShadow) pane.getEffect()).setSpread(0.4);
        } catch (Exception e) {

        }
    }

    public Pane asPane() {
        return pane;
    }

    public void changeRotate(double val) {
        pane.setRotate(val);
    }


    public void changeXY(double x, double y) {
        pane.setLayoutX(x);
        pane.setLayoutY(y);
    }

    public void refresh() {
        Platform.runLater(() -> {
            if (owner instanceof MonsterCard) {
                MonsterCard card = (MonsterCard) owner;

                hplbl.setText("HP:" + card.getHP());
                aplbl.setText("AP:" + card.getAP());
                if (card.isSleeping()) {
                    sleeplbl.setText("Zzz");
                } else {
                    sleeplbl.setText("");
                }
                if (card.getAP() < card.getDefaultAP()) {
                    aplbl.setTextFill(Color.valueOf("#ff1616"));
                } else if (card.getAP() == card.getDefaultAP()) {
                    aplbl.setTextFill(Color.WHITE);
                } else if (card.getAP() > card.getDefaultAP()) {
                    aplbl.setTextFill(Color.valueOf("#2dff42"));
                }

                if (card.getHP() < card.getDefaultHP()) {
                    hplbl.setTextFill(Color.valueOf("#ff1616"));
                } else if (card.getHP() == card.getDefaultHP()) {
                    hplbl.setTextFill(Color.WHITE);
                } else if (card.getHP() > card.getDefaultHP()) {
                    hplbl.setTextFill(Color.valueOf("#2dff42"));
                }

            }
        });
    }

    public double getX() {
        return pane.getLayoutX();
    }

    public double getY() {
        return pane.getLayoutY();
    }

    public void setOnClick(EventHandler<MouseEvent> eventHandler) {
        pane.setOnMouseClicked(eventHandler);
    }

    public Card getOwner() {
        return owner;
    }

    public void setOwner(Card owner) {
        this.owner = owner;
    }

    public Label getHplbl() {
        return hplbl;
    }

    public void setHplbl(Label hplbl) {
        this.hplbl = hplbl;
    }

    public Label getAplbl() {
        return aplbl;
    }

    public void setAplbl(Label aplbl) {
        this.aplbl = aplbl;
    }

    public Label getSleeplbl() {
        return sleeplbl;
    }

    public void setSleeplbl(Label sleeplbl) {
        this.sleeplbl = sleeplbl;
    }

    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }
}
