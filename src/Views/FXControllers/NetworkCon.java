package Views.FXControllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author SALEHSAGHARCHI
 * Date: 2018-07-16
 * Time: 2:59 AM
 */
public class NetworkCon extends FormCon {
    public Button makeServer;
    public Button connectServer;
    public ListView clients;
    public Label lblClients;
    public Button changeName;
    public TextField name;
    public TextArea allchat;
    public Button broadcast;
    public TextField msgbox;
    private boolean isServer = false;
    private Timer timer;


    public Optional<String> showTextInput(String defaultValue, String title, String headerText, String content) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    @Override
    public void start() {
        getGameManager().getNetworkManager().setNetworkCon(this);
        getGameManager().getNetworkManager().resetAllThings();


        clients.setVisible(true);
        lblClients.setVisible(true);

        makeServer.setOnAction(event -> {
            Optional<String> result1 = showTextInput("1254", "Server Port", "Server Port", "Please enter your server Port:");
            result1.ifPresent(s -> {
                try {
                    if (getGameManager().getNetworkManager().createServer(Integer.parseInt(s))) {
                        Optional<String> result3 = showTextInput("SERVER", "Your Name", "Name", "Please enter your Name:");
                        result3.ifPresent(s3 -> {
                            getGameManager().getNetworkManager().setServerName(s3);
                        });
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("Network Error");
                    alert.setContentText(s + "\n" + e.getMessage());
                    alert.showAndWait();
                }
            });
        });

        connectServer.setOnAction(event -> {
            Optional<String> result1 = showTextInput("192.168.1.171", "Server IP", "Server IP", "Please enter your server IP:");
            result1.ifPresent(s -> {
                try {
                    if (InetAddress.getByName(s).isReachable(1000)) {
                        Optional<String> result2 = showTextInput("1254", "Server Port", "Server Port", "Please enter your server Port:");
                        result2.ifPresent(s2 -> {
                            try {
                                if (getGameManager().getNetworkManager().connectToServer(s, Integer.parseInt(s2))) {
                                    Optional<String> result3 = showTextInput("Client1", "Client Name", "Name", "Please enter your Name:");
                                    result3.ifPresent(s3 -> {
                                        getGameManager().getNetworkManager().getMainClient().getPrintStream().println("setname:" + s3);
                                    });
                                }
                            } catch (NumberFormatException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("ERROR");
                                alert.setHeaderText("Server Error");
                                alert.setContentText(s2 + "\n" + e.getMessage());
                                alert.showAndWait();
                            }
                        });

                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("IP ERROR");
                        alert.setHeaderText("Server isn't reachable");
                        alert.setContentText(s + "\n isn't reachable");
                        alert.showAndWait();
                    }
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("Server Error");
                    alert.setContentText(s + "\n" + e.getMessage());
                    alert.showAndWait();
                    e.printStackTrace();
                }
            });
        });

        changeName.setOnAction(event -> {
            try {
                if (getGameManager().getNetworkManager().isServer()) {
                    getGameManager().getNetworkManager().setServerName(name.getText());
                } else {
                    getGameManager().getNetworkManager().getMainClient().getPrintStream().println("setname:" + name.getText());
                }
            } catch (Exception ignored) {

            }
        });

        broadcast.setOnAction(event -> {
            getGameManager().getNetworkManager().sendMessage(msgbox.getText().trim());
            msgbox.setText("");
        });


        allchat.setStyle("-fx-text-fill: #3070ff; -fx-font-size: 13;");
        allchat.setText("");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshAll();
            }
        }, 0, 2000);

    }


    public void onClose() {
        timer.cancel();
        getGameManager().getNetworkManager().resetAllThings();
        System.out.println("close");

    }


    public void refreshAll() {
        Platform.runLater(() -> {
            if (!getGameManager().getNetworkManager().isPlaying()) {
                getGameManager().getNetworkManager().requestClientList();
            }
            getGameManager().getNetworkManager().getAllChat();
        });
    }

    public void refreshList(ObservableList<String> items) {
        Platform.runLater(() -> {
            clients.setItems(items);
            clients.setCellFactory(new Callback<ListView, ListCell>() {
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
                                    button.setStyle("-fx-background-color: #00e6ff");
                                }
                                button.setText(item.substring(item.indexOf(";") + 1));
                                button.setOnAction(event -> {
                                    if (!item.contains("--> Not Any Item Now!")) {
                                        getGameManager().getNetworkManager().requestNewGame(item.trim());
                                    }
                                });
                                setGraphic(button);
                                button.setFont(new Font("Consolas", 8));
                            }
                            return;
                        }
                    });
                }
            });
        });
    }

    public void allChatChange(String text) {
        Platform.runLater(() -> {
            allchat.setText(text);
        });
    }
}
