
package models;

import interfaces.GroupInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class Group implements GroupInterface {
    
    // each Group object has an ArrayList of Entities, a attributes[] array
    // containing the total amount of each attribute (updated every time a new entity
    // is added), a name (A, B, C, D or E) and the capacity left
    private ArrayList<Entity> myEntities;
    private int[] attributes;
    private final char name;
    private int capacityLeft;
    
    // constructor
    public Group(char Name, boolean load) {
        // attributes are all initialized to zero, name is given and capacityLeft is set to 500
        this.attributes = new int[] {0, 0, 0, 0, 0};
        this.name = Name;
        this.capacityLeft = 500;
        
        // if the user chose to load the previously saved entities from file,
        // the ArrayList of myEntities is given by the ArrayList returned by
        // the static method loadEntities in the FileHandler class
        if (load) {
            this.myEntities = FileHandler.loadEntities(name);
            initAttributes();
            // a success message informs the user that Entities were loaded
            System.out.println("Group "+name+" created and "+getTotAttributes()/3+
                    " entities added to it from file");
        }
        // otherwise, myEntities will be initialized as an empty ArrayList
        else {
            this.myEntities = new ArrayList<>();
            System.out.println("Group "+name+" created empty");
        }
        
    }
    
    private void initAttributes() {
    // after loading the Entities from file, the attributes of this Group
    // are initialized ( updateAttributes() ) and capacityLeft is decreased by 1 for
    // each Entity
        for (Entity entity: myEntities) {
            updateAttributes(entity.getAttributeSet());
            capacityLeft--;
        }
    }
    
    private void updateAttributes(String attributeSet) {
    // loop through each character of attributeSet and increase attributes[] accordingly

        for (int i=0; i<3; i++)
            attributes[attributeSet.charAt(i)-'A'] ++;
    }
    
    private void decreaseAttributes(String attributeSet) {
    // works similarly to updateAttributes(), see method above
        for (int i=0; i<3; i++)
            attributes[attributeSet.charAt(i)-'A'] --;
    }
    
    public void insertEntity(Entity vol) {
        // order the letters alphabetically
        String attributes = orderAttributes(vol.getAttributeSet());
        // add the entity to the ArrayList
        myEntities.add(vol);
        // update the total attributes of this Group
        updateAttributes(attributes);
        // print a success message
        System.out.println("Entity "+attributes+" added to group "+name);
        capacityLeft--; // decrease the capacity left
    }
    
    private String orderAttributes(String attributeSet) {
        // As the values are only 3, I order it with a series of 3 IFs instead
        // of with a sorting algorithm (that would require 2 nested for loops).
        // the 3 IFs work similarly to a Bubble Sort
        
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
    
    public void orderEntities() {
        // the Entity class implements Comparable, therefore Collection can be used.
        // Entities are ordered alphabetically (AAA, AAB, ABC, ADE, BAA, BAC, ...)
        Collections.sort(myEntities);
    }
    
    public void deleteEntity(String attributeSet) {
            //System.out.println("Preparing to delete "+attributeSet+" from group "+name);
        attributeSet = orderAttributes(attributeSet);
        
        // loops as long as a Entity is not deleted. A "myVol.hasNext()" condition
        // is not needed as the Entity to be removed is certainly inside the group
        boolean removed = false;
        for (Iterator<Entity> myVol = myEntities.iterator(); !removed;) {
            if (myVol.next().getAttributeSet().equals(attributeSet)) {
                myVol.remove();
                // each time a Entity is removed, the attributes of this Group
                // need to be updated (in negative)
                decreaseAttributes(attributeSet);
                removed = true; // stop the loop
                // success message
                System.out.println("Entity "+attributeSet+" removed from group "+name);
                // as a Entity is removed, the capacityLeft is increased
                capacityLeft++;
            }
        }
    }
    
    public int getTotAttributes() {
        // returns the sum of the values in attributes. This value, if divided by 3,
        // gives the amount of Entities in the Group (1 Entity = 3 Attributes)
        int sum = 0;
        for (int i=0; i<attributes.length; i++)
            sum += attributes[i];
        return sum;
    }
    
    public void setAllZero() {
    // when all Entities are removed (method in AttributeSorter) all the group's attributes
    // are inizialized to zero and the capacityLeft is reset to the maximum.
        this.attributes = new int[] {0, 0, 0, 0, 0};
        this.capacityLeft = 500;
    }
    
    // THE FOLLOWING GETTERs ARE SELF-EXPLANATORY
    
    public char getName() {return name;}
    
    public int[] getAttributes() {
        return attributes;
    }
    
    public int getAttributes(int pos) {
        return attributes[pos];
    }
    
    public int getCapacityLeft() {
        return capacityLeft;
    }
    
    public ArrayList<Entity> getEntities() {
        return myEntities;
    }
    
    
    @Override
    public int howManyEntities() {
        return myEntities.size();
    }
    
    @Override
    public String getAttributesTotals() {
        // return the total number of each attribute in a String, example:
        // A: 13    B: 20   C: 23   D: 5    E: 41
        return "\tA: "+attributes[0]+"\tB: "+attributes[1]+"\tC: "+
                attributes[2]+"\tD: "+attributes[3]+"\tE: "+attributes[4];
    }
}
