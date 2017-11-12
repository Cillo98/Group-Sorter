package misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHandler {
    
    // BOTH METHODS AND THE VARIABLES IN THIS CLASS ARE STATIC SO THAT THEY CAN
    // BE CALLED ANYTIME WITHOUT PRIOR INITIALIZATION
    
    // file path
    private static final String PATH = "entities.txt";
    // to avoid displaying "error file not found" error message every time a new group is created
    private static boolean fileErrorAlreadyPrinted = false;
    
    public static void saveGroupEntities(ArrayList<Group> myGroups) {
        // create a new File Object given its path
        File file = new File(PATH);
        
        try {
            // if the file does not exist, it creates a new one
            if (!file.exists()) file.createNewFile();
            // the BufferedWriter will either start writing from the beginning of
            // the file (continueWriting == false) or after the last line (continueWriting == true)
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            // each Entity is written to file as  "attributeSet,", then a new line is made for the following group
            for (int i = 0; i < 5; i++) {
                ArrayList<Entity> myEntities = myGroups.get(i).getEntities();
                int j=0;
                while (j < myEntities.size()) {
                    bw.write(myEntities.get(j).getAttributeSet()+" ");
                    j++;
                }
                
                // make a new line for all but the last group
                if (i < 4)
                    bw.newLine();
                
                // success message for each group
                System.out.println(j+" entities from group "+myGroups.get(i).getName()+" saved onto file");
            }
            
            bw.flush();
            bw.close();
            
        } catch (IOException ioE) {
            System.out.println("Error writing to file");
            //ioE.printStackTrace();
        }
    }
    
    public static ArrayList<Entity> loadEntities(char group) {
        // create a new File Object given its path
        File file = new File(PATH);
        
        // savedEntities is an ArrayList of Entities that, at the end of the method,
        // will contain all the Entities in the given Group ('group')
        ArrayList<Entity> savedEntities = new ArrayList<>();
        String line;

        try {
            // creation of the BufferedReader object
            BufferedReader br = new BufferedReader(new FileReader(file));

            // loop as long as there is a following line
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (i == group - 'A') {
                    if (!line.isEmpty()) {
                    String[] attributeSets = line.split(" ");
                     
                    for (int j=0; j<attributeSets.length; j++)
                        savedEntities.add(new Entity(attributeSets[j]));
                    } 
                }
                i++;   
            }
        } catch (FileNotFoundException FnFe) {
            if (!fileErrorAlreadyPrinted)
                System.out.println("File not found. No entities will be loaded.\n");
            fileErrorAlreadyPrinted = true;
                //FnFe.printStackTrace();
        } catch (IOException IOe) {
            System.out.println("Error reading the file");
                //IOe.printStackTrace();
        }
        // returned the created ArrayList of Entities to be assigned to the
        // myEntities ArrayList in the given 'group'
        return savedEntities;
    }
}