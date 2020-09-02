package Views.FXControllers;

import Views.FXControllers.Map.BlockClass;
import Views.FXControllers.Map.Blocks;
import Views.FXControllers.Map.Direction;
import Views.FXControllers.Map.HumanAnimation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.ArrayList;

public class MapFormCon extends FormCon {
    public GridPane grid;
    public Button exit;
    public AnchorPane pane;
    public Button editInven;

    private double width;
    private double height;

    private ArrayList<BlockClass> blocks;
    private Integer[] map;
    private BlockClass[][] mapBlocks;

    private ArrayList<Image> humanImages;
    private ImageView humanIV;
    private Thread animThread;
    private HumanAnimation humanAnimation;
    private Timeline timeline;
    private boolean running;
    private int pointerX;
    private int pointerY;

    private ArrayList<String> converts;

    @Override
    public void start() {
        Runtime.getRuntime().gc();

        blocks = new ArrayList<>();
        humanImages = new ArrayList<>();

        width = grid.getWidth() / 16 + 0.01;
        height = grid.getHeight() / 16 + 0.01;


        if (converts == null) {
            converts = new ArrayList<>();
        }

        initImagesBlocks();


        if (pointerX == 0 && pointerY == 0) {
            pointerX = 3;
            pointerY = 12;
        }

        if (humanIV == null) {
            humanIV = new ImageView(humanImages.get(0));
            humanIV.setFitWidth(width * 1.3);
            humanIV.setFitHeight(height * 1.3);
            humanIV.setFocusTraversable(false);
            pane.getChildren().add(humanIV);
        }

        timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);


        drawHuman(false);

        initThreads();
        animThread.start();
        humanAnimation.pause();
        running = false;


        exit.setOnAction(event -> getMainController().getMapFormStage().close());

        editInven.setOnAction(event -> {
            if (getMainController().getShopStage().isShowing()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("OPEN EDIT");
                alert.setHeaderText("ERROR");
                alert.setContentText("You Cannot Edit Inventory When Shop Form Is Open!");
                alert.showAndWait();
            } else if (getMainController().getBattleFormStage().isShowing()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("OPEN EDIT");
                alert.setHeaderText("ERROR");
                alert.setContentText("You Cannot Edit Inventory When Battle Form Is Open!");
                alert.showAndWait();
            } else {
                getMainController().startEditForm();
                Platform.runLater(() -> humanIV.requestFocus());
            }
        });

        humanIV.setOnKeyPressed(event -> {
            handleHuman(event.getCode());
            Platform.runLater(() -> humanIV.requestFocus());
        });

        humanIV.requestFocus();

