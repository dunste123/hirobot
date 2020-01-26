package me.duncte123.hirobot;

import javax.measure.Measure;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Temperature;
import javax.measure.unit.Unit;

import static javax.measure.unit.NonSI.*;
import static javax.measure.unit.SI.*;

public class ConvertHelpers {

    public static double toCelsius(double input, Unit<Temperature> inputUnit) {
        return convertTemperature(input, inputUnit, CELSIUS);
    }

    public static double toFahrenheit(double input, Unit<Temperature> inputUnit) {
        return convertTemperature(input, inputUnit, FAHRENHEIT);
    }

    public static double toKelvin(double input, Unit<Temperature> inputUnit) {
        return convertTemperature(input, inputUnit, KELVIN);
    }

    public static double toKilometer(double input, Unit<Length> inputUnit) {
        return convertLength(input, inputUnit, KILOMETER);
    }

    public static double toMeter(double input, Unit<Length> inputUnit) {
        return convertLength(input, inputUnit, METER);
    }

    public static double toMile(double input, Unit<Length> inputUnit) {
        return convertLength(input, inputUnit, MILE);
    }

    private static double convertTemperature(double input, Unit<Temperature> sourceUnit, Unit<Temperature> targetUnit) {
        final UnitConverter converter = sourceUnit.getConverterTo(targetUnit);
        final double measure = Measure.valueOf(input, sourceUnit).doubleValue(sourceUnit);

        return converter.convert(measure);
    }

    private static double convertLength(double input, Unit<Length> sourceUnit, Unit<Length> targetUnit) {
        final UnitConverter converter = sourceUnit.getConverterTo(targetUnit);
        final double measure = Measure.valueOf(input, sourceUnit).doubleValue(sourceUnit);

        return converter.convert(measure);
    }

    private static double convert(double input, Unit<? extends Quantity> sourceUnit, Unit<? extends Quantity> targetUnit) {
        final UnitConverter converter = sourceUnit.getConverterTo(targetUnit);
        final double measure = Measure.valueOf(input, sourceUnit).doubleValue(sourceUnit);

        return converter.convert(measure);
    }

}
