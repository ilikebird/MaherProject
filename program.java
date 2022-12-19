/**
 * @author: Maher Nigar
*/

package finalproject_maher;

import java.util.*;
import java.io.File;

public class FinalProject_maher {

    public static void main(String[] args) throws Exception {

        // Reading input from a file
        File textFile = new File("maher.txt");
        Scanner sc = new Scanner(textFile);

        // Stores each set of the input program in an array list since we don't know how many programs
        // there are
        ArrayList<ArrayList<String>> programsList = new ArrayList<>();

        //Continue reading if the file has nextLine
        while (sc.hasNextLine()) {
            // An input  program can have multiple lines so I store the line in an array list
            ArrayList<String> program = new ArrayList<>();

            // Reading one line at a time
            String inputLine = sc.nextLine();
            int len = inputLine.length();

            // making sure what I read has a lenghth of longer than 0
            while (len > 0) {
                //a line at a time being added to the program array
                program.add(inputLine.trim());
                len = 0; //to reuse to len variable
                //If there is another line in the same input, continue to read
                if (sc.hasNextLine()) {
                    inputLine = sc.nextLine();
                    len = inputLine.length();
                }
            }
            // if there is a program, validate and print the program 
            //and the validation status of that program
            if (program.size() > 0) {
                boolean validProgram = isProgram(program);
                System.out.print(program);
                System.out.print(" is a valid program? ");
                System.out.println(validProgram);
                programsList.add(program);
                System.out.println();

            }

        } // end while loop
        // close the scanner
        sc.close();
    }// main()

    //This method validates that the array of strings is a valid program in the language
    // programs are of the form Assignment*
    public static boolean isProgram(ArrayList<String> program) {
        // validates each string in the arraylist
        for (String equation : program) {
            if (!isValidLine(equation)) {
                return false;
            }
        }
        return true;
    }

    // This method is to validate a line that may contain 0, 1 or multiple
    // assignments. It first splits by semicolons and then verifying the extracted
    // substring
    public static boolean isValidLine(String s) {
        s = s.trim();
        int len = s.length();
        if (!(len > 0)) {
            return false;
        }

        // verifing that the line ends with a semicolon 
        char c = s.charAt(len - 1);
        if (c == ';') {
            // remove the last semicolon  
            String s1 = s.substring(0, len - 1);
            //checking if there is any other semicolon
            int index = s1.lastIndexOf(Character.toString(';'));
            //If there is no other semicolon,length of s2 will be < 0
            String s2 = s1.substring(0, index + 1);
            //Taking one section to cehck if this is an assignment
            String s3 = s.substring(index + 1);
            //if there is one semicolon in s, 
            //s is split into two strings rhs is the last assignment and lhs is the rest of s
            if (s2.length() <= 0) {
                return isAssignment(s3);
            }
            //If there are more than one assignment in the same program
            return isValidLine(s2) && isAssignment(s3);
        }
        //Otherwise we know line doesn't end with a semicolon
        return false;
    }// validLine()

    // Validates that s is an assignment of the language
    // assignments are of the form identifier = exp;
    public static boolean isAssignment(String s) {
        s = s.trim();
        int len = s.length();
        // the minimun requirement is [letter | digit] 
        if (len < 2) {
            return false;
        }
        //check that line ends with a semicolon
        char c = s.charAt(len - 1);
        if (c != ';') {
            return false;
        }
        // remove the semicolon 
        String s1 = s.substring(0, len - 1);
        //split in the rhs and lhs if there is an = sign
        String[] sides = s1.split(Character.toString('='));
        // verifies that there was only one = sign 
        //And there has to be at least 2 sides: identifier and assignment
        if (sides.length != 2) {
            return false;
        }

        // verifies that lhs is an identifier and rhs is an exp
        return isIdentifier(sides[0]) && isExp(sides[1]);
    }// isAssignment()'

