
package models;

import interfaces.EntityInterface;


// implementing Comparable<Entity>, Entities can be ordered in Collections
public class Entity implements EntityInterface, Comparable<Entity> {
    
    // Each entity has a attributeSet. Its group is the Group that
    // contains the ArrayList of Entities containing this Entity
    private final String attributeSet;
    
    // constructor
    public Entity(String attributeset) {
        // order the given attributeSet alphabetically (DCA --> ACD)
        attributeset = Input.orderAttributes(attributeset);
        this.attributeSet = attributeset;
    }
    
    private int getIntValue() {
    // this method returns a number corresponding to the value of this
    // entity, which is used to make the compareTo method work.
        // Each letter A-E has a value 4-0, which is multiplied by its
        // position in attributeChar. Examples:
        // ABD = 4*10^2 + 3*10^1 + 1*10^0 = 500 + 40 + 1 = 541
        // CDE = 2*10^2 + 1*10^1 + 0*10^0 = 200 + 10 + 0 = 210
        // Ordering in descending order I get the entities orderd alphabetically
        int value = 0;
        for (int i=0; i<3; i++) {
            value += 4-(attributeSet.charAt(i)-'A') * Math.pow(10, 2-i);
        }
        return value;
    }
    
    @Override
    public int compareTo(Entity compareVol) {
        // from A to E..., descending order
        return compareVol.getIntValue() - this.getIntValue();
    }
 
      
    @Override
    public String getAttributeSet(){ return attributeSet; }
    
}