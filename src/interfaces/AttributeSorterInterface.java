
package interfaces;

import models.Group;
import models.Entity;
import java.util.ArrayList;

public interface AttributeSorterInterface { 
    public void addEntity(Entity vol);
    public void moveEntity(String attributeSet, Group from, Group to);
    public void deleteEntity(String attributeSet, Group from);
    public void deleteAllEntities();
    public ArrayList<Group> getGroups();
}
