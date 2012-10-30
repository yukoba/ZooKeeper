import java.awt.image.BufferedImage
import javax.imageio.ImageIO

Map hash2type = [:]
hash2type[-1105833264] = 1
hash2type[-1073523304] = 1
hash2type[-1870107180] = 2
hash2type[-1892344432] = 2
hash2type[1219501592] = 3
hash2type[1243028218] = 3
hash2type[1188725419] = 4
hash2type[-2129685109] = 4
hash2type[1685273973] = 5
hash2type[1680095637] = 5
hash2type[-712314208] = 6
hash2type[-705586956] = 6

int[][] map = new int[8][8]
(0..7).each { int y ->
    (0..7).each { int x ->
        BufferedImage biSub = ImageIO.read(new File("../tmp/zookeeper/${y}_${x}.png"))
        map[x][y] = hash2type[hashCode(biSub)] ?: 0
        print "${map[x][y]} "
    }
    println ""
}

int hashCode(BufferedImage bi) {
    int[] rgbs = bi.getRGB(0, 0, bi.width, bi.height, null, 0, bi.width)
    int sum = 0
    rgbs.each { sum += it }
    return sum;
}

boolean isValid(x) { x >= 0 && x <= 7 }

(0..7).each { int y ->
    (0..7).each { int x ->
        int target = map[x][y]
        if (target == 0) return

        (0..3).each { int d ->
            int dx = [0, 1, 0, -1][d]
            int dy = [-1, 0, 1, 0][d]
            int x2 = x + dx
            int y2 = y + dy

            if (isValid(x2) && isValid(y2) && map[x2][y2] == target) {
                (0..3).each { int d2 ->
                    int d2x = [0, 1, 0, -1][d2]
                    int d2y = [-1, 0, 1, 0][d2]
                    int x3 = x2 + dx + d2x
                    int y3 = y2 + dy + d2y

                    if (isValid(x3) && isValid(y3) && (x2 != x3 || y2 != y3) && map[x3][y3] == target) {
                        println "Found: ($x, $y) ($x2, $y2) ($x3, $y3)"
                    }
                }
            }
        }
    }
}
