/*
    Title:          HomeworkAssignment2
    Scope:          CSC 200 (002M) - Yun-Sheng Wang, Ph.D
    Author:         Christopher Yap Campos
    Contact:        cy.campos1983@gmail.com
    Created:        25SEP2018
    Dependencies:   Requires java 10 in order to use inference type "var"
    Summary:        HW2

    Requirements Updated 01SEP2018
        - UI will show:
            Convert a number from base A to base B.

            What is A:

            > 7

            What is B:

            > 5

            Give me a base-7 number:

            > 45.56

            (If it is not a base-7 #, start all over.)

            The number in b-10 is: XYZ

            The number in b-5 is: 1234.43.
 */

import java.util.regex.Pattern;

public class Hw2_Campos {
    public static void main(String[] args) {
        var baseValue = new BaseValue();
        var baseToConvert = Integer.valueOf(0);

        try (var scanner = new Hw2_scanner()) {
//            scanner.runLazyUnitTest();
            baseValue = scanner.getUserInputAsBaseValue();
            baseToConvert = scanner.getUserInput("What is the new base?", baseToConvert);

            scanner.print(String.format("Converting: %s(Base %d) -> x(Base %d)", baseValue.get_value(), baseValue.get_base(), baseToConvert));
            var x = BaseValue.convertToBase(baseValue, baseToConvert);
            scanner.print(String.format("x: %s(Base %d)", x.get_value(), baseToConvert));

//            var input = scanner.getUserInputAsString("Give me a base 3 number");
//            var split = input.split("\\.");
//            var left = split[0];
//            var right = split[1];
//
//            var baseValue2 = new BaseValue();
//            baseValue2.set_base(3);
//            baseValue2.set_value(left);

//            scanner.print(String.format("answer: %s", BaseValue.convertToBase(baseValue2, 2).get_value()));

//            scanner.print("Convert a number from base A to base B.");


        } catch (Exception ex) {
            System.out.println(String.format("Error: Unhandled Exception\nMessage: %s\n", ex.toString()));
        }
    }
}

// We want all functionality of MyScanner as well as being able to handle BaseValue
class Hw2_scanner extends MyScanner {
    public BaseValue getUserInputAsBaseValue() {
        try {
            var _out = new BaseValue();

            _out.set_value(this.getUserInputAsString("Please enter value:"));
            _out.set_base(this.getUserInputAsInteger("Please enter base value:"));

            return _out;
        } catch (Exception exception) {
            System.out.println(String.format("Error: Returning null BaseValue.\nMessage: %s\n", exception.toString()));

            // simple recursion
            return getUserInputAsBaseValue();
        }
    }
}

class BaseValue {
    public BaseValue() {
        _base = 0;
        _value = null;
    }

    public BaseValue(int base, String value) {
        _base = base;
        _value = value;
    }

    // Because we have delegated _base, _value to getters/setters, we can enforce logic for acceptable parameters in "set"
    private int _base;

    public int get_base() {
        return _base;
    }

    public void set_base(int base) throws IllegalArgumentException {
        if (base < 1)
            throw new IllegalArgumentException("Base cannot be less than 1");
        else if (base > 16)
            throw new IllegalArgumentException("Base cannot be greater than 16");
        else
            _base = base;
    }

    private String _value;

    public String get_value() {
        return _value;
    }

    public void set_value(String value) {
        value = value.toLowerCase();

        // use a regex: to support up to base 16, we can only take ranges "0-9" & "a-f"
        var conforms = Pattern.matches("^[a-f0-9\\.]+$", value);

        if (!conforms)
            throw new IllegalArgumentException("Value can only contain values [0-9][a-f]");
        else
            _value = value;
    }

    public static final class LetterValues {
        public static final int A = 10;
        public static final int B = 11;
        public static final int C = 12;
        public static final int D = 13;
        public static final int E = 14;
        public static final int F = 15;

        public static Integer getValue(String strNumber) throws IllegalArgumentException {
            if (strNumber.length() > 1)
                throw new IllegalArgumentException("Too many characters.");

            switch (strNumber.toLowerCase()) {
                case "a":
                    return A;
                case "b":
                    return B;
                case "c":
                    return C;
                case "d":
                    return D;
                case "e":
                    return E;
                case "f":
                    return F;
                default:
                    throw new IllegalArgumentException("Value out of range.");
            }
        }