    // validates that s is an Exp of the language
    // Exp are of the form Exp + Term | Exp - Term | Term
    public static boolean isExp(String s) {
        s = s.trim();
        if (isTerm(s)) {
            return true;
        }
        // find the first index of the + and - signs or -1 if they don't exist
        int plus = s.indexOf(Character.toString('+'));
        int minus = s.indexOf(Character.toString('-'));
        // if the lower index found is a + sign
        if (plus < minus) {
            // if + index is -1 then no + sign was found
            if (plus < 0) {
                return isExpTerm(s, minus);
            }
            //if + isn't -1, we return this
            return isExpTerm(s, plus);
        }
        // the lower index found is a - sign
        if (minus < plus) {
            // - index is -1 so no - sign was found
            if (minus < 0) {
                return isExpTerm(s, plus);
            }
            return isExpTerm(s, minus);
        }
        // s does not contain a + or - sign
        return false;
    }// isExp()

    // splits the data into stringLHS and stringRHS of + or -
    //checking if stringLHS is an Exp and stringRHS is a Term
    public static boolean isExpTerm(String s, int splitIndex) {
        return isExp(s.substring(0, splitIndex)) && isTerm(s.substring(splitIndex + 1));
    }// isEXPTerm()

    // validates that s is a term
    // terms are of the form Term * Fact | Fact
    public static boolean isTerm(String s) {
        s = s.trim();
        if (isFact(s)) {
            return true;
        }
        // find the last * and check for Term * Fact
        //If I find the * in a position >= 0, then I know there might be a terms multiplied to the fact 
        int mult = s.lastIndexOf(Character.toString('*'));

        if (mult >= 0) {
            return isTerm(s.substring(0, mult)) && isFact(s.substring(mult + 1));
        }
        return false;
    }// isTerm()

    // validates that s is a Fact of the language
    public static boolean isFact(String s) {

        s = s.trim();
        int len = s.length();
        // must have at least one character
        if (len < 1) {
            return false;
        }
        char c = s.charAt(0);
        // leading +|- sign
        if (c == '+' || c == '-') {
            // remove the first character and the following blank spaces since its a +|-
            //sign
            s = s.substring(1).trim();
            // a mult sign is not allowed to follow a +|- operator
            if (s.length() > 0 && s.charAt(0) == '*') {
                return false;
            }
            return isExp(s);
        }
        // check if s is encapsulated in parenthesis and remove them
        if (c == '(' && s.charAt(len - 1) == ')') {
            return isExp(s.substring(1, len - 1));
        }
        // either identifier or literal or not a Fact
        return isIdentifier(s) || isLiteral(s);
    }// Fact()

    // verifies that s is a valid identifier for the language
    // identifiers have the form Letter [Letter | Digit]^*
    public static boolean isIdentifier(String s) {
        s = s.trim();
        int len = s.length();
        // must be at least one letter long
        if (len >= 1) {
            // first character must be a letter
            if (isMyLetter(s.charAt(0))) {
                // validates that every character after the 1st is either a letter or a digit
                for (int i = 1; i < len; i++) {
                    char c = s.charAt(i);
                    if (!isMyLetter(c) && !isMyDigit(c)) {
                        return false;
                    }
                }
                return true;
            }
        }
        // either empty string or doesn't start with a letter
        return false;
    }// isIdentifier()

    // validates that c is a letter in the language
    // letters are of the form a|...|z|A|...|Z|_
    public static boolean isMyLetter(char c) {
        char UNDERSCORE = '_';
        return Character.isLetter(c) || c == UNDERSCORE;
    }// isMyLangLetter()

    // validates the s is Literal from the language
    // literals are of the form 0 | non-Zero-digit digits^* 
    public static boolean isLiteral(String s) {
        s = s.trim();
        int len = s.length();
        // if the length is 1, it can be any digit
        if (len == 1) {
            return isMyDigit(s.charAt(0));
        }
        // the first character must be non zero
        if (len > 1 && isNonZeroDigit(s.charAt(0))) {
            // making sure all characters are digits
            for (int i = 1; i < len; i++) {
                //if it's not a digit, return false
                if (!isMyDigit(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        // empty string
        return false;
    }// isLiteral()

    // validates that c is a non-zero digit 
    // non-zero digits are of the form 1|...|9
    public static boolean isNonZeroDigit(char c) {
        //checks if c is a digit, but can't be a zero
        return isMyDigit(c) && c != '0';
    }// isNonZeroDigit()

    // validates that c is a digit from the language
    // digits are of the form 0|...|9
    public static boolean isMyDigit(char c) {
        return Character.isDigit(c);
    }

}
