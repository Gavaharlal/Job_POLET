import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NameListFormatter {
    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("abc.tif"));
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.rotate(Math.PI / 2);
        BufferedImage result = bufferedImage;
        graphics2D.drawImage(result, null, null);
//        graphics2D.drawRenderedImage(result, null);


        ImageIO.write(result, "tif", new File("abcFormatted.tif"));
    }
}
