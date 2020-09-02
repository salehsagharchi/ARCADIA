package Game.Assets;

import Game.BattleManaging.CanBeTarget;
import Game.Exceptions.DeadPlayerException;

import java.io.Serializable;

public interface DoAction extends Serializable {
    void run(CanBeTarget target) throws DeadPlayerException;
}
