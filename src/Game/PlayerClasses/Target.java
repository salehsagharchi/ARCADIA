package Game.PlayerClasses;

import Game.BattleManaging.CanBeTarget;

import java.io.Serializable;

public class Target implements Serializable {
    private CanBeTarget object;
    private Player player;

    public Target(CanBeTarget object, Player player) {
        this.object = object;
        this.player = player;
    }

    @Override
    public String toString() {
        return object.getName();
    }

    public CanBeTarget getObject() {
        return object;
    }

    public Player getPlayer() {
        return player;
    }
}
