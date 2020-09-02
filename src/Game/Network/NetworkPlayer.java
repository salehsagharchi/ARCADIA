package Game.Network;

import Game.PlayerClasses.Player;

import java.io.Serializable;

/**
 * @author SALEHSAGHARCHI
 * Date: 2018-07-21
 * Time: 1:55 AM
 */
public abstract class NetworkPlayer implements Serializable {
    private Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract String getName();
}