        public static String getValue(double number) throws IllegalArgumentException {
            var asInt = (int) number;

            switch (asInt) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    return asInt + "";
                case 10:
                    return "a";
                case 11:
                    return "b";
                case 12:
                    return "c";
                case 13:
                    return "d";
                case 14:
                    return "e";
                case 15:
                    return "f";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private static boolean _containsDecimal(String value) {
        return value.contains(".");
    }

    public static BaseValue convertToBase(BaseValue value, Integer base) throws UnsupportedOperationException {
        if (value.get_base() == base)
            return value;

        if (base != 10) {
            var _out = new BaseValue();

            // convert to base 10
            var base10 = value.get_base() == 10 ? value : BaseValue.toBase10(value);

            _out.set_value(_convertBase10StringToNewBase(base10.get_value(), base));
            _out.set_base(base);

            return _out;

        } else {
            return BaseValue.toBase10(value);
        }
    }

    private static String _convertBase10StringToNewBase(String base10Value, int newBase) {

        if (_containsDecimal(base10Value)) {
            var strSplit = base10Value.split("\\.");
            var strLeft = strSplit[0];
            var strRight = strSplit[1];

            var strLeftSolved = _convertBase10ToBaseLeft(strLeft, newBase);

            var strRightSolved = _convertBase10ToBaseRight(strRight, newBase);

            return strLeftSolved + "." + strRightSolved;
        } else {
            return _convertToBase10Left(base10Value, newBase);
        }
    }

    private static String _convertBase10ToBaseLeft(String strLeft, int newBase) {
        var base10ValueAsDouble = Double.parseDouble(strLeft);
        var remainder = 0;
        var roundedValue = (int) Math.floor(base10ValueAsDouble);

        java.util.List<String> stringList = new java.util.ArrayList<>();

        do {
            remainder = roundedValue % newBase;
            stringList.add(remainder > 9 ? LetterValues.getValue(remainder) : remainder + "");
            roundedValue = (int) Math.floor(roundedValue / newBase);

        } while (roundedValue != 0);

        var _outvalue = "";
        // stringlist is backwards, so we have append it to the value backwards
        for (var x = stringList.size() - 1; x >= 0; x--) {
            _outvalue += stringList.get(x);
        }

        return _outvalue;
    }

    private static String _convertBase10ToBaseRight(String strRight, int newBase) {
        var value = Double.valueOf("." + strRight);
        var base = Double.valueOf(newBase + "");
        var total = Double.valueOf(0);
        var _out = "";

        // we'll set precision to the amount of sigfigs in strRight
        var tries = strRight.length();

        do {
            var strValue = (value * base) + "";
            var strSplit = strValue.split("\\.");
            var _strLeft = strSplit[0];
            var _strRight = strSplit[1];
            _out += LetterValues.getValue(Double.valueOf(_strLeft).doubleValue());
            value = Double.parseDouble("." + _strRight);

            tries--;
        } while ((tries > 0) && (value != 0.0));

        return _out;
    }

    // Returns the (Base 10) Value of the given baseValue
    public static BaseValue toBase10(BaseValue baseValue) {
        var base = baseValue.get_base();
        var value = baseValue.get_value();

        if (base == 10)
            return baseValue;

        if (_containsDecimal(value)) {
            var strSplit = value.split("\\.");
            var strLeft = strSplit[0];
            var strRight = strSplit[1];

            // solve for the left side of the decimal
            var strLeftSolved = _convertToBase10Left(strLeft, base);

            // solve for the right side of the decimal
            var strRightSolved = _convertToBase10Right(strRight, base);

            // concatenate the values and return BaseValue
            return new BaseValue(10, strLeftSolved + "." + strRightSolved);
        } else {

            var _out = new BaseValue();

            _out.set_value(_convertToBase10Left(baseValue.get_value(), baseValue.get_base()));
            _out.set_base(10);

            return _out;
        }
    }

    private static String _convertToBase10Left(String leftValue, int base) {
        var total = Double.valueOf(0);
        var increment = Double.valueOf(0);
        var _value = leftValue;
        var _base = base;

        // convert base 10 to given base param
        for (var x = _value.length() - 1; x >= 0; x--) {
            // get the first letter as String
            var character = String.valueOf(_value.charAt(x));

            // determine if character is a number
            if (Pattern.matches("[0-9]", character)) {
                var number = Integer.parseInt(character);
                total += number * Math.pow(_base, increment);
            } else if (Pattern.matches("[a-f]", character)) {  // determine if char is a letter
                var number = LetterValues.getValue(character);
                total += number * Math.pow(_base, increment);
            }

            increment++;
        }

        var integer = total.intValue();
        return integer + "";
    }

    private static String _convertToBase10Right(String rightValue, int base) {
        var increment = Double.valueOf(1);
        var _base = Double.valueOf(base + "");
        var total = Double.valueOf(0);

        // convert base 10 to given base param
        for (var x = 0; x < rightValue.length(); x++) {
            var character = String.valueOf(rightValue.charAt(x));

            // determine if character is a number
            if (Pattern.matches("[0-9]", character)) {
                var number = Double.parseDouble(character);
//                total += number * Math.pow(_base, -increment);
                var pow = Math.pow(_base, (-1 * increment));
                total += number * pow;
            } else if (Pattern.matches("[a-f]", character)) {  // determine if char is a letter
                var number = LetterValues.getValue(character);
                total += number * Math.pow(_base, -increment);
            }

            increment++;
        }

        return (total + "").replace("0.", "");
    }
}
