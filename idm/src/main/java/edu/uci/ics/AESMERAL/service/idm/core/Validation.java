package edu.uci.ics.AESMERAL.service.idm.core;

public class Validation {

    public Validation() {
    }

    public boolean passwordCharacterValidation(char[] arr) {
        boolean containsDigit = false;
        boolean containsUpper = false;
        boolean containsLower = false;
        for (int i = 0; i < arr.length; i++) {
            if (Character.isDigit(arr[i]))
                containsDigit = true;
            if (Character.isUpperCase(arr[i]))
                containsUpper = true;
            if (Character.isLowerCase(arr[i]))
                containsLower = true;
        }
        return containsDigit && containsUpper && containsLower;
    }

    public int checkEmail(String email) {
        if (email == null || email.equals(""))
            return -10;
        String[] format = email.split("@", -1);
        // checking the <email> part
        if (!format[0].matches("[a-zA-Z0-9]+") || format.length != 2 || format[0].equals("") || format[0] == null)
            return -11;
        // check both the domain and extension
        String[] secondRoundFormat = format[1].split("\\.", -1);
        if (secondRoundFormat.length != 2 || secondRoundFormat[0].equals("") || secondRoundFormat[1].equals("")
                || secondRoundFormat[0] == null || secondRoundFormat[1] == null)
            return -11;
        return 1;
    }

    public boolean plevel(int inputPlevel, int userPlevel) {
        return userPlevel <= inputPlevel;
    }

}
