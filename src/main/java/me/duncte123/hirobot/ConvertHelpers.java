package me.duncte123.hirobot;

import java.util.List;

public class ConvertHelpers {


    public static enum Unit {
        FAHRENHEIT("f", "F"),
        KELVIN("k", "K"),
        CELSIUS("c", "C"),
        ;

        private final List<String> keys;

        Unit(final String... keys) {
            this.keys = List.of(keys);
        }

        /*
           KELVIN.toCelsius(5) === this == kelvin
           CELSIUS.toCelsius(5) === this == celsius
         */
        public float toCelsius(final float input) {
            switch (this) {
                case FAHRENHEIT:
                    return 0;
                case KELVIN:
                    return toKelvin(input, this);
                case CELSIUS:
                    return input;
                default:
                    throw new IllegalArgumentException("Input was not a temperature unit");
            }
        }

        private float toKelvin(final float input, final Unit from) {
            switch (from) {
                case CELSIUS:
                    return input + 273.15f;
                case FAHRENHEIT:
                    return (input - 32) * 5/9 + 273.15f;
                case KELVIN:
                    return input;
                default:
                    throw new IllegalArgumentException("Input was not a temperature unit");
            }
        }

        private float toFahrenheit(final float input, final Unit from) {
            switch (from) {
                case CELSIUS:
                    return input + 273.15f;
                case FAHRENHEIT:
                    return input;
                case KELVIN:
                    return input;
                default:
                    throw new IllegalArgumentException("Input was not a temperature unit");
            }
        }

        public static Unit fromKey(final String key) {
            for (Unit value : values()) {
                if (value.keys.contains(key)) {
                    return value;
                }
            }

            return null;
        }
    }
}
