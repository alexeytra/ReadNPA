package classes;

public class RomanNumeral {
    int last = 2000;

    public int convertRomanToInt(String romanNumeral) throws NumberFormatException {
        int integerValue = 0;
        for (int i = 0; i < romanNumeral.length(); i++) {
            char ch = romanNumeral.charAt( i );
            int number = letterToNumber( ch );
            if ( number == -1) {
                throw new NumberFormatException("Invalid format");
            }
            if (last < number)
                number = number - last - 1;
            integerValue += number;
            last = number;
        }
        last = 2000;
        return integerValue;
    }

    private static int letterToNumber(char letter) {

        switch (letter) {
            case 'I':  return 1;
            case 'V':  return 5;
            case 'X':  return 10;
            case 'L':  return 50;
            case 'C':  return 100;
            case 'D':  return 500;
            case 'M':  return 1000;
            default:   return -1;
        }
    }
}