        Runtime.getRuntime().gc();
    }

    private void initThreads() {
        humanAnimation = new HumanAnimation();
        humanAnimation.setHumanImages(humanImages);
        humanAnimation.setImageView(humanIV);
        humanAnimation.setDirection(Direction.UP);
        animThread = new Thread(humanAnimation);
    }

    private void initImagesBlocks() {
        for (int i = 1; i <= 36; i++) {
            String num = String.valueOf(i);
            if (num.length() == 1) {
                num = "0" + num;
            }
            String path = getClass().getResource("../../resources/mostafa/mostafa_" + num + ".png").toExternalForm();
            humanImages.add(new Image(path));
        }


        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/aab.png").toExternalForm()), Blocks.AAB));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/battle1.png").toExternalForm()), Blocks.BATTLE1));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/battle2.png").toExternalForm()), Blocks.BATTLE2));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/battle3.png").toExternalForm()), Blocks.BATTLE3));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/canpass1.png").toExternalForm()), Blocks.CANPASS1));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/canpass2.png").toExternalForm()), Blocks.CANPASS2));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/chaman.png").toExternalForm()), Blocks.CHAMAN));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/lucifer.png").toExternalForm()), Blocks.LUCIFER));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/mount.png").toExternalForm()), Blocks.MOUNT));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/shop.png").toExternalForm()), Blocks.SHOP));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/tower.png").toExternalForm()), Blocks.TOWER));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/wall.png").toExternalForm()), Blocks.WALL));
        blocks.add(new BlockClass(new Image(getClass().getResource("../../resources/blocks/canpass3.png").toExternalForm()), Blocks.CANPASS3));

        map = new Integer[]
                {
                        10, 10, 10, 10, 10, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
                        10, 6, 6, 6, 10, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
                        10, 6, 7, 6, 12, 6, 6, 6, 6, 6, 6, 6, 6, 3, 6, 6,
                        10, 6, 6, 6, 10, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
                        10, 10, 10, 10, 10, 6, 6, 6, 6, 9, 6, 6, 6, 6, 6, 6,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 11, 5, 11, 11,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6,
                        8, 8, 8, 8, 6, 6, 6, 0, 6, 9, 6, 6, 6, 6, 6, 6,
                        8, 8, 8, 6, 6, 6, 6, 0, 6, 6, 6, 6, 2, 6, 6, 6,
                        8, 8, 8, 6, 1, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6,
                        8, 8, 8, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 6, 6,
                        8, 8, 8, 8, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 6, 6,
                };

        mapBlocks = new BlockClass[16][16];

        for (int i = 0; i < map.length; i++) {
            int col = i % 16;
            int row = i / 16;
            mapBlocks[col][row] = new BlockClass(blocks.get(map[i]).getImage(), blocks.get(map[i]).getBlocks());
            ImageView view = new ImageView(mapBlocks[col][row].getImage());
            view.setFitWidth(width);
            view.setFitHeight(height);
            mapBlocks[col][row].setImageView(view);
            grid.add(mapBlocks[col][row].getImageView(), col, row);
        }

        for (String con : converts) {
            convert(Blocks.valueOf(con.split("#")[0].trim()), Blocks.valueOf(con.split("#")[1].trim()), false);
        }
    }

    public void convert(Blocks from, Blocks to) {
        convert(from, to, true);
    }

    public void convert(Blocks from, Blocks to, boolean save) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (mapBlocks[i][j].getBlocks() == from) {
                    mapBlocks[i][j].setBlocks(to);
                    for (BlockClass entry : blocks) {
                        if (entry.getBlocks() == to) {
                            mapBlocks[i][j].getImageView().setImage(entry.getImage());
                        }
                    }
                }
            }
        }
        if (save) {
            converts.add(from.toString().trim() + "#" + to.toString().trim());
        }
    }

    private void drawHuman(boolean animate) {
        if (!animate) {
            humanIV.setX(pointerX * width + grid.getLayoutX() - 6);
            humanIV.setY(pointerY * height + grid.getLayoutY() - 12);
            return;
        }
        KeyValue kv = new KeyValue(humanIV.xProperty(), pointerX * width + grid.getLayoutX() - 6);
        KeyValue kv2 = new KeyValue(humanIV.yProperty(), pointerY * height + grid.getLayoutY() - 12);
        KeyFrame kf = new KeyFrame(Duration.millis(1000), kv, kv2);

        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(kf);
        timeline.stop();
        timeline.play();
        running = true;
        humanAnimation.resume();
        Thread.yield();

        timeline.setOnFinished(event -> {
            running = false;
            humanAnimation.pause();
            Blocks now = (mapBlocks[pointerX][pointerY].getBlocks());
            if (now == Blocks.SHOP) {
                if (getMainController().getEditFormStage().isShowing()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("OPEN SHOP");
                    alert.setHeaderText("ERROR");
                    alert.setContentText("You Cannot Enter Shop When Edit Form Is Open!");
                    alert.showAndWait();
                } else {
                    getMainController().startShop();
                }
            } else if (now.toString().toUpperCase().startsWith("BATTLE") || now == Blocks.LUCIFER) {
                if (getMainController().getEditFormStage().isShowing()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("OPEN BATTLE");
                    alert.setHeaderText("ERROR");
                    alert.setContentText("You Cannot Enter Battle When Edit Form Is Open!");
                    alert.showAndWait();
                } else {
                    getMainController().startBattle(now);
                }
            }
        });

        Runtime.getRuntime().gc();
    }

    private void handleHuman(KeyCode code) {
        if (running) {
            return;
        }
        if (getMainController().getShopStage().isShowing() || getMainController().getEditFormStage().isShowing() || getMainController().getBattleFormStage().isShowing()) {
            return;
        }
        int tempX = pointerX;
        int tempY = pointerY;
        if (code == KeyCode.UP) {
            tempY--;
            humanAnimation.setDirection(Direction.UP);
        } else if (code == KeyCode.DOWN) {
            tempY++;
            humanAnimation.setDirection(Direction.DOWN);
        } else if (code == KeyCode.RIGHT) {
            tempX++;
            humanAnimation.setDirection(Direction.RIGHT);
        } else if (code == KeyCode.LEFT) {
            tempX--;
            humanAnimation.setDirection(Direction.LEFT);
        } else if (code == KeyCode.BACK_SPACE) {
            convert(Blocks.CANPASS1, Blocks.CHAMAN);
            return;
        } else {
            return;
        }
        Thread.yield();

        if (tempY < 0 || tempX < 0 || tempX >= 16 || tempY >= 16) {
            return;
        }

        Blocks next = (mapBlocks[tempX][tempY]).getBlocks();
        if (next == Blocks.CHAMAN || next == Blocks.SHOP || next.toString().startsWith("BATTLE") || next == Blocks.LUCIFER) {
            pointerX = tempX;
            pointerY = tempY;
            drawHuman(true);
        }

    }
}
