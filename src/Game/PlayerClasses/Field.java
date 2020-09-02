package Game.PlayerClasses;


import Game.Assets.Card;

public interface Field {
    int getLimit();

    Card getSlotCard(int index);
}
