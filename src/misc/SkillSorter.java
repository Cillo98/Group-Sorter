
package misc;

import interfaces.AttributeSorterInterface;
import java.util.ArrayList;
import java.util.ListIterator;


public class AttributeSorter implements AttributeSorterInterface {
    // each AttributeSorter object (for the programme's purpose, only 1) has an
    // ArrayList of CommunityGroups, each containing an ArrayList of Entities
    private final ArrayList<CommunityGroup> myGroups = new ArrayList<>();

    
    // constructor
    public AttributeSorter(boolean load) {
        // 5 CommunityGroups are created, each with a name: A, B, C, D, and E.
        // When a CommunityGroup is created (which happens only at the beginning
        // of the programme) it is either filled with the previously saved entities
        // written on file or not, based on the value of 'load', passed in from CW3Main
        myGroups.add(new CommunityGroup('A', load));
        myGroups.add(new CommunityGroup('B', load));
        myGroups.add(new CommunityGroup('C', load));
        myGroups.add(new CommunityGroup('D', load));
        myGroups.add(new CommunityGroup('E', load));
    }

    private int threeEqual(char attribute) {
        // attributePos contains a number 0<number<5 that indicated the position of the given char
        // in an array of letters [0-5]. Example: if attribute = 'C', attributePos = 'C'-'A' = 67 - 65 = 2
        int attributePos = attribute - 'A';
      
        // array containing the sum of all attributes within each group. Has lenght 5
        int[] attributeTotals = attributeTotalsCalc();
      
        // AttributeGaps contains the difference between each group's average attribute quantity
        // except the one given ( (totAttributes - numberOfGivenAttribute) / 4 ) and the chosen
        // attribute's quantity. The bigger the number, the more unbalanced the attributes within a group
        double[] attributeGaps = attributeGapsCalc(attributeTotals, attributePos);
            //System.out.println("Gaps are: "+attributeGaps[0]+", "+attributeGaps[1]+", "+attributeGaps[2]+", "+attributeGaps[3]+", "+attributeGaps[4]);
        
        // indexMaxGap contains one of the indexes of AttributeGaps, the one with the biggest gap to fill
        int indexMaxGap = indexMaxValue(attributeGaps);
            //System.out.println("Index chosen: "+indexMaxGap+" with a gap of "+attributeGaps[indexMaxGap]);
        
        // indexMaxGap now has the index of the group I want to put the entity
        // entity into, but I still want to check if other groups have no
        // more that 7.5% less total attributes. If they have, I change the chosen Index
        // to keep balance between the total attributes of each group (all groups similar size)
        double[] diffPercent = diffPercentCalc(indexMaxGap);
            //System.out.printf("diffPercent: %f, %f, %f, %f, %f\n",diffPercent[0],diffPercent[1],diffPercent[2],diffPercent[3],diffPercent[4]);
        int chosenIndex = chosenIndexCalc(indexMaxGap, diffPercent, 0.075);
        
        // if the chosen group is full, I change it to the one with less entities.
        // if a group is full, all other groups should be almost full too, therefore
        // it is not very important to add the entity according to its attributes,
        // as its addition would not change much the groups' balance
        chosenIndex = updateIndexIfGroupFull(chosenIndex);
        
        // now I finally have the index of the chosen group, so it is returned
        return chosenIndex;
    }
    private int twoEqual(char two, char one) {
    // two = char that is twice in attributeset, one = char that is once in attributeset
        // get the indexes of the attributes in the arrays
        int onePos = one - 'A';
        int twoPos = two - 'A';
        
        // array containing the sum of all attributes within each group. Has lenght 5
        int[] attributeTotals = attributeTotalsCalc();
        
        // get 2 attributeGaps arrays, each containing the difference between the given
        // attribute in a group and the other attributes' average in the same group (see above)
        double[] oneAttributeGaps = attributeGapsCalc(attributeTotals, onePos);
        double[] twoAttributeGaps = attributeGapsCalc(attributeTotals, twoPos);
        
        // each IndexMaxGap contains one of the indexes of AttributeGaps, the one with the biggest gap to fill
        int oneIndexMaxGap = indexMaxValue(oneAttributeGaps);
        int twoIndexMaxGap = indexMaxValue(twoAttributeGaps);
        
        // each MaxGap contains the max gap that a character has with all the other
        // characters in all groups. The bigger the number, the bigger the gap to fill
        double oneMaxGap = oneAttributeGaps[oneIndexMaxGap];      
        double twoMaxGap = twoAttributeGaps[twoIndexMaxGap];
        
        // by default, I choose the group with lacks the most the duplicate attribute
        int chosenIndex = twoIndexMaxGap;
        if (oneMaxGap > 2*twoMaxGap)
            chosenIndex = oneIndexMaxGap;
            // if the lack of the single attribute (attribute max gap) is greater than
            // twice the lack of the duplicate attribute, then more importance
            // is given to the group lacking the most the single attribute
        
        // if a group has at least 5% of difference in totattribute compared to others,
        // more importance is given to that group
        double[] diffPercent = diffPercentCalc(chosenIndex);
        chosenIndex = chosenIndexCalc(chosenIndex, diffPercent, 0.05);
        
        // see explanation above in threeEqual()
        chosenIndex = updateIndexIfGroupFull(chosenIndex);
        
        return chosenIndex;
    }
    private int noEqual(char first, char second, char third) {
        // as each of the three characters is unique, I need all three of them
        int firstPos = first - 'A';
        int secondPos = second - 'A';
        int thirdPos = third - 'A';

        // array containing the sum of all attributes within each group. Has lenght 5
        int[] attributeTotals = attributeTotalsCalc();

        // each AttributeGaps contains the difference between each group's average attribute quantity
        // except the one given ( (totAttributes - numberOfGivenAttribute) / 4 ) and the chosen
        // attribute's quantity. The bigger the number, the more unbalanced the attributes within a group
        double[] firstAttributeGaps = attributeGapsCalc(attributeTotals, firstPos);
        double[] secondAttributeGaps = attributeGapsCalc(attributeTotals, secondPos);
        double[] thirdAttributeGaps = attributeGapsCalc(attributeTotals, thirdPos);
        
        // each IndexMaxGap contains one of the indexes of AttributeGaps, the one with the biggest gap to fill
        int firstIndexMaxGap = indexMaxValue(firstAttributeGaps);
        int secondIndexMaxGap = indexMaxValue(secondAttributeGaps);
        int thirdIndexMaxGap = indexMaxValue(thirdAttributeGaps);
        
        // each MaxGap contains the max gap that a character has with all the other
        // characters in all groups. The bigger the number, the bigger the gap to fill
        double firstMaxGap = firstAttributeGaps[firstIndexMaxGap];      
        double secondMaxGap = secondAttributeGaps[secondIndexMaxGap];     
        double thirdMaxGap = thirdAttributeGaps[thirdIndexMaxGap];
        
        // choose the group with the most lacking attribute among the ones in attributeset
        int chosenIndex = firstIndexMaxGap;
        if (secondMaxGap > firstMaxGap)
            chosenIndex = secondIndexMaxGap;
        if (thirdMaxGap > secondMaxGap)
            chosenIndex = thirdIndexMaxGap;
        
        // if a group has at least 2.5% of difference in totattribute compared to others, choose that
        double[] diffPercent = diffPercentCalc(chosenIndex);
        chosenIndex = chosenIndexCalc(chosenIndex, diffPercent, 0.025);
        
        // see explanation above in threeEqual()
        chosenIndex = updateIndexIfGroupFull(chosenIndex);
        
        return chosenIndex;
    }
    
    
    private int[] attributeTotalsCalc() {
    // the method returns an array of ints containing the total amount of
    // attributes within each community group
        int[] attributesTotal = new int[5];
        
        for (int i=0; i<myGroups.size(); i++)
            attributesTotal[i] = myGroups.get(i).getTotAttributes();
        
        return attributesTotal;
    }
    
