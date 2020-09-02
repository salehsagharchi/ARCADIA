package Game.BattleManaging;

import Game.Assets.Amulet;
import Game.Assets.Card;
import Game.Constants.Initializer_Statics;
import Game.Enums.BattleState;
import Game.Enums.PlayerType;
import Game.Exceptions.DeadPlayerException;
import Game.Exceptions.DeckIsEmptyException;
import Game.Network.NetworkManager;
import Game.PlayerClasses.*;
import Game.States.StateManager;
import MenuControllers.GeneralClasses.Menus;
import Views.FXControllers.BattleFormCon;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Battle implements Serializable {
    private String battleName;
    private String battleKey;
    private int gilReward;
    private BattleState state;
    private Player enemyPlayer;
    private Player humanPlayer;
    private Player nowPlayer;
    private Player starter;
    private int turnNumber;
    private transient BattleFormCon formCon;
    private Player netPlayer1, netPlayer2;

    // For Network Game
    private boolean networkGaming;
    private String networkBattleKey = "";
    private String playerNetKey = ""; // use in clients battle
    private boolean humanIsNetP1; // use in clients battle
    private String message;
    private transient NetworkManager networkManager; // use in clients battle
    private Player mainHumanPlayer; // use in clients battle

    //For Network Game And Animating
    private Card toAnimate;
    private String fromObjectAnim;
    private String toObjectAnim;
    private int fromNAnim;
    private int toNAnim;

    public Battle(String battleKey, Player humanPlayer) {
        createNewBattle(battleKey, humanPlayer);
    }

    // this function run in server for main battle
    public Battle(String battleKey, Player netPlayer1, Player netPlayer2) {
        if (battleKey.trim().equals("network")) {
            createNewNetworkBattle(netPlayer1, netPlayer2);
        }
    }

    private void createNewBattle(String battleKey, Player humanPlayer) {
        networkGaming = false;
        this.battleKey = battleKey;
        this.humanPlayer = humanPlayer;
        turnNumber = 0;

        enemyPlayer = new Player(PlayerType.Computer, "", 0);

        String out = "";
        try {
            out = IOUtils.toString(
                    Battle.class.getResourceAsStream("../Constants/Constants.txt"),
                    "UTF-8"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> output = new ArrayList<>();
        String[] lines = out.split("\n");
        boolean find = false;
        for (String line : lines) {
            if (line.trim().equals("")) {
                continue;
            }
            line = line.trim();

            if (line.equals("}") && find) {
                break;
            }
            if (find) {
                output.add(line);
            }
            if (line.equalsIgnoreCase(battleKey + "{")) {
                find = true;
            }
        }

        for (String entry : output) {
            if (entry.startsWith("name:")) {
                setBattleName(entry.split(":")[1].trim());
            }
            if (entry.startsWith("vsname:")) {
                enemyPlayer.setName(entry.split(":")[1].trim());
            }
            if (entry.startsWith("gil:")) {
                setGilReward(Integer.parseInt(entry.split(":")[1].trim()));
            }
            if (entry.startsWith("c:")) {
                String tmp = entry.split(":")[1].trim();
                int num = Integer.parseInt(tmp.split("@")[1]);
                for (int i = 0; i < num; i++) {
                    enemyPlayer.getBag().getDeck().addCard(Initializer_Statics.getCloneOfCard(tmp.split("@")[0]));
                }
            }
            if (entry.startsWith("amulet:")) {
                enemyPlayer.getBag().setAmulet(Initializer_Statics.getCloneOfAmulet(entry.split(":")[1]));
            }
        }


    }

    public void nextPlayer() {
        if (turnNumber == 0) {
            turnNumber = 1;
            int empties;

            ArrayList<String> names = new ArrayList<>();


            empties = humanPlayer.getBag().getHand().emptySlots();


            for (int i = 1; i <= empties; i++) {

                try {
                    String name = (humanPlayer.getBag().getDeck().getOneCardAndDelete());
                    names.add("\"" + name + "\"");
                    humanPlayer.getBag().getHand().addCard(Initializer_Statics.getCloneOfCard(name));

                    {
                        CountDownLatch latch = new CountDownLatch(1);
                        formCon.animateCard(humanPlayer.getBag().getHand().getSlotCard(i), humanPlayer.getBag().getDeck(), humanPlayer.getBag().getHand(), 0, i, new Runnable() {
                            @Override
                            public void run() {
                                latch.countDown();
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (DeckIsEmptyException e) {
                    handleEmptyDeck();
                }
            }
            //humanPlayer.getBag().getHand().addCard(Initializer_Statics.getCloneOfCard("FirstAidKit"));

            empties = enemyPlayer.getBag().getHand().emptySlots();
            for (int i = 1; i <= empties; i++) {
                try {
                    enemyPlayer.getBag().getHand().addCard(Initializer_Statics.getCloneOfCard(enemyPlayer.getBag().getDeck().getOneCardAndDelete()));
                } catch (DeckIsEmptyException e) {
                    handleEmptyDeck();
                }
            }
            formCon.log(Menus.YELLOW + "\nYou Drew " + names.toString() + Menus.RESET);


            formCon.log("\n" + showPlayers());


        } else {
            if (nowPlayer == starter) {
                nowPlayer = starter.getOpposite();
            } else {
                nowPlayer = starter;
            }
            turnNumber += 1;

            nowPlayer.addMaxMP(1);
            nowPlayer.equalizeMP();

            formCon.log(Menus.YELLOW + "\nTurn " + turnNumber + " Started!");
            formCon.log("\"" + nowPlayer.getName() + "\"'s Turn!\n" + Menus.RESET);


            if (nowPlayer.getBag().getHand().emptySlots() >= 1) {
                try {
                    String name = nowPlayer.getBag().getDeck().getOneCardAndDelete();
                    int to = nowPlayer.getBag().getHand().findFirstEmptySlotIndex();
                    nowPlayer.getBag().getHand().addCardToSlot(Initializer_Statics.getCloneOfCard(name), to);
                    if (nowPlayer == humanPlayer) {
                        formCon.animateCard(humanPlayer.getBag().getHand().getSlotCard(to), humanPlayer.getBag().getDeck(), humanPlayer.getBag().getHand(), 0, to, () -> {
                            formCon.log(Menus.YELLOW + "\"" + name + "\" drew!" + Menus.RESET);
                        });
                    }
                } catch (DeckIsEmptyException e) {
                    handleEmptyDeck();
                }
            }

            if (nowPlayer == humanPlayer) {
                formCon.log(Menus.YELLOW + "Current MP: " + nowPlayer.getMP() + " - MaxMP: " + nowPlayer.getMaxMP() + "\n" + Menus.RESET);
            }

            nowPlayer.turnStarted();
        }

        if (nowPlayer == humanPlayer) {
            formCon.myTurn();
        } else {
            formCon.notMyTurn();
            enemyPlayer.decide();
        }
    }

    // this function run in server for main battle
    private void createNewNetworkBattle(Player netPlayer1, Player netPlayer2) {

        networkGaming = true;
        this.netPlayer1 = netPlayer1;
        this.netPlayer2 = netPlayer2;
        turnNumber = 0;
        setBattleName("Multi Player Battle:)");
        setGilReward(0);
        System.out.println("BattleMade");

        setState(BattleState.Playing);

        netPlayer1.setOpposite(netPlayer2);
        netPlayer2.setOpposite(netPlayer1);

        netPlayer1.getBag().getDeck().Shuffle();
        netPlayer2.getBag().getDeck().Shuffle();

        netPlayer1.setBattle(this);
        netPlayer2.setBattle(this);

        Random random = new Random();
        if (random.nextBoolean()) {
            starter = netPlayer1;
        } else {
            starter = netPlayer2;
        }
        starter = netPlayer1;

        starter.setMaxMP(starter.getDefaultMaxMP());
        starter.equalizeMP();

        starter.getOpposite().setMaxMP(0);
        starter.getOpposite().equalizeMP();

        netPlayer1.setMaxHP(netPlayer1.getDefaultMaxHP());
        netPlayer1.setHP(netPlayer1.getMaxHP());

        netPlayer2.setMaxHP(netPlayer2.getDefaultMaxHP());
        netPlayer2.setHP(netPlayer2.getMaxHP());
    }

    public void startBattle(BattleFormCon formCon) {
        this.formCon = formCon;

        if (networkGaming) {
            mainHumanPlayer = formCon.getGameManager().getHumanPlayer();
            formCon.getGameManager().setHumanPlayer(humanPlayer);
        }

        formCon.log("Battle Name : \"" + battleName + "\"\n");
        formCon.log("Battle Against \"" + enemyPlayer.getName() + "\" Started!");


        if (!networkGaming) {
            Random random = new Random();
            if (random.nextBoolean()) {
                starter = enemyPlayer;
            } else {
                starter = humanPlayer;
            }
        }


        nowPlayer = starter;

        formCon.log("\"" + nowPlayer.getName() + "\"" + " Starts The Battle!\n" + Menus.RESET);

        if (!networkGaming) {
            setState(BattleState.Playing);

            enemyPlayer.setOpposite(humanPlayer);
            humanPlayer.setOpposite(enemyPlayer);

            humanPlayer.getBag().getDeck().Shuffle();
            enemyPlayer.getBag().getDeck().Shuffle();

            humanPlayer.setBattle(this);
            enemyPlayer.setBattle(this);


            starter.setMaxMP(starter.getDefaultMaxMP());
            starter.equalizeMP();

            starter.getOpposite().setMaxMP(0);
            starter.getOpposite().equalizeMP();

            humanPlayer.setMaxHP(humanPlayer.getDefaultMaxHP());
            humanPlayer.setHP(humanPlayer.getMaxHP());

            enemyPlayer.setMaxHP(enemyPlayer.getDefaultMaxHP());
            enemyPlayer.setHP(enemyPlayer.getMaxHP());
        }

        if (!networkGaming) {
            humanPlayer.executeAmulets();
            enemyPlayer.executeAmulets();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    nextPlayer();

                }
            }).start();


        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    nextPlayerNetwork();
                }
            }).start();

        }

    }

    public void settingSocketGaming(BattleFormCon formCon) {
        syncPlayers(false);
        this.networkManager = formCon.getGameManager().getNetworkManager();
    }

    public void nextPlayerNetwork() {
        if (turnNumber == 0) {
            turnNumber = 1;
            int empties;
            ArrayList<String> names = new ArrayList<>();
            empties = humanPlayer.getBag().getHand().emptySlots();
            for (int i = 1; i <= empties; i++) {
                try {
                    String name = (humanPlayer.getBag().getDeck().getOneCardAndDelete());
                    names.add("\"" + name + "\"");
                    humanPlayer.getBag().getHand().addCard(Initializer_Statics.getCloneOfCard(name));
                    {
                        CountDownLatch latch = new CountDownLatch(1);
                        formCon.animateCard(humanPlayer.getBag().getHand().getSlotCard(i), humanPlayer.getBag().getDeck(), humanPlayer.getBag().getHand(), 0, i, new Runnable() {
                            @Override
                            public void run() {
                                latch.countDown();
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (DeckIsEmptyException e) {
                    handleEmptyDeck();
                }
            }
            formCon.log(Menus.YELLOW + "\nYou Drew " + names.toString() + Menus.RESET);
            formCon.log("\n" + showPlayers());
            if (!isHumanIsNetP1()) {
                try {

                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.setMessage("update1:humanplayer&turnnumber");
            if (nowPlayer == humanPlayer) {
                this.setMessage(this.getMessage() + "&meturn");
            }
            networkManager.sendBattleToServer(this);
            networkManager.logToPlayers("Log to Players Test :)", networkBattleKey);

        } else {
            if (nowPlayer == humanPlayer) {
                nowPlayer = enemyPlayer;
            } else {
                nowPlayer = humanPlayer;
            }
            turnNumber += 1;
            nowPlayer.addMaxMP(1); // todo change mp
            nowPlayer.equalizeMP();

            if (nowPlayer == humanPlayer) {
                formCon.log(Menus.YELLOW + "Current MP: " + nowPlayer.getMP() + " - MaxMP: " + nowPlayer.getMaxMP() + "\n" + Menus.RESET);
            }


            this.setMessage("update2:humanplayer&enemyplayer&turnnumber&");
            if (nowPlayer == humanPlayer) {
                this.setMessage(this.getMessage() + "meturn&");
            } else {
                this.setMessage(this.getMessage() + "enemyturn&");
            }
            this.setMessage(this.getMessage() + "turnstart");
            networkManager.sendBattleToServer(this);

        }


    }

    public void syncAllWith(Battle battle, String[] updates) {
        System.out.println("sync : " + Arrays.toString(updates));
        boolean turnstart = false;
        boolean animate = false;
        boolean finish = false;
        for (String toUpdate : updates) {
            if (toUpdate.equals("netpl1")) {
                netPlayer1 = battle.getNetPlayer1();
            } else if (toUpdate.equals("netpl2")) {
                netPlayer2 = battle.getNetPlayer2();
            } else if (toUpdate.equals("turnnumber")) {
                turnNumber = battle.getTurnNumber();
            } else if (toUpdate.equals("nowplayer")) {
                if (battle.getNetPlayer1() == battle.nowPlayer) {
                    nowPlayer = netPlayer1;
                } else if (battle.getNetPlayer2() == battle.nowPlayer) {
                    nowPlayer = netPlayer2;
                }
            } else if (toUpdate.equals("turnstart")) {
                turnstart = true;
            } else if (toUpdate.equals("anim")) {
                toAnimate = battle.getToAnimate();
                fromNAnim = battle.getFromNAnim();
                toNAnim = battle.getToNAnim();
                fromObjectAnim = battle.getFromObjectAnim();
                toObjectAnim = battle.getToObjectAnim();
                animate = true;
            } else if (toUpdate.equals("battleend")) {
                finish = true;
            }
        }

        syncPlayers(animate);


        if (turnstart) {
            formCon.log("\nTurn " + turnNumber + " Started!");
            formCon.log("\"" + nowPlayer.getName() + "\"'s Turn!\n");
            if (nowPlayer == humanPlayer) {
                System.out.println("ME TURN");
                if (nowPlayer.getBag().getHand().emptySlots() >= 1) {
                    try {
                        String name = nowPlayer.getBag().getDeck().getOneCardAndDelete();
                        int to = nowPlayer.getBag().getHand().findFirstEmptySlotIndex();
                        nowPlayer.getBag().getHand().addCardToSlot(Initializer_Statics.getCloneOfCard(name), to);
                        if (nowPlayer == humanPlayer) {
                            formCon.animateCard(humanPlayer.getBag().getHand().getSlotCard(to), humanPlayer.getBag().getDeck(), humanPlayer.getBag().getHand(), 0, to, () -> {
                                formCon.log(Menus.YELLOW + "\"" + name + "\" drew!" + Menus.RESET);
                            });
                        }
                    } catch (DeckIsEmptyException e) {
                        handleEmptyDeck();
                    }
                }
                nowPlayer.turnStarted();
                this.setMessage("update1:humanplayer&enemyplayer");
                networkManager.sendBattleToServer(this);
            }
        }


/*        System.out.println("    In battle HUMAN CARDS");
        for (int i = 1; i <= 5; i++) {
            Card card = humanPlayer.getBag().getHand().getSlotCard(i);
            if (card != null) {
                System.out.print(card.getName() + " ");
            } else
                System.out.print("null ");

        }
        System.out.println();
        System.out.println("    In battle ENEMY CARDS");
        for (int i = 1; i <= 5; i++) {
            Card card = enemyPlayer.getBag().getHand().getSlotCard(i);
            if (card != null) {
                System.out.print(card.getName() + " ");
            } else
                System.out.print("null ");

        }
        System.out.println();*/

        Runtime.getRuntime().gc();

        if (finish) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("");
                alert.setContentText("Battle Finished (Or Closed :|) :))");
                alert.showAndWait();
                endNetworkBattle(false);
            });
        }
    }

    public void syncPlayers(boolean animate) {
        String nobat = "";
        if (nowPlayer == humanPlayer) {
            nobat = "human";
        } else if (nowPlayer == enemyPlayer) {
            nobat = "enemy";
        }
        if (humanIsNetP1) {
            humanPlayer = netPlayer1;
            enemyPlayer = netPlayer2;
        } else {
            humanPlayer = netPlayer2;
            enemyPlayer = netPlayer1;
        }
        if (nobat.equals("human")) {
            nowPlayer = humanPlayer;
        } else if (nobat.equals("enemy")) {
            nowPlayer = enemyPlayer;
        }

        netPlayer1.setOpposite(netPlayer2);
        netPlayer2.setOpposite(netPlayer1);
        netPlayer1.setBattle(this);
        netPlayer2.setBattle(this);

        if (animate) {
            runAnimations();
        }

        if (formCon != null) {
            formCon.getGameManager().setHumanPlayer(humanPlayer);
            if (nowPlayer == humanPlayer) {
                formCon.myTurn();
            } else {
                formCon.notMyTurn();
            }
        }
    }

    public void setupForAnimate(Card card, String from, String to, int fromN, int toN) {
        toAnimate = card;
        fromNAnim = fromN;
        toNAnim = toN;
        fromObjectAnim = from;
        toObjectAnim = to;
    }

    private void runAnimations() {
        Object from = null;
        String fromObjStr = fromObjectAnim.split("-")[1];
        String toObjStr = toObjectAnim.split("-")[1];
        Player target = enemyPlayer;
        if (fromObjectAnim.split("-")[0].equals("enemy")) {
            target = humanPlayer;
        }
        Player target2 = enemyPlayer;
        if (toObjectAnim.split("-")[0].equals("enemy")) {
            target2 = humanPlayer;
        }

        if (fromObjStr.equals("hand")) {
            from = target.getBag().getHand();
        } else if (fromObjStr.equals("spl")) {
            from = target.getBag().getSpellField();
        } else if (fromObjStr.equals("mon")) {
            from = target.getBag().getMonsterField();
        } else if (fromObjStr.equals("grave")) {
            from = target.getBag().getGraveyard();
        }
        Object to = null;
        if (toObjStr.equals("hand")) {
            to = target2.getBag().getHand();
        } else if (toObjStr.equals("spl")) {
            to = target2.getBag().getSpellField();
        } else if (toObjStr.equals("mon")) {
            to = target2.getBag().getMonsterField();
        } else if (toObjStr.equals("grave")) {
            to = target2.getBag().getGraveyard();
        }
        formCon.animateCard(toAnimate, from, to, fromNAnim, toNAnim, null);
    }

    public void endNetworkBattle(boolean send) {
        if (networkManager.isPlaying()) {
            this.setMessage("update1:humanplayer&enemyplayer&battleend");
            if (send) {
                networkManager.sendBattleToServer(this);
            }
            restoreState();
            formCon.getGameManager().setHumanPlayer(mainHumanPlayer);
            networkManager.setPlaying(false);
            networkManager.setEnemyClientStr("");
            Platform.runLater(() -> {
                formCon.getMainController().getBattleFormStage().close();
                formCon.getMainController().getNetworkStage().toFront();
            });
        }
    }

    public void IAmDead(Player player) throws DeadPlayerException {

        logToAll(Menus.PURPLE + "\nPlayer Dead : " + player.getName() + "\n" + Menus.RESET, true);

        if (player == humanPlayer) {
            formCon.log(Menus.PURPLE + "You Lose \"" + battleName + "\" Battle :-( !\n\n" + Menus.RESET);

            if (humanPlayer.getInventory().findElementNumber(humanPlayer.getInventory().getItems(), "MysticHourglass") > 0 && !networkGaming) {

                formCon.log(Menus.GREEN + ":-)\n" +
                        humanPlayer.getInventory().findElement(humanPlayer.getInventory().getItems(), "MysticHourglass").toString() + "\n\n" + Menus.RESET);

                restoreState();

                createNewBattle(battleKey, humanPlayer);

                setState(BattleState.NotStarted);

                StateManager.restoreState(
                        "state.ser", humanPlayer.getBag().getDeck(), humanPlayer.getInventory(), formCon.getGameManager(), true);


                formCon.battleLose(false);
            } else {
                formCon.battleLose(true);
            }

            throw new DeadPlayerException("player was dead");
        } else {
            formCon.log(Menus.GREEN + "You Won \"" + battleName + "\" Battle Successfully !\n\n" + Menus.RESET);

            restoreState();

            humanPlayer.addGil(gilReward);

            if (gilReward > 0) {
                formCon.log(Menus.GREEN + "You Gil Reward is : \"" + gilReward + "\"\n\n" + Menus.RESET);
            }

            if (!networkGaming) {
                createNewBattle(battleKey, humanPlayer);
                setState(BattleState.Ended);
            }

            formCon.battleWon();

            throw new DeadPlayerException("player was dead");
        }
    }

    private void handleEmptyDeck() {
        if (humanPlayer.getBag().isEmptyAll() && enemyPlayer.getBag().isEmptyAll()) {
            if (humanPlayer.getHP() > enemyPlayer.getHP()) {
                try {
                    IAmDead(enemyPlayer);
                } catch (DeadPlayerException e) {

                }
            } else if (humanPlayer.getHP() < enemyPlayer.getHP()) {
                try {
                    IAmDead(humanPlayer);
                } catch (DeadPlayerException e) {

                }
            } else {
                formCon.battleDraw();
            }
        }
    }

    public void updateAndSendToServer(boolean anim) {
        if (networkGaming) {
            this.setMessage("update1:humanplayer&enemyplayer");
            if (anim && toAnimate != null) {
                this.setMessage(this.getMessage() + "&anim");
            }
            networkManager.sendBattleToServer(this);
        }
    }

    public void restoreState() {
        Deck tempDek = humanPlayer.getBag().getDeck();


        // Empty Grave
        {
            Graveyard tempGrave = humanPlayer.getBag().getGraveyard();
            for (int i = 1; i <= tempGrave.getLimit(); i++) {
                if (tempGrave.getSlotCard(i) != null) {
                    tempDek.addCard(Initializer_Statics.getCloneOfCard(tempGrave.getSlotCard(i).getName()));
                }
            }
            tempGrave.emptyGraveyard();
        }

        // Empty MonsterField
        {
            MonsterField tempMonF = humanPlayer.getBag().getMonsterField();
            for (int i = 1; i <= tempMonF.getLimit(); i++) {
                if (tempMonF.getSlotCard(i) != null) {
                    tempDek.addCard(Initializer_Statics.getCloneOfCard(tempMonF.getSlotCard(i).getName()));
                    try {
                        tempMonF.addCardToSlot(null, i, false);
                    } catch (DeadPlayerException e) {

                    }
                }
            }
        }

        // Empty SpellField
        {
            SpellField tempSplF = humanPlayer.getBag().getSpellField();
            for (int i = 1; i <= tempSplF.getLimit(); i++) {
                if (tempSplF.getSlotCard(i) != null) {
                    tempDek.addCard(Initializer_Statics.getCloneOfCard(tempSplF.getSlotCard(i).getName()));
                    try {
                        tempSplF.addCardToSlot(false, null, i, false);
                    } catch (DeadPlayerException e) {

                    }
                }
            }
        }

        // Empty Hand
        {
            Hand tempHand = humanPlayer.getBag().getHand();
            for (int i = 1; i <= tempHand.getLimit(); i++) {
                if (tempHand.getSlotCard(i) != null) {
                    tempDek.addCard(Initializer_Statics.getCloneOfCard(tempHand.getSlotCard(i).getName()));
                    tempHand.addCardToSlot(null, i);
                }
            }
        }

        tempDek.Shuffle();

        humanPlayer.setOpposite(null);
        humanPlayer.setBattle(null);

        enemyPlayer.setOpposite(null);
        enemyPlayer.setBattle(null);

        humanPlayer.setMaxMP(starter.getDefaultMaxMP());
        humanPlayer.equalizeMP();

        humanPlayer.setMaxHP(humanPlayer.getDefaultMaxHP());
        humanPlayer.setHP(humanPlayer.getMaxHP());


    }

    public void endBattleWithoutSaving() {

        restoreState();
        if (!networkGaming) {
            createNewBattle(battleKey, humanPlayer);
            setState(BattleState.NotStarted);
        }
    }

    public String showPlayers() {
        Amulet amulet = starter.getBag().getAmulet();
        String res = "\n";
        res += "Player 1 : " + Menus.CYAN + starter.getName() + "\n";
        res += ("Current MP: " + starter.getMP() + " - MaxMP: " + starter.getMaxMP() + "\n");
        res += ("Amulet: " + (amulet == null ? "NULL" : amulet.getName()) + "\n");
        res += "Player Health: " + starter.getHP() + Menus.RESET + "\n\n";
        res += "Player 2 : " + Menus.CYAN + starter.getOpposite().getName() + "\n";
        res += ("Current MP: " + starter.getOpposite().getMP() + " - MaxMP: " + starter.getOpposite().getMaxMP() + "\n");
        amulet = starter.getOpposite().getBag().getAmulet();
        res += ("Amulet: " + (amulet == null ? "NULL" : amulet.getName()) + "\n");
        res += "Player Health: " + starter.getOpposite().getHP() + Menus.RESET + "\n";
        return res;
    }

    public BattleState getState() {
        return state;
    }

    public void setState(BattleState state) {
        this.state = state;
    }

    public Player getEnemyPlayer() {
        return enemyPlayer;
    }

    public String getBattleName() {
        return battleName;
    }

    public void setBattleName(String battleName) {
        this.battleName = battleName;
    }

    public int getGilReward() {
        return gilReward;
    }

    public void setGilReward(int gilReward) {
        if (gilReward > 0) {
            this.gilReward = gilReward;
        }
    }

    public BattleFormCon getFormCon() {
        return formCon;
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public Player getNowPlayer() {
        return nowPlayer;
    }

    public void setNowPlayer(Player nowPlayer) {
        this.nowPlayer = nowPlayer;
    }

    public Player getStarter() {
        return starter;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public String getBattleKey() {
        return battleKey;
    }

    public String getNetworkBattleKey() {
        return networkBattleKey;
    }

    public void setNetworkBattleKey(String networkBattleKey) {
        this.networkBattleKey = networkBattleKey;
    }

    public String getPlayerNetKey() {
        return playerNetKey;
    }

    public void setPlayerNetKey(String playerNetKey) {
        this.playerNetKey = playerNetKey;
    }

    public boolean isHumanIsNetP1() {
        return humanIsNetP1;
    }

    public void setHumanIsNetP1(boolean humanIsNetP1) {
        this.humanIsNetP1 = humanIsNetP1;
    }

    public boolean isNetworkGaming() {
        return networkGaming;
    }

    public void setNetworkGaming(boolean networkGaming) {
        this.networkGaming = networkGaming;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public void logToAll(String message) {
        logToAll(message, false);
    }

    public void logToAll(String message, boolean msgbox) {
        if (networkGaming) {
            networkManager.logToPlayers(message, networkBattleKey, msgbox);
        } else {
            formCon.log(message);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Player getNetPlayer1() {
        return netPlayer1;
    }

    public void setNetPlayer1(Player netPlayer1) {
        this.netPlayer1 = netPlayer1;
    }

    public Player getNetPlayer2() {
        return netPlayer2;
    }

    public void setNetPlayer2(Player netPlayer2) {
        this.netPlayer2 = netPlayer2;
    }

    public Player getMainHumanPlayer() {
        return mainHumanPlayer;
    }

    public Card getToAnimate() {
        return toAnimate;
    }

    public void setToAnimate(Card toAnimate) {
        this.toAnimate = toAnimate;
    }

    public String getFromObjectAnim() {
        return fromObjectAnim;
    }

    public void setFromObjectAnim(String fromObjectAnim) {
        this.fromObjectAnim = fromObjectAnim;
    }

    public String getToObjectAnim() {
        return toObjectAnim;
    }

    public void setToObjectAnim(String toObjectAnim) {
        this.toObjectAnim = toObjectAnim;
    }

    public int getFromNAnim() {
        return fromNAnim;
    }

    public void setFromNAnim(int fromNAnim) {
        this.fromNAnim = fromNAnim;
    }

    public int getToNAnim() {
        return toNAnim;
    }

    public void setToNAnim(int toNAnim) {
        this.toNAnim = toNAnim;
    }

    public void setHumanPlayer(Player humanPlayer) {
        this.humanPlayer = humanPlayer;
    }

}
