package Views.FXControllers.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class HumanAnimation implements Runnable {
    private Direction direction;
    private boolean stopped = false;
    private ImageView imageView;
    private ArrayList<Image> humanImages;

    private int start;
    private int stop;
    int i;

    @Override
    public void run() {
        i = start;
        while (true) {
            if (!stopped) {
                if (i >= stop) {
                    i = start;
                }
                imageView.setImage(humanImages.get(i));
                Thread.yield();
                try {
                    Thread.sleep(80);
                } catch (InterruptedException ignored) {
                }
                i++;
            }
            Thread.yield();
        }
    }

    public void setDirection(Direction direct) {
        direction = direct;
        if (direct == Direction.UP) {
            start = 0;
            stop = 9;
        } else if (direct == Direction.LEFT) {
            start = 9;
            stop = 18;
        } else if (direct == Direction.DOWN) {
            start = 18;
            stop = 27;
        } else if (direct == Direction.RIGHT) {
            start = 27;
            stop = 36;
        }
        i = start;
        Thread.yield();
    }

    public void pause() {
        stopped = true;
    }

    public void resume() {
        stopped = false;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setHumanImages(ArrayList<Image> humanImages) {
        this.humanImages = humanImages;
    }
}
