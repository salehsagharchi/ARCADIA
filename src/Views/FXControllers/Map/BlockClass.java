package Views.FXControllers.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BlockClass {
    private Blocks blocks;
    private Image image;
    private ImageView imageView;

    public BlockClass(Image image, Blocks blocks) {
        this.blocks = blocks;
        this.image = image;
    }

    public Blocks getBlocks() {
        return blocks;
    }

    public void setBlocks(Blocks blocks) {
        this.blocks = blocks;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
