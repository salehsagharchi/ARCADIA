package Game.Network;

import Game.BattleManaging.Battle;
import Game.GameManager;
import Game.PlayerClasses.Deck;
import Views.FXControllers.NetworkCon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * @author SALEHSAGHARCHI
 * Date: 2018-07-16
 * Time: 3:48 AM
 */
public class NetworkManager extends NetworkPlayer {
    private GameManager gameManager;
    private boolean isServer;

    // i am client
    private Client mainClient;

    // i am server
    private ServerSocket serverSocket;
    private ArrayList<Client> clients;
    private Thread getClient;
    private String serverName;
    private String allChat;
    private NetworkCon networkCon;
    private Map<String, NetworkGame> networkGameMap;

    // server or client1
    private boolean isPlaying;
    private String enemyClientStr;


    public NetworkManager(GameManager gameManager) {
        this.gameManager = gameManager;
        clients = new ArrayList<>();
        serverName = "";
    }

    public void resetAllThings() {
        if (isPlaying) {
            if (getGameManager().getMainController().getBattleFormStage().isShowing()) {
                Platform.runLater(() -> getGameManager().getMainController().getBattleFormStage().close());
            }
        }
        try {
            getClient.interrupt();
        } catch (Exception ignored) {
        }
        try {
            mainClient.getInputThread().interrupt();
        } catch (Exception ignored) {
        }
        clearClientsAndDeleteThem();
        try {
            serverSocket.close();
        } catch (Exception ignored) {
        }
        try {
            mainClient.getSocket().close();
        } catch (Exception ignored) {
        }
        serverSocket = null;
        mainClient = null;
        isServer = false;
        allChat = "";
        isPlaying = false;
        enemyClientStr = "";
        networkGameMap = new LinkedHashMap<>();
    }

    // client or server
    public void checkIsEnemyAvailable() {
        if (isPlaying && !enemyClientStr.equals("")) {
            if (isServer) {
                Client found;
                found = findClient(enemyClientStr);
                if (found == null) {
                    enemyIsNotAvailable();
                }
            } else {
                if (mainClient != null) {
                    mainClient.getPrintStream().println("checkavailable:" + enemyClientStr);
                }
            }
        }
    }

