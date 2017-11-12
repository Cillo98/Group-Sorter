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
    private static final String PATH = "volunteers.txt";
    // to avoid displaying "error file not found" error message every time a new community group is created
    private static boolean fileErrorAlreadyPrinted = false;
    
    public static void saveGroupVolunteers(ArrayList<CommunityGroup> myGroups) {
        // create a new File Object given its path
        File file = new File(PATH);
        
        try {
            // if the file does not exist, it creates a new one
            if (!file.exists()) file.createNewFile();
            // the BufferedWriter will either start writing from the beginning of
            // the file (continueWriting == false) or after the last line (continueWriting == true)
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            // each Volunteer is written to file as  "skillSet,", then a new line is made for the following group
            for (int i = 0; i < 5; i++) {
                ArrayList<Volunteer> myVols = myGroups.get(i).getVolunteers();
                int j=0;
                while (j < myVols.size()) {
                    bw.write(myVols.get(j).getSkillSet()+" ");
                    j++;
                }
                
                // make a new line for all but the last group
                if (i < 4)
                    bw.newLine();
                
                // success message for each group
                System.out.println(j+" volunteers from group "+myGroups.get(i).getName()+" saved onto file");
            }
            
            bw.flush();
            bw.close();
            
        } catch (IOException ioE) {
            System.out.println("Error writing to file");
            //ioE.printStackTrace();
        }
    }
    
    public static ArrayList<Volunteer> loadVolunteers(char group) {
        // create a new File Object given its path
        File file = new File(PATH);
        
        // savedVols is an ArrayList of Volunteers that, at the end of the method,
        // will contain all the Volunteers in the given CommunityGroup ('group')
        ArrayList<Volunteer> savedVols = new ArrayList<>();
        String line;

        try {
            // creation of the BufferedReader object
            BufferedReader br = new BufferedReader(new FileReader(file));

            // loop as long as there is a following line
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (i == group - 'A') {
                    if (!line.isEmpty()) {
                    String[] skillSets = line.split(" ");
                     
                    for (int j=0; j<skillSets.length; j++)
                        savedVols.add(new Volunteer(skillSets[j]));
                    } 
                }
                i++;   
            }
        } catch (FileNotFoundException FnFe) {
            if (!fileErrorAlreadyPrinted)
                System.out.println("File not found. No volunteers will be loaded.\n");
            fileErrorAlreadyPrinted = true;
                //FnFe.printStackTrace();
        } catch (IOException IOe) {
            System.out.println("Error reading the file");
                //IOe.printStackTrace();
        }
        // returned the created ArrayList of Volunteers to be assigned to the
        // myVolunteers ArrayList in the given 'group'
        return savedVols;
    }
}