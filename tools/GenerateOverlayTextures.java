// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Fallatori
//
// Regenerates the original overlay textures. Usage:
//   java tools/GenerateOverlayTextures.java src/main/resources/assets/sensorlamp/textures
import javax.imageio.ImageIO; import java.awt.image.BufferedImage; import java.io.File;
// Draws the Sensor Lamp overlay textures from scratch (no source image is read).
public class GenerateOverlayTextures {
  static BufferedImage blank() { return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB); }
  // 4x5 "unlit glass" pane: darker edges, slightly lighter centre. Rows 3..7, columns 1..4 (side-face UV region 0,2..6,9).
  static void glass(BufferedImage img, int edge, int mid, int centre) {
    int[][] p = { {edge, mid, mid, edge}, {mid, centre, centre, mid}, {mid, centre, centre, mid}, {mid, centre, centre, mid}, {edge, mid, mid, edge} };
    for (int r = 0; r < 5; r++) for (int c = 0; c < 4; c++) img.setRGB(1 + c, 3 + r, 0xFF000000 | p[r][c]);
  }
  public static void main(String[] a) throws Exception {
    File out = new File(a[0]);
    // Regular lantern: also covers the glowing rim (columns 0 and 5) and the lid's lower row (row 1, columns 1..4).
    BufferedImage lantern = blank();
    glass(lantern, 0x4C463A, 0x524C3E, 0x58523F);
    int rim = 0xFF433E32;
    for (int y = 3; y <= 7; y++) { lantern.setRGB(0, y, rim); lantern.setRGB(5, y, rim); }
    for (int x = 1; x <= 4; x++) lantern.setRGB(x, 1, rim);
    ImageIO.write(lantern, "png", new File(out, "block/lantern_off_overlay.png"));
    // Soul lantern: glass pane only (its frame does not glow).
    BufferedImage soul = blank();
    glass(soul, 0x34474A, 0x3A4F51, 0x405658);
    ImageIO.write(soul, "png", new File(out, "block/soul_lantern_off_overlay.png"));
    // Item indicator: 2x2 red light, drawn over the vanilla lantern item sprite.
    BufferedImage ind = blank();
    for (int y = 9; y <= 10; y++) for (int x = 7; x <= 8; x++) ind.setRGB(x, y, 0xFFE63228);
    ImageIO.write(ind, "png", new File(out, "item/sensor_indicator.png"));
  }
}
