import java.awt.*
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

Robot robot = new Robot()
BufferedImage bi = robot.createScreenCapture(new Rectangle(445, 222, 270, 270))
ImageIO.write(bi, "png", new File("../tmp/test.png"))

//int x = 1, y = 1;
//def biSub = bi.getSubimage(x * 34, y * 34, 32, 32);
//ImageIO.write(biSub, "png", new File("../tmp/test2.png"))


int[][] map = new int[8][8]
(0..7).each { int y ->
	(0..7).each { int x ->
        BufferedImage biSub = bi.getSubimage(x * 34, y * 34, 32, 32)
		map[x][y] = biSub.hashCode()
		print map[x][y] + " "
		ImageIO.write(biSub, "png", new File("../tmp/zookeeper/${y}_${x}.png"))
	}
	println ""
}

//new File("../tmp/result.txt").text = map

