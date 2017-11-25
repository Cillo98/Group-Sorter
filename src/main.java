
import java.util.Random;

/* IMPORTANT:
SOME LINES CONTAINING SYSTEM.OUT.PRINTLNs AND PRINTSTACKTRACEs HAVE BEEN COMMENTED 
OUT. THEY SERVE AS GUIDES TO BETTER DEBUG AN UNDERSTAND THE CODE. IF A CHUNK
OF CODE'S WORKING PROCESS IS UNCLEAR, UNCOMMENTING THOSE LINES CAN GIVE USEFUL
INFORMATION ON WHAT IS GOING ON IN THE PROGRAMME. THEY HAVE NO OTHER USE.
*/

public class Main {
    AttributeSorter sks;
    
    public static void main(String[] args){
        // create a new object from the main class to avoid static context problems
        Main main = new Main();
    }
    
    private Main() {
        menu(); // initiates the programme
    }
    
    private void menu() {
        // the programme first asks the user whether to load entities from file or not.
        // this is the only chance the user has to retrieve saved entities
        System.out.print("Would you like to load all entities and groups saved previously (y/n) ? ");
        // the initialization of AttributeSorter either loads entities from file into
        // the Groups or not based on the value of loadFromFile
        boolean loadFromFile = Input.getYes();
        sks = new AttributeSorter(loadFromFile);
        
        // the menu (and therefore the programme) runs as long as goOn is true. goOn
        // is set to false by processChoice() only if the user's menu choice is
        // 10 (save & exit) or 9 (exit) and then the user is sure to quit (quit? y)
        boolean goOn = true;
        while (goOn) {
            displayMenu(); // simple serie of System.out.println()
            int menuChoice = Input.getMenuChoice(); // contains catching of exceptions
            goOn = processChoice(menuChoice);
        }
    }
    
    private boolean processChoice(int choice) {
        
        switch (choice) { // switch case for the menu
            
            case 0: // save and exit
                FileHandler.saveGroupEntities(sks.getGroups());
                return false; // returning false, the programme will stop the menu loop
                
                
            case 1: // add a entity
                // in order to add a entity, there must be at least an empty group
                if (!sks.allGroupsFull()) {
                    String attributes = Input.getAttributes(); // contains catching of exceptions
                    // call to the allocation algorith in AttributeSorter given the entity
                    sks.addEntity(new Entity(attributes));
                } else // error if all groups are full
                    System.out.print("Impossible to add more entities, all groups are full.");
                return true; // returning true, the programme re-displays the menu
                
                
            case 2: // move a entity
                moveEntity(); // the method is written below to keep the code tidy
                return true;
                
                
            case 3: // delete a entity
                deleteEntity(); // the method is written below to keep the code tidy
                return true;
                
                
            case 4: // delete all entities
                System.out.print("Are you sure to delete all entities from the system (y/n) ? ");
                if (Input.getYes())
                    sks.deleteAllEntities();
                return true;
                
                
            case 5: // order the entities
                sks.orderAllEntities();
                return true;
                
                
            case 6: // generate random entities
                System.out.print("Insert number of random entities to add randomly: ");
                int number = Input.getInteger(); // contains catching of exceptions
                
                // the Random object is created here to avoid calling it many times
                Random ran = new Random(); 
                for (int i = 0; i<number; i++) { // loop once per each entity to be added
                    if (!sks.allGroupsFull()) // can add only if groups are not full
                        sks.addEntity(new Entity(randomAttributesGenerator(ran)));
                    else {
                        System.out.print("Impossible to add more entities, all groups are full.");
                        i = number; // stop the for loop if all groups are full
                    }
                }
                return true;
                
                
            case 7: // display all entities
                for (int i=0; i<5; i++)
                    sks.displayAllEntities((char)(i+'A'));
                return true;
                
                
            case 8: // display all groups
                sks.displayGroups();
                return true;
                
                
            case 9: // exit
                System.out.print("Are you sure you want to quit without saving (y/n) ? ");
                // if the user chooses to quit (quit==true), the method returns false,
                // otherwise it will return true (if quit==false) to continue
                boolean quit = Input.getYes();
                return !quit;
                
                
            default: // to avoid writing a return true/false outside the switch case
                return true;
        }
    }
    
    private String randomAttributesGenerator(Random ran) {
        // to generate a random letter A to E, a random integer 0 to 4 is added
        // to the ASCII code of A. A+0=A, A+1=B, A+2=C, A+3=D, A+4=E
        char[] attribute = new char[3];
        attribute[0] = (char)(ran.nextInt(5)+'A');
        attribute[1] = (char)(ran.nextInt(5)+'A');
        attribute[2] = (char)(ran.nextInt(5)+'A');
        // the char array is converted to String and returned
        return new String(attribute);
    }
    
