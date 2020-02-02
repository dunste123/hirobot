package me.duncte123.hirobot;

public class Utils {
    /**
     * https://github.com/duncte123/ColorUtils/blob/master/src/main/java/me/duncte123/colorutils/ColorUtils.java
     *
     * Converts a hex string to an color int
     *
     * @param hex
     *         The hex string to convert to a color int
     *
     * @return The color as int
     */
    public static int hexStringToInt(String hex) {
        final String hexValue = "0x" + hex.replaceFirst("#", "");

        return Integer.decode(hexValue);
    }
}
