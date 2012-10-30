import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Robot
import java.awt.Rectangle
import java.awt.event.InputEvent

// http://yahoo-mbga.jp/game/12002369/play
// Firefox で7ホイールスクロール必要

Map hash2type = [:]
hash2type[-2120622807] = 1 // さる
hash2type[-2119325097] = 1
hash2type[-558991865] = 2 // ぱんだ
hash2type[-560549117] = 2
hash2type[-1000746417] = 3 // きりん
hash2type[-999456457] = 3
hash2type[1205201925] = 4 // ぞう
hash2type[1204492507] = 4
hash2type[1687979181] = 5 // ライオン
hash2type[1690055517] = 5
hash2type[1296197000] = 6 // かえる
hash2type[1296975626] = 6
hash2type[-1839787472] = 7 // かば
hash2type[-1838230220] = 7

//int i = 0;
//while (true) {
//    Thread.sleep(1000)
//
//    // Capture
//    Robot robot = new Robot()
//    BufferedImage bi = robot.createScreenCapture(new Rectangle(445, 222, 270, 270))
//    ImageIO.write(bi, "png", new File("../tmp/test${i}.png"))
//
//    int[][] map = toMap(hash2type, bi)
//    doAction(map)
//
//    i++
//}

int[][] map = toMap(hash2type, ImageIO.read(new File("../tmp/test14.png")))
printMap(map)

void printMap(int[][] map) {
    for (int y in 0..7) {
        for (int x in 0..7) {
            print map[x][y]
        }
        println()
    }
}

int[][] toMap(Map hash2type, BufferedImage bi) {
    int[][] map = new int[8][8]
    for (int y in 0..7) {
        for (int x in 0..7) {
            BufferedImage biSub = bi.getSubimage(x * 34, y * 34, 32, 32)
            ImageIO.write(biSub, "png", new File("../tmp/sub_${x}_${y}.png"))
            map[x][y] = hash2type[hashCode(bi, x * 34, y * 34, 32, 32)] ?: 0
        }
    }
    return map
}

int hashCode(BufferedImage bi, int x, int y, int w, int h) {
    int[] rgbs = bi.getRGB(x, y, w, h, null, 0, w)
    int sum = 0
    rgbs.each { int rgb ->
        rgb &= 0xFFFFFF;
        int r = (rgb >> 16) & 0xFF
        int g = (rgb >> 8) & 0xFF
        int b = (rgb >> 0) & 0xFF
        if (r >= 245 && g >= 245 && b >= 192) {
            rgb = 0xFFFFFF;
        }
        sum += rgb
    }
    println "${x / 34} ${y / 34} ${sum}"
    return sum;
}

boolean isValid(x) { x >= 0 && x <= 7 }

void doAction(int[][] map) {
    for (int y in 0..7) {
        for (int x in 0..7) {
            int target = map[x][y]
            if (target == 0) continue

            for (int d in 0..3) {
                int dx = [0, 1, 0, -1][d]
                int dy = [-1, 0, 1, 0][d]
                int x2 = x + dx
                int y2 = y + dy

                if (isValid(x2) && isValid(y2) && map[x2][y2] == target) {
                    for (int d2 in 0..3) {
                        int d2x = [0, 1, 0, -1][d2]
                        int d2y = [-1, 0, 1, 0][d2]
                        int x3 = x2 + dx + d2x
                        int y3 = y2 + dy + d2y

                        if (isValid(x3) && isValid(y3) && (x2 != x3 || y2 != y3) && map[x3][y3] == target) {
                            printMap(map)
                            println "Found: ($x, $y) ($x2, $y2) ($x3, $y3)"
                            moveMouse(x2 + dx, y2 + dy, d2x, d2y)
                            return
                        }
                    }
                }
            }
        }
    }
}

int toScreenX(x) { x * 34 + 445 }
int toScreenY(y) { y * 34 + 222 }

void moveMouse(int x, int y, int dx, int dy) {
    Robot robot = new Robot()
    robot.mouseMove(toScreenX(x + dx) + 16, toScreenY(y + dy) + 16)
    Thread.sleep(50)
    robot.mousePress(InputEvent.BUTTON1_MASK)
    Thread.sleep(50)
    robot.mouseMove(toScreenX(x) + 16, toScreenY(y) + 16)
    Thread.sleep(50)
    robot.mouseRelease(InputEvent.BUTTON1_MASK)
    Thread.sleep(50)
}