    private void deleteEntity() {
        // even though the user does not ask for it, ordering the entities
        // before displaying them gives a much neater view of the available choices
        sks.orderAllEntities();
        for (int i=0; i<5; i++)
            sks.displayAllEntities((char)(i+'A'));
        
        // if there is no  group that has at least a entity, no
        // entities can be deleted
        boolean nonEmptyExists = false;
        for (Group cg: sks.getGroups())
            if (cg.getCapacityLeft() != 500)
                nonEmptyExists = true;
        
        if (nonEmptyExists) {
            // loop as long as a valid group (= not empty) is not selected
            boolean validGroup = false;
            char group;
            while (!validGroup) {
                group = Input.getGroup(1); // '1' to display the keywords "remove" & "from"
                //((int)group)-65 is the index at which the selcted group is to be found
                Group tmpCG = sks.getGroups().get(((int)group)-65);

                // check that the group actually has entities (checking on the temporary group) 
                if (tmpCG.getTotAttributes() > 0) { 
                    validGroup = true; // stop the loop
                    sks.displayAllEntities(group); // give the user the possible choices
                    // get the user's choice in attributeSet (with exceptions catching)
                    String attributeSet = Input.getAttributesIfAvailable(sks.getGroup(group).getEntities());
                    // delete the selected entity from the selected group (here is a temporary
                    // group, in AttributeSorter the actual group in myGroups will be modified)
                    sks.deleteEntity(attributeSet, tmpCG);
                } else 
                    System.out.println("Selected group is empty, please try again");
            }
        } else
            System.out.println("There are no entities in the system that can be eliminated");
    }
    
    private void moveEntity() {
    // THIS METHODS WORKS SIMILARLY TO deleteEntity(), THE ONLY DIFFERENCE
    // IS THAT IT READS 2 GROUPS INSTEAD OF 1 (FIRST GROUP NOT EMPTY, SECOND GROUP NOT FULL)
        
        sks.orderAllEntities();
        for (int i=0; i<5; i++)
            sks.displayAllEntities((char)(i+'A'));
        // if there is no  group that has at least a entity, no
        // entities can be deleted
        boolean nonEmptyExists = false;
        for (Group cg: sks.getGroups())
            if (cg.getCapacityLeft() != 500)
                nonEmptyExists = true;
        
        if (nonEmptyExists) {
            boolean validFromGroup = false;
            char groupFrom;
            while (!validFromGroup) {
                groupFrom = Input.getGroup(2); // '2' to display the keywords "move" & "from"
                Group tmpCG_From = sks.getGroups().get(((int)groupFrom)-65);

                // check that the group is not empty
                if (tmpCG_From.getTotAttributes() > 0) {
                    validFromGroup = true;
                    sks.displayAllEntities(groupFrom);
                    String attributeSet = Input.getAttributesIfAvailable(sks.getGroup(groupFrom).getEntities());

                    char groupTo;
                    boolean validToGroup = false;
                    while (!validToGroup) {
                        groupTo = Input.getGroup(3); // '3' to display the keywords "move" & "to"
                        // check that the group is not full (capacityLeft > 0)
                        Group tmpCG_To = sks.getGroups().get(((int)groupTo)-65);
                        if (tmpCG_To.getCapacityLeft() > 0) {
                            validToGroup = true;
                            sks.moveEntity(attributeSet, tmpCG_From, tmpCG_To);
                        } else
                            System.out.println("Selected group is full, please try again.");
                    }
                } else 
                    System.out.println("Selected group is empty, please try again.");
            }
        } else
            System.out.println("There are no entities in the system that can be moved");
    }
    
    private void displayMenu() {
        // display the menu
        System.out.println("\n\t _______________________________________________________________");
        System.out.println("\t|\t\t\t  --MENU--\t\t\t\t|");
        System.out.println("\t|\t\t\t\t\t\t\t\t|");
        System.out.println("\t| 1: Add a entity \t\t 2: Move a entity\t\t|");
        System.out.println("\t| 3: Delete a entity \t 4: Delete all entities\t|");
        System.out.println("\t| 5: Order the entities \t 6: Generate random entities\t|");
        System.out.println("\t| 7: Display all entities \t 8: Display all groups\t\t|");
        System.out.println("\t| 9: Exit \t\t\t 0: Save & Exit\t\t\t|");
        System.out.println("\t|_______________________________________________________________|");
        System.out.print("\tChoice: ");
    }
}