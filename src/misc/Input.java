package misc;

import java.util.ArrayList;
import java.util.Scanner;

public class Input {
    // as the Scanner object is used in many methods, one is created as class variable
    private static final Scanner in = new Scanner(System.in);
    // same for line
    private static String line;
    
    public static boolean getYes() {
        // loops as long as either true or false are returned.
        // The following lines are self-explanatory
        while (true) {
            line = in.nextLine().toLowerCase();
            
            if (line.equals("yes") || line.equals("y") || line.equals("true"))
                return true;
            
            if (line.equals("no") || line.equals("n") || line.equals("false"))
                return false;
            
            // if the programme executes the following instruction, neither true
            // or false have been returned, therefore an error message is displayed
            System.out.print("Invalid input, please try again: ");
        }
    }
    
    public static String getattributes() {
    // this methods asks the user to input a attributeset in the format 'ABC'
    
        boolean valid = false;
        while (!valid) {
            System.out.print("Enter the attributes. [format: 'ABC' - accepted values: {A,B,C,D,E}]: ");
            line = in.nextLine().toUpperCase();
            // suppose the input to be valid
            valid = true;
            
            // execute the following only if the input is three characters long, otherwise valid = false
            if (line.length() == 3) {
                // loop 3 times to check each of the 3 characters. If any of the
                // characters is not between A and E, valid = false
                for (int i=0; i<3; i++) {
                    if (line.charAt(i) < 'A' || line.charAt(i) > 'E')
                        valid = false;
                }
            } else valid = false;
            
            // if the input is not valid, an error message is displayed
            if (!valid)
                System.out.println("Input invalid. Please enter again"); 
        }
        
        // the valid attributeset (line) is returned
        return line;
    }
    
    public static int getMenuChoice() {
    // this method returns the user's menu choice as an integer 0 to 9
    
        // the choice must be initialized
        int choice = 0;
        boolean valid = false;
        
        // loop as long as a valid choice is not selected
        while (!valid) {
            line = in.nextLine();
            try {
                // converts the entered string into a number. Possible exeptions are caught
                choice = Integer.parseInt(line);
                
                // the choice must be between 0 and 9
                if (choice >= 0 && choice <= 9)
                    valid = true;
                 else
                    System.out.print("\tInvalid choice, please try again: ");
                
            } catch (NumberFormatException e) {
                // in case the entered string does not correspond to a number...
                System.out.print("\tNot a number, please try again: ");
                    //e.printStackTrace();
            }
        }
        
        System.out.println();
        return choice;
    }
    
    public static int getInteger() {
    // this method simply returns the user's entered integer, if valid
        int number = 0;
        line = in.nextLine();
        boolean valid = false;
        
        while (!valid) {
            try {
                number = Integer.parseInt(line);
                valid = true;
            } catch (NumberFormatException NFe) {
                System.out.print("Entered value is not a number, please enter again: ");
                    //NFe.printStackTrace();
            }
        }
        
        return number;
    }
    
    public static char getGroup(int Case) {
    // returns a char corresponding to the selected group (A to E)
        boolean valid = false;
        
        // based on the context (case) in which this method is called, a different message will appear
        if (Case == 1)
            System.out.print("\nEnter the group name [A, B, C, D, E] from which the Entity must be removed: ");
        if (Case == 2)
            System.out.print("\nEnter the group name [A, B, C, D, E] from which the Entity must be moved: ");
        if (Case == 3)
            System.out.print("\nEnter the group name [A, B, C, D, E] to which the Entity must be moved: ");
        
        // loop as long as a valid group is not entered
        while (!valid) {
            line = in.nextLine().toUpperCase();
            
            // if the input is not 1 character, an error message is displayed
            if (line.length() != 1) {
                System.out.println("Invalid input: 1 character only allowed.");
            } else {
                // if the character is between A and E, it is a valid group...
                if (line.charAt(0) >= 'A' && line.charAt(0) <= 'E') {
                    valid = true;
                } else {
                    // ... otherwise an error message is displayed
                    System.out.println("Invalid input: the character is not in [A, B, C, D, E]");
                }

            }
            
            // ask the user to enter again if the input was not valid
            if (!valid)
                System.out.print("\nTry again: ");
        }
        
        // at this point, 'goup' is surely between A ad E
        return line.charAt(0);
    }
    
    public static String getattributesIfAvailable(ArrayList<Entity> myEntities) {
    // given an ArrayList of entities, this method returns the user's entered
    // attributeSet only if that attributeSet corresponds to the attributeSet of one of the 
        boolean valid = false;
        System.out.print("Enter the attributeset of the Entity: ");
        
        while (!valid) {
            line = in.nextLine().toUpperCase();
            valid = true; // assume the input to be valid
            
            if (line.length() != 3) {
                System.out.println("Invalid input: 3 characters only allowed.");
                valid = false;
            } else {
                // loop for each of the three characters
                for (int i=0; i<3; i++) {
                    if (line.charAt(i) < 'A' || line.charAt(i) > 'E') {
                        // if one character is less than A or after E, print error message and invalidate the attributeSet
                        System.out.println("Invalid input: the character number "+(i+1)+" is not in [A, B, C, D, E]");
                        valid = false;
                    }
                }
            }
            
            // check for the existance of a Entity who has the same attributeSet
            boolean volExists = false;
            if (valid) {
                line = orderattributes(line);
                for (Entity vol: myEntities) {
                    if (vol.getattributeSet().equals(line))
                        volExists = true;
                }
            }
            
            
            // if no corresponding Entity is found, the attributeSet is invalid
            if (!volExists)
                valid = false;
            
            // if the attributeSet is invalid, an error message is displayed
            if (!valid)
                System.out.print("\nEntity "+line+" not in the group. Try again: ");
        }
        
            //System.out.println("Chosen Entity: "+new String(attributeSet));
        return line;// return the valid attributeset
    }
    
    public static String orderattributes(String attributeSet) {
    // even though there is not input, this class seemed the most appropriate for this method
    
        // As the values are only 3, I order it with a series of 3 IFs instead
        // of with a sorting algorithm (that would require 2 nested for loops)
        char temp; // needed to swap values
        char[] attribute = attributeSet.toCharArray();
        if ( (int)attribute[0] > (int)attribute[1] ) {
            temp = attribute[0];
            attribute[0] = attribute[1];
            attribute[1] = temp;
        }
        if ( (int)attribute[1] > (int)attribute[2] ) {
            temp = attribute[1];
            attribute[1] = attribute[2];
            attribute[2] = temp;
        }
        if ( (int)attribute[0] > (int)attribute[1] ) {
            temp = attribute[0];
            attribute[0] = attribute[1];
            attribute[1] = temp;
        }
        return new String(attribute);
    }
}