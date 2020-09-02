package Game.Network;

import Game.BattleManaging.Battle;
import Game.Enums.PlayerType;
import Game.PlayerClasses.Player;

import java.io.Serializable;

/**
 * @author SALEHSAGHARCHI
 * Time: 1:53 AM
 */
public class NetworkGame implements Serializable {
    private NetworkPlayer player1;
    private NetworkPlayer player2;
    private Battle battle;


    public NetworkGame() {

    }

    public void makePlayersNew() {
        player1.setPlayer(new Player(PlayerType.Human, player1.getName(), 0));
        player2.setPlayer(new Player(PlayerType.Human, player2.getName(), 0));
        player1.getPlayer().getBag().setDeck(null);
        player2.getPlayer().getBag().setDeck(null);
        player1.getPlayer().setOpposite(player2.getPlayer());
        player1.getPlayer().setOpposite(player1.getPlayer());
    }

    public NetworkPlayer getPlayer1() {
        return player1;
    }

    public void setPlayer1(NetworkPlayer player1) {
        this.player1 = player1;
    }

    public NetworkPlayer getPlayer2() {
        return player2;
    }

    public void setPlayer2(NetworkPlayer player2) {
        this.player2 = player2;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }
}