    private double[] attributeGapsCalc(int[] attributesTotal, int attributePos) {
    // this method return an array containing the difference between
    // the chosen attribute and the average of the other attributes within each group
    // EXAMPLE: if a group 'i' has: A:20  B:15  C:18  D:20  E:17 and the considered attribute is E,
    // then otherAttributes = (90 - 17) / 4) = 18.25 and attributeGaps[i] = 18.25 - 17 = 1.25
        double[] attributeGaps = new double[5];
        double otherAttributes;
        int i = 0;
        for (CommunityGroup cg: myGroups) {
            // get the average amount of other attributes in the group
            otherAttributes = ((double)(attributesTotal[i] - cg.getAttributes(attributePos))) / 4;
            // store the attributeGap
            attributeGaps[i] = otherAttributes - cg.getAttributes(attributePos);
            i++;
        }
        return attributeGaps;
    }
    
    private int indexMaxValue(double[] attributeGaps) {
    // returns the index (0 to 5) of the highest value (greatest gap) in attributeGaps
        int indexMaxGap = 0;
        for (int j=1; j<5; j++) {
            if (attributeGaps[j] > attributeGaps[indexMaxGap])
                indexMaxGap = j;
        }
        return indexMaxGap;
    }
    
    private double[] diffPercentCalc(int indexMaxGap) {
    // this method returns an array of doubles containing, in each position,
    // the percentage of each group's total attributes oven the chosen group's total attributes,
    // calculated as 1 - (groupTotAttributes / chosenGroupTotAttributes). The highest the
    // result, the bigger gap to be filled
        double[] diffPercent = new double[5];
        int totChosen = myGroups.get(indexMaxGap).getTotAttributes();
        if (totChosen == 0) totChosen++;
        for (int j=0; j<5; j++) {
            diffPercent[j] = 1 - (((double)myGroups.get(j).getTotAttributes()) / totChosen);
            //System.out.printf("1 - (%f / %f) = %f\n", myGroups.get(j).getTotAttributes(), totChosen, diffPercent[j]);
        }
        return diffPercent;
    }
    
