/**
 * Class to mimic behaviour of double when parsing, but allows parsing of E and PI
 */
public class Number {

    public static double parseNumber(String number)
    {
        if(number.equals("E"))
            return Math.E;
        if(number.equals("PI"))
            return Math.PI;

        try { return Double.parseDouble(number); }
        catch(Exception e) { return Double.NaN; }
    }
}
