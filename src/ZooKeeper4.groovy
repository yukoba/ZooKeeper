import groovy.transform.CompileStatic

import java.awt.Rectangle
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.image.BufferedImage

// http://yahoo-mbga.jp/game/12002369/play
// Firefox で7ホイールスクロール必要

@CompileStatic
class ZooKeeper4 {
    static final Map hash2type = [:]
    static final Robot robot = new Robot()
    static int imgNo = 0
    static BufferedImage bi
    static final int COUNTER_ANIMAL = -1

    static void main(String[] args) {
        initHashType()

        while (true) {
            Thread.sleep(50)

            // Capture
            bi = robot.createScreenCapture(new Rectangle(445, 222, 270, 270))
//            ImageIO.write(bi, "png", new File("tmp/test${imgNo++}.png"))

            int[][] map = toMap(hash2type, bi)
            doAction(map)
        }
    }

    static void initHashType() {
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
        hash2type[COUNTER_ANIMAL] = 8 // ぐるぐる回ってるやつ
    }

    static void printMap(int[][] map) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                print map[x][y]
            }
            println()
        }
    }

    static int[][] toMap(Map hash2type, BufferedImage bi) {
        int[][] map = new int[8][8]
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
//            BufferedImage biSub = bi.getSubimage(x * 34, y * 34, 32, 32)
//            ImageIO.write(biSub, "png", new File("tmp/sub_${x}_${y}.png"))
                map[x][y] = (hash2type[hashCode(bi, x * 34, y * 34, 32, 32)] ?: 0) as int
            }
        }
        return map
    }

    static int hashCode(BufferedImage bi, int x, int y, int w, int h) {
        int[] rgbs = bi.getRGB(x, y, w, h, null, 0, w)

        if ((rgbs[w * 4 + 4] & 0xFFFFFF) == 0x00D87F) {
            return COUNTER_ANIMAL
        }

        int sum = 0
        for (int rgb in rgbs) {
            rgb &= 0xFFFFFF
            int r = (rgb >> 16) & 0xFF
            int g = (rgb >> 8) & 0xFF
            int b = (rgb >> 0) & 0xFF
            if (r >= 245 && g >= 245 && b >= 192) {
                rgb = 0xFFFFFF
            }
            sum += rgb
        }
//    println "${x / 34} ${y / 34} ${sum}"
        return sum
    }

    static boolean isValid(int x) { x >= 0 && x <= 7 }

    static void doAction(int[][] map) {
        boolean isFound = false
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int target = map[x][y]
                if (target == 0) continue

                if (target == 8) {
                    clickCounterAnimal(x, y)
                }

                // 端を移動してそろうパターン
                for (int d = 0; d < 4; d++) {
                    int dx = [0, 1, 0, -1][d]
                    int dy = [-1, 0, 1, 0][d]
                    int x2 = x + dx
                    int y2 = y + dy

                    if (isValid(x2) && isValid(y2) && map[x2][y2] == target) {
                        for (int d2 = 0; d2 < 4; d2++) {
                            int d2x = [0, 1, 0, -1][d2]
                            int d2y = [-1, 0, 1, 0][d2]
                            int x3 = x2 + dx + d2x
                            int y3 = y2 + dy + d2y

                            if ((x2 != x3 || y2 != y3) && isValid(x3) && isValid(y3) && map[x3][y3] == target) {
//                            printMap(map)
//                            println "Found: ($x, $y) ($x2, $y2) ($x3, $y3)"
                                moveMouse(x2 + dx, y2 + dy, d2x, d2y)
                                isFound = true
                                // return
                            }
                        }
                    }
                }

                // 真ん中を移動してそろうパターン
                for (int d = 0; d < 4; d++) {
                    int dx = [0, 1, 0, -1][d]
                    int dy = [-1, 0, 1, 0][d]
                    int x2 = x + dx * 2
                    int y2 = y + dy * 2

                    if (isValid(x2) && isValid(y2) && map[x2][y2] == target) {
                        int xm = x + dx
                        int ym = y + dy

                        for (int d2 = 0; d2 < 4; d2++) {
                            int d2x = [0, 1, 0, -1][d2]
                            int d2y = [-1, 0, 1, 0][d2]
                            int x3 = xm + d2x
                            int y3 = ym + d2y

                            if ((x2 != x3 || y2 != y3) && (x != x3 || y != y3) &&
                                    isValid(x3) && isValid(y3) && map[x3][y3] == target) {
//                            printMap(map)
//                            println "Found: ($x, $y) ($x2, $y2) ($x3, $y3)"
                                moveMouse(xm, ym, d2x, d2y)
                                isFound = true
                                // return
                            }
                        }
                    }
                }
            }
        }

//        if (!isFound) {
//            println "Error: Not found. imgNo = $imgNo"
//            printMap(map)
//            ImageIO.write(bi, "png", new File("tmp/test${imgNo++}.png"))
//        }
    }

    static int toScreenX(int x) { x * 34 + 445 }
    static int toScreenY(int y) { y * 34 + 222 }

    static void moveMouse(int x, int y, int dx, int dy) {
        robot.mouseMove(toScreenX(x + dx) + 16, toScreenY(y + dy) + 16)
        Thread.sleep(50)
        robot.mousePress(InputEvent.BUTTON1_MASK)
        Thread.sleep(50)
        robot.mouseMove(toScreenX(x) + 16, toScreenY(y) + 16)
        Thread.sleep(50)
        robot.mouseRelease(InputEvent.BUTTON1_MASK)
        Thread.sleep(50)
    }

    static void clickCounterAnimal(int x, int y) {
        robot.mouseMove(toScreenX(x) + 16, toScreenY(y) + 16)
        Thread.sleep(50)
        robot.mousePress(InputEvent.BUTTON1_MASK)
        Thread.sleep(50)
        robot.mouseRelease(InputEvent.BUTTON1_MASK)
        Thread.sleep(50)
    }
}