    private int chosenIndexCalc(int indexMaxGap, double[] diffPercent, double percentage) {
    // given an allowed percentage (15%, 10% or 5%), this method will return the index
    // containing the highest percentage to be adjusted, if and only if there exist
    // a percentage higher than the allowed one. For example, given percentage = 15% and
    // diffPercent = {0.0, 0.0, -0.054, 0.0, 0.12}, the index will not be changed.
    // Instead, given percentage = 5% and diffPercent = {0.04, 0.08, 0.0, 0.08, 0.042},
    // the index will be changed to 1 (second value) (unless already chosen in indexMaxGap)
        double lastDiffPercent = 0;
        // if chosenIndex is not changed, the value of indexMaxGap is returned (therefore, no changes)
        int chosenIndex = indexMaxGap; 
        for (int j=0; j<5; j++) {
            if (diffPercent[j] > percentage && diffPercent[j] > lastDiffPercent) {
                lastDiffPercent = diffPercent[j];
                chosenIndex = j;
            }
        }
            //System.out.println("DiffPercent is "+diffPercent[0]+", "+diffPercent[1]+", "+diffPercent[2]+", "+diffPercent[3]+", "+diffPercent[4]);
            //if (chosenIndex != indexMaxGap) System.out.println("Index changed to: "+chosenIndex+" because its diffPercent is "+diffPercent[chosenIndex]);
        return chosenIndex;
    }
    
    private int updateIndexIfGroupFull(int chosenIndex) {
    // according to the algorithm's structure, groups increase their number of entities
    // with a maximum difference of 15% (maximum, therefore the difference is usually lower).
    // If a group is full than other groups are close to getting full too, therefore I choose
    // the one with fewer entities. Attributes' balance will not be altered significantly
    
        // execute only if the groups is full
        if (myGroups.get(chosenIndex).getCapacityLeft() == 0) {
            int indexSmaller = 0;
            for (int i=1; i<5; i++) { // start from 1, 0 is selected by default
                if (myGroups.get(i).getName() !=  'A'+chosenIndex) {
                    if (myGroups.get(i).getTotAttributes() < myGroups.get(indexSmaller).getTotAttributes())
                        indexSmaller = i;
                }
            }
            return indexSmaller;
        } // return either the passed argument or the the new selected index (indexSmaller)
        return chosenIndex;
    }
    
    public boolean allGroupsFull() {
    // return whether all groups are full
        boolean allFull = true;
        for (CommunityGroup cg: myGroups)
            if (cg.getCapacityLeft() != 0)
                allFull = false; // if at least one of the groups is not full, return false
        return allFull;
    }
    
    public void orderAllEntities() {
    // order alphabetically the entities within each group
        for (int i=0; i<5; i++)
            myGroups.get(i).orderEntities();
        System.out.println("Entities ordered successfully");
    }
    
    public void displayAllEntities(char group) {
    // display all Entities within the chosen CommunityGroup given its name
        for (CommunityGroup cg: myGroups)
            if (cg.getName() == group)
                displayEntities(cg);
    }
    
    public void displayEntities(CommunityGroup cg) {
        System.out.println();
        // display the group name
        System.out.print("Group "+cg.getName()+":\n\t\t");
        
        // display all entities in rows, 10 entities per row
        int i = 0; // keeps the count of entities displayed in eah row
        ListIterator<Entity> vols = cg.getEntities().listIterator();
        while (vols.hasNext()) {
            // print all but the last element (which requires a fullstop at the end)
            if (vols.nextIndex() < cg.getEntities().size()-1) {
                if (i<10) {
                    // If 10 entities haven't been displayed in a row yet, print on the same row
                    System.out.print(vols.next().getAttributeSet()+", ");
                    i++;
                } else {
                    // otherwise go onto a new line
                    System.out.println();
                    System.out.print("\t\t"+vols.next().getAttributeSet()+", ");
                    i = 1;
                }
            } else {
                // if 10 entities have been displayed on the last line already, go to new line
                if (i == 10) System.out.print("\n\t\t");
                // the last entity must be printed with a fullstop, no comma
                System.out.print(vols.next().getAttributeSet()+".");
                i++;
            }
        }
        System.out.println();
    }
    