    //client or server
    public void enemyIsNotAvailable() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Game Error");
            alert.setContentText("Your Enemy Is Not Available Now :)");
            alert.showAndWait();
        });
    }

    // i am client
    private void handleServerDisconnect() {
        resetAllThings();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Network Error");
            alert.setContentText("Connection is Not Available Now :)");
            alert.showAndWait();
        });
    }

    // i am client
    public boolean connectToServer(String ip, int port) {
        try {
            resetAllThings();
            isServer = false;

            mainClient = new Client(new Socket(ip, port), false);

            mainClientThreadInit();


            return true;

        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Network Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
            return false;
        }
    }

    // i am client
    private void mainClientThreadInit() {
        mainClient.setInputThread(new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted() && mainClient != null) {
                    String input = null;
                    Object input_object;
                    try {
                        input_object = mainClient.getObjectInputStream().readUnshared();
                    } catch (Exception ex) {
                        input_object = "#disconnect#";
                    }
                    if (input_object == null) {
                        input_object = "#disconnect#";
                    }
                    if (input_object instanceof String) {
                        input = (String) input_object;
                    } else if (input_object instanceof Battle) {
                        manageReceivedBattleObject(input_object);
                    }
//                    System.out.println(input_object.toString());
                    if (input != null) {
                        manageReceivedMessagesInClient(input.trim());
                    }
                }
            }
        }));
        mainClient.getInputThread().setDaemon(true);
        mainClient.getInputThread().start();
    }

    // i am server
    public boolean createServer(int port) {
        try {
            resetAllThings();
            isServer = true;

            serverSocket = new ServerSocket(port);

            manageNewClientsConnected();

            networkGameMap = new LinkedHashMap<>();

            return true;

        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Network Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
            return false;
        }
    }

    // i am server
    private void clearClientsAndDeleteThem() {
        for (Client c : clients) {
            if (c.getInputThread() != null) {
                c.getInputThread().interrupt();
            }
            try {
                c.getSocket().close();
            } catch (IOException e) {
            }
        }
        clients.clear();
    }

    // client or server
    private void sendMessageFromAnyToAny(Object packet, String from, String receiver) {
        if (packet instanceof String) {
            String toSend = (String) packet;
            if (isServer && from.toLowerCase().startsWith("[server]")) {
                if (receiver.toLowerCase().startsWith("[server]")) {
                    manageReceivedMessagesInServer(toSend, null);
                } else {
                    findClient(receiver).getPrintStream().println("from:" + getServerString() + "#" + toSend);
                }
            } else if (mainClient != null && from.toLowerCase().startsWith("[client]")) {
                if (receiver.toLowerCase().startsWith("[server]")) {
                    mainClient.getPrintStream().println(toSend);
                } else {
                    mainClient.getPrintStream().println("sendto:" + receiver + "#" + toSend);
                }
            }
        } else {
            if (receiver.toLowerCase().startsWith("[server]")) {
                if (isServer && from.toLowerCase().startsWith("[server]")) {
                    manageReceivedMessagesInServer(packet, null);
                } else {
                    try {
                        mainClient.getObjectOutputStream().flush();
                        mainClient.getObjectOutputStream().writeUnshared(packet);
                        mainClient.getObjectOutputStream().reset();
                        mainClient.getObjectOutputStream().flush();
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR");
                            alert.setHeaderText("Server Error");
                            alert.setContentText("Error Send Object to Server :)");
                            alert.showAndWait();
                        });
                        e.printStackTrace();
                    }
                }
            } else {
                if (isServer && from.toLowerCase().startsWith("[server]")) {
                    try {
                        findClient(receiver).getObjectOutputStream().flush();
                        findClient(receiver).getObjectOutputStream().writeUnshared(packet);
                        findClient(receiver).getObjectOutputStream().reset();
                        findClient(receiver).getObjectOutputStream().flush();
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR");
                            alert.setHeaderText("Server Error");
                            alert.setContentText("Error Send Object to Client :)");
                            alert.showAndWait();
                        });
                        e.printStackTrace();
                    }
                } else {
                    try {
                        throw new Exception("Error Send Object to Client");
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR");
                            alert.setHeaderText("Server Error");
                            alert.setContentText("Error Send Object to Client :)");
                            alert.showAndWait();
                        });
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // i am client
    private void manageReceivedMessagesInClient(String msg) {
        manageReceivedMessagesInClient(msg, "");
    }

    // client or server
    private boolean messagesRelatedToBattle(String msg, String sender, String me) {
        if (msg.equals("canyouplay")) {
            if (isPlaying) {
                sendMessageFromAnyToAny("noplay", me, sender);
            } else {
                Platform.runLater(() -> {
                    // request play with Me
                    if (confirmGame(sender)) {
                        sendMessageFromAnyToAny("yesplay", me, sender);
                        isPlaying = true;
                        enemyClientStr = sender;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Started");
                        alert.setHeaderText("Game Started");
                        alert.setContentText("Your New Game Is Started With\n" + sender);
                        alert.showAndWait();
                        sendMessageFromAnyToAny("makebattle$" + me + "$" + sender, me, "[server]");
                    } else {
                        sendMessageFromAnyToAny("noplay", me, sender);
                    }
                });
            }
            return true;
        } else if (msg.equals("noplay")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Refused");
                alert.setContentText("Player\n" + sender + "\nhas refused to play with you :)");
                alert.showAndWait();
            });
            return true;
        } else if (msg.equals("yesplay")) {
            isPlaying = true;
            enemyClientStr = sender;
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Started");
                alert.setHeaderText("Game Started");
                alert.setContentText("Your New Game Is Started With\n" + sender);
                alert.showAndWait();
            });
            return true;
        } else if (msg.equals("sendMeDeck")) {
            Object deck = getGameManager().getHumanPlayer().getBag().getDeck();
            ((Deck) deck).setNetworkBattleKey(standardClientStr(me) + "$" + standardClientStr(enemyClientStr));
            sendMessageFromAnyToAny(deck, me, "[server]");
            return true;
        } else if (msg.startsWith("log:")) {
            String toLog = msg.substring(msg.indexOf(":") + 1);
            if (getGameManager().getMainController().getBattleFormStage().isShowing()) {
                getGameManager().getMainController().getBattleFormCon().log(toLog);
            }
            return true;
        } else if (msg.startsWith("mslog:")) {
            String toLog = msg.substring(msg.indexOf(":") + 1);
            if (getGameManager().getMainController().getBattleFormStage().isShowing()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("");
                    alert.setContentText(toLog);
                    alert.showAndWait();
                });
            }
            return true;
        }
        return false;
    }

    // i am client
    private void manageReceivedMessagesInClient(String msg, String senderStr) {
        if (msg.startsWith("from:")) {
            String from = msg.split("#")[0].trim();
            from = from.substring(from.indexOf(":") + 1);
            manageReceivedMessagesInClient(msg.split("#")[1], from);
        } else if (msg.equals("#disconnect#")) {
            System.out.println("Disconnect message");
            handleServerDisconnect();
        } else if (msg.startsWith("clients=")) {
            ObservableList<String> resultList = FXCollections.observableArrayList();
            msg = msg.substring(msg.indexOf("=") + 1);
            for (String str : msg.split("\\$")) {
                if (!str.trim().equals("")) {
                    resultList.add(str.trim());
                }
            }
            if (resultList.size() == 0) {
                resultList.add("RED;\t" + "--> Not Any Item Now!");
            }
            getNetworkCon().refreshList(resultList);
        } else if (msg.startsWith("chats=")) {
            getNetworkCon().allChatChange(msg.substring(msg.indexOf("=") + 1).replace("$", "\n"));
        } else if (msg.startsWith("error:")) {
            String finalMsg = msg;
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("");
                alert.setContentText(finalMsg.substring(finalMsg.indexOf(":") + 1));
                alert.showAndWait();
            });
        } else if (msg.equals("enemynotavailable")) {
            enemyIsNotAvailable();
        } else if (!senderStr.equals("")) {
            if (messagesRelatedToBattle(msg, senderStr, mainClient.toString())) {

            }// else if ...
        }
    }

    // i am server
    private void manageReceivedMessagesInServer(Object packet, Client client) {
        if (packet instanceof String) {
            String str = (String) packet;

            if (str.equals("#disconnect#")) {
                for (Map.Entry<String, NetworkGame> entry : networkGameMap.entrySet()) {
                    String pl1 = standardClientStr(entry.getValue().getPlayer1().toString());
                    String pl2 = standardClientStr(entry.getValue().getPlayer2().toString());
                    if (standardClientStr(client.toString()).equals(pl1)) {
                        entry.getValue().getBattle().setMessage("sync:battleend");
                        sendMessageFromAnyToAny(entry.getValue().getBattle(), getServerString(), pl2);
                    }
                    if (standardClientStr(client.toString()).equals(pl2)) {
                        entry.getValue().getBattle().setMessage("sync:battleend");
                        sendMessageFromAnyToAny(entry.getValue().getBattle(), getServerString(), pl1);
                    }
                }
                deleteClient(client.getSocket());
            } else if (str.startsWith("setname:")) {
                client.setName(str.replace("setname:", "").trim());
            } else if (str.startsWith("msg:")) {
                allChat += "[" + client.getName().toUpperCase() + "] : " + (str.replace("msg:", "").trim()) + "\n";
            } else if (str.equals("sendMeMessages")) {
                client.getPrintStream().println("chats=" + allChat.replace("\n", "$").trim());
            } else if (str.equals("sendMeClients")) {
                StringBuilder res = new StringBuilder("clients=");
                res.append(getServerString()).append("$");
                for (Client c : clients) {
                    if (!c.equals(client)) {
                        res.append(c.toString()).append("$");
                    }
                }
                client.getPrintStream().println(res.toString().trim());
            } else if (str.startsWith("sendto:")) {
                String str1 = str.split("#")[0];
                String str2 = str.split("#")[1];
                String targetMsg = str1.substring(str1.indexOf(":") + 1).trim();

                if (targetMsg.equals(getServerString())) {
                    manageReceivedMessagesInServer(str2, client);
                } else {
                    Client found;
                    found = findClient(targetMsg);
                    if (found == null) {
                        client.getPrintStream().println("error:playerNotFound");
                    } else {
                        found.getPrintStream().println("from:" + client.toString() + "#" + str2);
                    }
                }
            } else if (str.startsWith("checkavailable:")) {
                boolean find = false;
                String to_check = str.substring(str.indexOf(":") + 1);
                if (!to_check.equals(getServerString())) {
                    Client found;
                    found = findClient(to_check);
                    if (found == null) {
                        client.getPrintStream().println("enemynotavailable");
                    }
                }
            } else if (str.startsWith("makebattle")) {
                String pl1 = standardClientStr(str.split("\\$")[1].trim());
                String pl2 = standardClientStr(str.split("\\$")[2].trim());
                String key = "";
                NetworkGame networkGame = new NetworkGame();
                NetworkPlayer player1 = null;
                NetworkPlayer player2 = null;
                if (pl1.toLowerCase().startsWith("[server]")) {
                    player1 = this;
                    pl1 = getServerString();
                } else {
                    player1 = findClient(pl1);
                    pl1 = player1.toString();
                }
                if (pl2.toLowerCase().startsWith("[server]")) {
                    player2 = this;
                    pl2 = getServerString();
                } else {
                    player2 = findClient(pl2);
                    pl2 = player2.toString();
                }
                key = standardClientStr(pl1) + "$" + standardClientStr(pl2);
                networkGame.setPlayer1(player1);
                networkGame.setPlayer2(player2);
                networkGame.makePlayersNew();
                networkGameMap.put(key, networkGame);
                sendMessageFromAnyToAny("sendMeDeck", getServerString(), pl1);
                sendMessageFromAnyToAny("sendMeDeck", getServerString(), pl2);
                System.out.println("Game Created:)");
            } else {
                if (client != null) {
                    messagesRelatedToBattle(str, client.toString(), getServerString());
                } else {
                    messagesRelatedToBattle(str, getServerString(), getServerString());
                }
            }
        } else if (packet instanceof Deck) {
            Deck deck = (Deck) packet;
            System.out.println("Deck Received : " + deck.getNetworkBattleKey());
            NetworkGame networkGame = findGameFromPlayers(deck.getNetworkBattleKey());
            String pl1 = deck.getNetworkBattleKey().split("\\$")[0];
            String pl2 = deck.getNetworkBattleKey().split("\\$")[1];
            if (playerIsFirst(pl1, networkGame)) {
                networkGame.getPlayer1().getPlayer().getBag().setDeck(deck);
            } else {
                networkGame.getPlayer2().getPlayer().getBag().setDeck(deck);
            }
            if (networkGame.getPlayer1().getPlayer().getBag().getDeck() != null
                    && networkGame.getPlayer2().getPlayer().getBag().getDeck() != null) {

                Battle battle = new Battle("network", networkGame.getPlayer1().getPlayer(), networkGame.getPlayer2().getPlayer());
                networkGame.setBattle(battle);
                battle.setNetworkBattleKey(deck.getNetworkBattleKey());
                battle.setMessage("showstage");

                battle.setPlayerNetKey(pl1);
                battle.setHumanIsNetP1(playerIsFirst(pl1, networkGame));
                sendMessageFromAnyToAny(battle, getServerString(), pl1);

                battle.setPlayerNetKey(pl2);
                battle.setHumanIsNetP1(playerIsFirst(pl2, networkGame));
                sendMessageFromAnyToAny(battle, getServerString(), pl2);
            }
        } else if (packet instanceof Battle) {
            manageReceivedBattleObject(packet);
        }
    }

    // client or server
    private void manageReceivedBattleObject(Object object) {
        if (object instanceof Battle) {
            Battle battle = (Battle) object;
            System.out.println("=> " + isServer + " battle message : " + battle.getMessage());
            if (battle.getMessage().equals("showstage")) {
                if (!getGameManager().getMainController().getBattleFormStage().isShowing()) {
                    Platform.runLater(() -> {
                        Battle battleNew = (Battle) SerializationUtils.clone((Battle) object);
                        getGameManager().getMainController().startNetworkBattle(battleNew);
                        getGameManager().getMainController().getBattleFormCon().getBattle().setMessage("");
                    });
                }
            } else if (battle.getMessage().startsWith("update1:") || battle.getMessage().startsWith("update2:")) {
                NetworkGame networkGame = findGameFromPlayers(battle.getNetworkBattleKey());
                boolean deleteGame = false;
                String pl1 = battle.getNetworkBattleKey().split("\\$")[0];
                String pl2 = battle.getNetworkBattleKey().split("\\$")[1];
                String msg = battle.getMessage();
                String[] updates = msg.substring(msg.indexOf(":") + 1).split("&");
                String toSync = "";
                for (String toUpdate : updates) {
                    if (toUpdate.equals("humanplayer")) {
                        if (battle.getHumanPlayer() == battle.getNetPlayer1()) {
                            networkGame.getBattle().setNetPlayer1(battle.getNetPlayer1());
                            toSync += "netpl1&";
                        } else {
                            networkGame.getBattle().setNetPlayer2(battle.getNetPlayer2());
                            toSync += "netpl2&";
                        }
                    } else if (toUpdate.equals("turnnumber")) {
                        networkGame.getBattle().setTurnNumber(battle.getTurnNumber());
                        toSync += "turnnumber&";
                    } else if (toUpdate.equals("enemyplayer")) {
                        if (battle.getEnemyPlayer() == battle.getNetPlayer1()) {
                            networkGame.getBattle().setNetPlayer1(battle.getNetPlayer1());
                            toSync += "netpl1&";
                        } else {
                            networkGame.getBattle().setNetPlayer2(battle.getNetPlayer2());
                            toSync += "netpl2&";
                        }
                    } else if (toUpdate.equals("meturn")) {
                        if (battle.getHumanPlayer() == battle.getNetPlayer1()) {
                            networkGame.getBattle().setNowPlayer(networkGame.getBattle().getNetPlayer1());
                            toSync += "nowplayer&";
                        } else {
                            networkGame.getBattle().setNowPlayer(networkGame.getBattle().getNetPlayer2());
                            toSync += "nowplayer&";
                        }
                    } else if (toUpdate.equals("enemyturn")) {
                        if (battle.getEnemyPlayer() == battle.getNetPlayer1()) {
                            networkGame.getBattle().setNowPlayer(networkGame.getBattle().getNetPlayer1());
                            toSync += "nowplayer&";
                        } else {
                            networkGame.getBattle().setNowPlayer(networkGame.getBattle().getNetPlayer2());
                            toSync += "nowplayer&";
                        }
                    } else if (toUpdate.equals("turnstart")) {
                        toSync += "turnstart&";
                    } else if (toUpdate.equals("anim")) {
                        networkGame.getBattle().setToAnimate(battle.getToAnimate());
                        networkGame.getBattle().setFromNAnim(battle.getFromNAnim());
                        networkGame.getBattle().setToNAnim(battle.getToNAnim());
                        networkGame.getBattle().setFromObjectAnim(battle.getFromObjectAnim());
                        networkGame.getBattle().setToObjectAnim(battle.getToObjectAnim());
                        toSync += "anim&";
                    } else if (toUpdate.equals("battleend")) {
                        deleteGame = true;
                        toSync += "battleend&";
                    }
                }
                networkGame.getBattle().setMessage("sync:" + toSync);

                if (battle.getMessage().startsWith("update1:")) {
                    if (standardClientStr(battle.getPlayerNetKey()).equals(standardClientStr(pl1))) {
                        sendMessageFromAnyToAny(networkGame.getBattle(), getServerString(), pl2);
                    } else {
                        sendMessageFromAnyToAny(networkGame.getBattle(), getServerString(), pl1);
                    }
                } else {
                    sendMessageFromAnyToAny(networkGame.getBattle(), getServerString(), pl1);
                    sendMessageFromAnyToAny(networkGame.getBattle(), getServerString(), pl2);
                }

                if (deleteGame) {
                    networkGameMap.remove(findGameCorrectKey(battle.getNetworkBattleKey()));
                }

            } else if (battle.getMessage().startsWith("sync:")) {
                String msg = battle.getMessage();
                String[] updates = msg.substring(msg.indexOf(":") + 1).split("&");
                getGameManager().getMainController().getBattleFormCon().getBattle().syncAllWith(battle, updates);
            }
        }

        Runtime.getRuntime().gc();
    }

    // i am server
    private void manageNewClientsConnected() {
        getClient = new Thread(() -> {
            while (isServer && serverSocket != null && !Thread.interrupted()) {
                try {
                    Socket temp = serverSocket.accept();
                    Client client = new Client(temp, true);
                    clients.add(client);
                    client.setInputThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!Thread.interrupted()) {
                                String input = null;
                                Object input_object;
                                try {
                                    input_object = client.getObjectInputStream().readUnshared();
                                } catch (Exception ex) {
                                    //ex.printStackTrace();
                                    input_object = "#disconnect#";
                                }
                                if (input_object == null) {
                                    input_object = "#disconnect#";
                                }
                                if (input_object instanceof String) {
                                    input = (String) input_object;
                                } else {
                                    manageReceivedMessagesInServer(input_object, client);
                                }
                                if (input != null) {
                                    manageReceivedMessagesInServer(input.trim(), client);
                                }
                            }
                        }
                    }));
                    client.getInputThread().setDaemon(true);
                    client.getInputThread().start();
                } catch (IOException e) {
                }
            }
        });
        getClient.setDaemon(true);
        getClient.start();
    }

    public void requestNewGame(String clientStr) {
        // send message from client to client : sendto:[client string]#message
        if (isPlaying) {
            return;
        }
        if (isServer) {
            if (clientStr.equals(getServerString())) {
                return;
            }
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation For Game");
        alert.setHeaderText("Play Game");
        alert.setContentText("Send Request To This User ?\n" + clientStr);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (isServer) {
                // canyouplay
                findClient(clientStr).getPrintStream().println("from:" + getServerString() + "#" + "canyouplay");
                ;
            } else if (mainClient != null) {
                // sendto : target # canyouplay
                // server sends this text --> from : clientStr # message
                mainClient.getPrintStream().println("sendto:" + clientStr + "#" + "canyouplay");
            }
        }
    }

    public void sendBattleToServer(Battle battle) {
        sendMessageFromAnyToAny(battle, battle.getPlayerNetKey(), "[server]");
    }

    public void logToPlayers(String tolog, String networkBattleKey) {
        logToPlayers(tolog, networkBattleKey, false);
    }

    public void logToPlayers(String tolog, String networkBattleKey, boolean msgbox) {
        String logMessage = "log:";
        if (msgbox) {
            logMessage = "mslog:";
        }
        String pl1 = networkBattleKey.split("\\$")[0];
        String pl2 = networkBattleKey.split("\\$")[1];
        try {
            Thread.sleep(800);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isPlaying) {
            if (isServer) {
                sendMessageFromAnyToAny(logMessage + tolog, getServerString(), pl1);
                sendMessageFromAnyToAny(logMessage + tolog, getServerString(), pl2);
            } else {
                sendMessageFromAnyToAny(logMessage + tolog, mainClient.toString(), pl1);
                sendMessageFromAnyToAny(logMessage + tolog, mainClient.toString(), pl2);
            }
        }
        try {
            Thread.sleep(800);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean playerIsFirst(String key, NetworkGame game) {
        if (standardClientStr(key).equals(standardClientStr(game.getPlayer1().toString()))) {
            return true;
        }
        return false;
    }

    public NetworkGame findGameFromPlayers(String pl1, String pl2) {
        String key1 = standardClientStr(pl1) + "$" + standardClientStr(pl2);
        String key2 = standardClientStr(pl2) + "$" + standardClientStr(pl1);
        if (networkGameMap.getOrDefault(key1, null) != null) {
            return networkGameMap.get(key1);
        } else {
            return networkGameMap.getOrDefault(key2, null);
        }
    }

    public String findGameCorrectKey(String key) {
        String key1 = standardClientStr(key.split("\\$")[0].trim()) + "$" + standardClientStr(key.split("\\$")[1].trim());
        String key2 = standardClientStr(key1.split("\\$")[1].trim()) + "$" + standardClientStr(key1.split("\\$")[0].trim());
        if (networkGameMap.getOrDefault(key1, null) != null) {
            return key1;
        } else {
            return key2;
        }
    }

    public NetworkGame findGameFromPlayers(String key) {
        String key1 = standardClientStr(key.split("\\$")[0].trim()) + "$" + standardClientStr(key.split("\\$")[1].trim());
        String key2 = standardClientStr(key1.split("\\$")[1].trim()) + "$" + standardClientStr(key1.split("\\$")[0].trim());
        if (networkGameMap.getOrDefault(key1, null) != null) {
            return networkGameMap.get(key1);
        } else {
            return networkGameMap.getOrDefault(key2, null);
        }
    }

    public Client findClient(String key) {
        key = standardClientStr(key);
        for (Client cl : clients) {
            if (standardClientStr(cl.toString()).equals(key)) {
                return cl;
            }
        }
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Server Error");
            alert.setContentText("Cannot Find Client");
            alert.showAndWait();
        });
        return null;
    }

    public String standardClientStr(String key) {
        if (key.contains("\"")) {
            key = key.trim();
            key = key.substring(0, key.indexOf("\"")) + key.substring(key.lastIndexOf("\"") + 1);
        }
        return key.trim();
    }

    public boolean confirmGame(String requestor) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation For Game");
        alert.setHeaderText("Play Game");
        alert.setContentText("Do You Want To Play With This User ?\n" + requestor);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    private void deleteClient(Socket socket) {
        Iterator iterator = clients.iterator();

        while (iterator.hasNext()) {
            Client client = (Client) iterator.next();
            if (client.getSocket().equals(socket)) {
                if (client.getInputThread() != null) {
                    client.getInputThread().interrupt();
                    //System.out.println("intrupted");
                }
                iterator.remove();
            }
        }
    }

    public void requestClientList() {
        ObservableList<String> resultList = FXCollections.observableArrayList();
        if (isServer && serverSocket != null) {
            resultList.add(getServerString());
            for (Client client : clients) {
                resultList.add(client.toString());
            }
            if (resultList.size() == 0) {
                resultList.add("RED;\t" + "--> Not Any Item Now!");
            }
            getNetworkCon().refreshList(resultList);
        } else if (mainClient != null) {
            mainClient.getPrintStream().println("sendMeClients");
        } else {
            resultList.add("RED;\t" + "--> Not Any Item Now!");
            getNetworkCon().refreshList(resultList);
        }
    }

    public void getAllChat() {
        if (isServer) {
            getNetworkCon().allChatChange(allChat);
        } else if (mainClient != null) {
            mainClient.getPrintStream().println("sendMeMessages");
        } else {
            allChat = "";
            getNetworkCon().allChatChange("");
        }
    }

    public void sendMessage(String msg) {
        if (!msg.equals("")) {
            if (isServer) {
                allChat += "[" + getServerName().toUpperCase() + "] : " + (msg) + "\n";
            } else {
                if (mainClient != null) {
                    mainClient.getPrintStream().println("msg:" + msg);
                }
            }
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean isServer() {
        return isServer;
    }

    public Client getMainClient() {
        return mainClient;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public NetworkCon getNetworkCon() {
        return networkCon;
    }

    public void setNetworkCon(NetworkCon networkCon) {
        this.networkCon = networkCon;
    }

    private String getServerString() {
        return ("[Server] : \"" + serverName + "\"" + " IP:" + serverSocket.getInetAddress() + " Port:" + serverSocket.getLocalPort()).trim();

    }

    @Override
    public String getName() {
        return getServerName();
    }

    @Override
    public String toString() {
        return getServerString();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setEnemyClientStr(String enemyClientStr) {
        this.enemyClientStr = enemyClientStr;
    }
}
