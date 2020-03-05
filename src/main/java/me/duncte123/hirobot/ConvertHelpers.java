package me.duncte123.hirobot;

import javax.annotation.Nullable;
import javax.measure.Measure;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Temperature;
import javax.measure.unit.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static javax.measure.unit.NonSI.*;
import static javax.measure.unit.SI.*;

@SuppressWarnings("rawtypes")
public class ConvertHelpers {
    private static final Map<String, BiFunction<Double, Unit, Double>> UNIT_MAP = new HashMap<>();

    static {
        UNIT_MAP.put("c", ConvertHelpers::toCelsius);
        UNIT_MAP.put("f", ConvertHelpers::toFahrenheit);
        UNIT_MAP.put("k", ConvertHelpers::toKelvin);

        UNIT_MAP.put("km", ConvertHelpers::toKilometer);
        UNIT_MAP.put("m", ConvertHelpers::toMeter);
        UNIT_MAP.put("cm", ConvertHelpers::toCentimeter);
        UNIT_MAP.put("i", ConvertHelpers::toInch);
        UNIT_MAP.put("ft", ConvertHelpers::toFoot);
        UNIT_MAP.put("mi", ConvertHelpers::toMile);
    }

    @Nullable
    public static BiFunction<Double, Unit, Double> getConvertMethod(String unit) {
        return UNIT_MAP.get(unit);
    }

    @Nullable
    public static Unit<? extends Quantity> getUnitForInput(String inputUnit) {
        switch(inputUnit.toLowerCase()) {
            case "k":
                return KELVIN;
            case "c":
                return CELSIUS;
            case "f":
                return FAHRENHEIT;

            case "km":
                return KILOMETER;
            case "m":
                return METER;
            case "cm":
                return CENTIMETER;
            case "in":
                return INCH;
            case "ft":
                return FOOT;
            case "mi":
                return MILE;

            default:
                return null;
        }
    }


    public static double toCelsius(double input, Unit<Temperature> inputUnit) {
        return convert(input, inputUnit, CELSIUS);
    }

    public static double toFahrenheit(double input, Unit<Temperature> inputUnit) {
        return convert(input, inputUnit, FAHRENHEIT);
    }

    public static double toKelvin(double input, Unit<Temperature> inputUnit) {
        return convert(input, inputUnit, KELVIN);
    }

    public static double toKilometer(double input, Unit<Length> inputUnit) {
        return convert(input, inputUnit, KILOMETER);
    }

    public static double toMeter(double input, Unit<Length> inputUnit) {
        return convert(input, inputUnit, METER);
    }

    public static double toCentimeter(double input, Unit<Length> inputUnit) {
        return convert(input, inputUnit, CENTIMETER);
    }

    public static double toFoot(double input, Unit<Length> inputUnit) {
        return convert(input, inputUnit, FOOT);
    }

    public static double toMile(double input, Unit<Length> inputUnit) {
        return convert(input, inputUnit, MILE);
    }

    public static double toInch(double input, Unit<Length> inputUnit) {
        return convert(input, inputUnit, INCH);
    }

    /*private static double convertTemperature(double input, Unit<Temperature> sourceUnit, Unit<Temperature> targetUnit) {
        final UnitConverter converter = sourceUnit.getConverterTo(targetUnit);
        final double measure = Measure.valueOf(input, sourceUnit).doubleValue(sourceUnit);

        return converter.convert(measure);
    }

    private static double convertLength(double input, Unit<Length> sourceUnit, Unit<Length> targetUnit) {
        final UnitConverter converter = sourceUnit.getConverterTo(targetUnit);
        final double measure = Measure.valueOf(input, sourceUnit).doubleValue(sourceUnit);

        return converter.convert(measure);
    }*/

    @SuppressWarnings("unchecked")
    private static double convert(double input, Unit<? extends Quantity> sourceUnit, Unit<? extends Quantity> targetUnit) {
        final UnitConverter converter = sourceUnit.getConverterTo(targetUnit);
        final Unit<Quantity> wtfJava = (Unit<Quantity>) sourceUnit;
        final double measure = Measure.valueOf(input, wtfJava).doubleValue(wtfJava);

        return converter.convert(measure);
    }

}