    public void displayGroups() {
        System.out.println();
        for (CommunityGroup cg: myGroups) {
            // each output looks like "Group A:  A: 10  B: 10  C: 12   D: 9   E: 10    Total: 51    Entities: 17"
            System.out.println("Group "+cg.getName()+":   "+cg.getAttributesTotals()+"\tTotal: "+cg.getTotAttributes()+"\tEntities: "+cg.getTotAttributes()/3);
        }
    }
    
    public CommunityGroup getGroup(char name) {
    // this method returns the CommunityGroup with the specified name
        for (int i=0; i<myGroups.size(); i++)
            if (myGroups.get(i).getName() == name)
                return myGroups.get(i);
        return null; // the method will never return null because 'name' is verified to be in {A, B, C, D, E}
    }

    
    @Override
    public void addEntity(Entity vol) {
    // THE OVERWHELMING QUANTITY OF VARIABLES AND CALLS TO METHODS CAN BE CONFUSING.
    // THE CODE SHOULD BE READ WITH THE HELP OF THE WORD FILE EXPLAINING THE ALGORITHM
        
        // attributes[] will be used to determine which method to call
        char[] attributes = vol.getAttributeSet().toCharArray();
        
        // depending on whether the attributeSet has all equal attributes (AAA), two
        // equal attributes and one different (AAB) or all attributes different (ABC),
        // a different method is called and each different attribute is passed
        int chosenIndex;
        if ((attributes[0] == attributes[1]) && (attributes[1] == attributes[2])) {
            chosenIndex = threeEqual(attributes[0]);
            myGroups.get(chosenIndex).insertEntity(vol);
        } 
        else if (attributes[0] == attributes[1]) {
            chosenIndex = twoEqual(attributes[0], attributes[2]);
            myGroups.get(chosenIndex).insertEntity(vol);
        } 
        else if (attributes[0] == attributes[2]) {
            chosenIndex = twoEqual(attributes[0], attributes[1]);
            myGroups.get(chosenIndex).insertEntity(vol);
        } 
        else if (attributes[1] == attributes[2]) {
            chosenIndex = twoEqual(attributes[1], attributes[0]);
            myGroups.get(chosenIndex).insertEntity(vol);
        } 
        else {
            chosenIndex = noEqual(attributes[0], attributes[1], attributes[2]);
            myGroups.get(chosenIndex).insertEntity(vol);
        } 
    }

    @Override
    public void moveEntity(String attributeSet, CommunityGroup from, CommunityGroup to) {
    // this method simply deletes the entity from the "from" CommunityGroup
    // and adds the same entity to the "to" CommunityGroup
        deleteEntity(attributeSet, from);
        myGroups.get((int)(to.getName()-'A')).insertEntity(new Entity(attributeSet));
    }

    @Override
    public void deleteEntity(String attributeSet, CommunityGroup from) {
        //delete a entity with this attributeset from this CommunityGroup
        boolean eliminated = false;
            //System.out.println("AttributeSorter has received "+attributeSet+" to be eliminated.");
        for (int i=0; i<myGroups.size() && !eliminated; i++) {
                //System.out.println("Scanning group "+myGroups.get(i).getName());
            if (myGroups.get(i).getName() == from.getName()) {
                    //System.out.println("Group found, now eliminating...");
                myGroups.get(i).deleteEntity(attributeSet);
                eliminated = true;
            }
        }
    }

    @Override
    public void deleteAllEntities() {
    // a ListIterator on the Entity ArrayList of each CommunityGroups is
    // used to remove all Entity objects in the programme
        for (CommunityGroup gp: myGroups) {
            ListIterator li = gp.getEntities().listIterator();
            while (li.hasNext()) {
                li.next();
                li.remove();
            }
            // all the data of the CommunityGroup is ereased
            gp.setAllZero();
            System.out.println("Entities in group "+gp.getName()+" eliminated successfully");
        }      
    }

    @Override
    public ArrayList<CommunityGroup> getCommunityGroups() {
        //return an ArrayList of all the CommunityGroups in AttributeSorter
        return myGroups;
    }
}