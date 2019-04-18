# Group-Sorter
## _An organizer to sort entities with attributes among balanced groups_

Thig program sorts any number of entities with attributes among a set number of groups. The goal of the program is to keep the groups balanced at any point in time without knowing the total number of entities.

## Models
Each entity is defined by a set of three attributes and each attribute is a letter of the alphabet in the range _A~E_. For example, an entity could be AAA or ABD or BBE. 
Similarly, each group will have a number identifying the strength of each skill. For example, a group could have the following attributes: A 13; B 12; C 5; D 0; E 12. Balanced groups have similar values for the same attribute.

## Algorithm
Each time a new entitity is to be added to a community group,the addEntitity() method in AttributeSorter is called to perform the operation. The entitity is added to a chosen group based on the following two criteria:

1. The number of each attribute within a single group should be fairly well balanced
2. The total number of all attributes in each group should be fairly well balanced

An algorithm that takes both criteria into consideration is key in creating overall balanced groups.
The algorithm I’ve designed and implemented works in three different ways according to the type of AttributeSet of the entitity, which can be either:
- *AAA*: all three attributes are the same attribute
- *AAB*: two attributes are equal and one is different
- *ABC*: all attributes are different

### AAA Case
>The destination group is first chosen to satisfy criteria (2), keeping balance amongst different attributes within each group. Then, if a group significantly smaller (at least 15% smaller) than the chosen one exists, the smallest group is chosen in order to satisfy criteria (1). Finally, if the chosen group is full, the next smallest one is chosen, if available and not at maximum.

If all three of the given attributes are equal, the algorithm prioritises assigning the entitity to the group with the fewest of that attribute compared to other attributes. An array of doubles is used to store the difference between the given attribute’s number and the average of all the other attributes within each group in order to make the necessary comparisons. Each value of the array is calculated as:
$$ 
\forall i \in \{0, 1, 2, 3, 4\}, attrGaps_i = \frac{attrTotals_i - givenAttrQuantity_i}{4}
$$

Where attrTotals is an array of integers containing the total amount of attributes within each group. It is generated prior to attrGaps  and each value in it is calculated as:
$$ 
\forall i \in \{0, 1, 2, 3, 4\}, attrTotals_i = \sum_{j=0}^5 attrQuantity_i
$$

Every instance of community group uses an array of integers called attrQuantity[]  to store the total amount of each attribute within the group. For example, a community group with the following entities will have attrQuantity[] values of:
$$
AAA, ABC, CDE, ABE,BCC, BCD, DEE
$$
$$
attrQuantity = \{5, 4, 5, 3, 4\}
$$

Therefore, the array attributeGaps contains the “gaps” (differences) between the chosen attribute (if the entitity is BBB, the chosen attribute is B) and the average of the other attributes within each group (so the array has length of 5). The higher a value in the array, the more unbalanced the group corresponding to its index is. The index (0 to 4, for 5 gaps/groups) that contains the highest value corresponds to the index of the most unbalanced group in the myGroups ArrayList. That index is calculated in the programme with a method that returns the index with the highest value as follows:
$$ 
indexMaxGap=indexMaxValue(skillGaps)
$$

At this point, index_Max_Gap  contains the index that best satisfies criteria (2) for the allocation of the new entitity. The next step, according to criteria (1), is to check that there are no other groups with a total number of entities is less than the total number of entities in the chosen group by a margin of 7.5%. If such a group exists, then the group that has less total entities is chosen as the destination group. The chosen value is 7.5% because if a group is more than 7.5% smaller than other, then that group significantly needs new entities, no matter what set of attributes they have. 

For example, if a community group that has been chosen based on attributes (based on criteria (1)) has 400 entities and another group has less than 400*(1-7.5%) (= 370) entities, the sorting algorithm will  allocate a new entitity to the second group, even though the first has a higher need for that particular attribute.

Therefore, a new array of doubles is created to store the percentage of the difference between a group’s number of entities and the currently chosen group’s number of entities, considering the latter number to be 100%. The array is calculated as:
$$ 
\forall i \in \{0, 1, 2, 3, 4\}, diffPercent_i = 1 - \frac{groupEntities}{group_{chosen} Entities}
$$

Where chosen is the index chosen previously according to criteria (2) alone.

Once the array containing the percentages of differences is created, if one of its values is more than 0.15 (15%), then the index with the highest value is taken as the chosen index of the group in which the entitity must be added.
$$ 
chosenIndex = chosenIndexCalc(chosenIndex,diffPercent,0.075)
$$

One further step, common among all three sub-algorithms, is checking that the finally chosen group is not full. If the chosen group is full, as all other community groups are no more than 15% smaller than it, then all other groups are also close to being full. When groups are almost full, balancing different attributes within the same group does not have much significance anymore, as the impact of any addition of an entitity would be minimal. Therefore, the chosen group is the one with the least entities inside, to better satisfy criteria (1).hich the entitity must be added.
$$ 
chosenIndex = updateIndexIfGroupFull(chosenIndex)
$$

The choice process ends at this point: the group is chosen.

### AAB Case
>The destination group is first chosen in order to satisfy criteria (2), giving the duplicate attribute twice the importance of the single attribute. Then, if a smaller group (at least 10% smaller) than the chosen one exists, the smallest group is chosen in order to satisfy criteria (1). Finally, in case the chosen group is full, the next smallest one is chosen, if available and not already full

In this case, two arrays of doubles containing the differences between each of the two given attributes and the average of the other attributes in each group are created, with the same process as explained above for the AAA Case:
$$ 
\forall i \in \{0, 1, 2, 3, 4\}, oneAttrGaps_i = \frac{attrTotals_i - singleAttrsQuantity_i}{4}
$$
$$ 
\forall i \in \{0, 1, 2, 3, 4\}, twoAttrGaps_i = \frac{attrTotals_i - doubleAttrsQuantity_i}{4}
$$

Given these two arrays, two variables are used to store the index at which one of the AttributeGaps  arrays has the highest value. They contain the indexes of the two best groups in which to add the new entitity:
$$ 
oneIndexMaxGap = indexMaxValue(oneAttrGaps)
$$
$$ 
twoIndexMaxGap = indexMaxValue(twoAttrGaps)
$$

twoIndexMaxGap  is then assigned to the index of the group to which the entitity should be added. The two highest values among the gaps arrays are then compared. If the highest value corresponding to the single attribute is more than twice the value corresponding to the double attribute, the chosen group becomes the one in position oneIndexMaxGap. That is because, given:
$$ 
oneMaxGap = oneAttrGaps_{oneIndexMaxGap}
$$
$$ 
twoMaxGap = twoAttrGaps_{twoIndexMaxGap}
$$

If $oneMaxGap > 2*twoMaxGap$ , then the group at oneIndexMaxGap  needs the new entitity at least two times more than the group at twoIndexMaxGap.

A further calculation made to satisfy criteria (1). As in the previous case, each groups’ total amounts of entities is compared to the currently chosen group’s number of entities. If a group has 5+% fewer entities than the chosen group, the group with the fewest entities is chosen as the destination group. Compared to the previous case, a lower percentage is used here due to the importance given to criteria (1) over criteria (2). In this case, as the addition of the new entitity’s attributeset does not affect a group’s internal balance as dramatically as in the previous case, more importance is given to keeping the total number of entities among all groups balanced.
$$ 
chosenIndex = chosenIndexCalc(chosenIndex,diffPercent,0.05)
$$

The final step is to check that the chosen group is not full; if it is, the chosen group is the one with the least entities inside, to better satisfy criteria (1).
$$ 
chosenIndex = updateIndexIfGroupFull(chosenIndex)
$$

The choice process ends at this point: the group is chosen.

### ABC Case
>The destination group is first chosen in order to satisfy criteria (2), giving each attribute equal importance. Then, if an even smaller group (at least 2.5% smaller) than the chosen one exists, the smallest group is chosen in order to satisfy criteria (1). Finally, in case the chosen group is full, the next smallest group that is not full is chosen

As in the previous example, a attributeGaps array is generated for each given attribute (A, B and C) to represent the difference between the given attribute and the average of the other attributes within each group. Three arrays are created as:
$$ 
\forall i \in \{0, 1, 2, 3, 4\}, firstAttrGaps_i = \frac{attrTotals_i - firstAttrsQuantity_i}{4}
$$
$$ 
\forall i \in \{0, 1, 2, 3, 4\}, secondAttrGaps_i = \frac{attrTotals_i - secondAttrsQuantity_i}{4}
$$
$$ 
\forall i \in \{0, 1, 2, 3, 4\}, thirdAttrGaps_i = \frac{attrTotals_i - thirdAttrsQuantity_i}{4}
$$

Next, three integer variables are created using those arrays to store the index of the highest value of each array. Each integer represents the index of the best group to add the new entitity to considering each attribute individually:
$$ 
firstIndexMaxGap = indexMaxValue(firstAttrGaps)
$$
$$ 
secondIndexMaxGap = indexMaxValue(secondtAttrGaps)
$$
$$ 
thirdIndexMaxGap = indexMaxValue(thirdtAttrGaps)
$$

Combining these three variables and their corresponding arrays, the maximum value within each array can be found as:
$$ 
firstMaxGap = firstAttrGaps_{firstIndexMaxGap}
$$
$$ 
secondMaxGap = secondAttrGaps_{secondIndexMaxGap}
$$
$$ 
thirdMaxGap = thirdAttrGaps_{thirdIndexMaxGap}
$$

Now each of the three variables contains the maximum possible difference that the corresponding attribute has when compared to other attributes among all groups. The highest number is used to determine which attribute should be chosen for the destination group. Therefore, the chosen number becomes ...IndexMaxGap , where “...” is either first, second or third  based on which of the MaxGaps has the highest value. The group is then chosen according to criteria (2).

In order to satisfy criteria (1), the currently chosen group’s total amount of entities is compared with the number of entities in all other groups. If a group has 2.5+% less entities than the chosen group exists, the group with the least entities is chosen as the destination group. The lower percentage, compared to previous cases, is due to the importance given to criteria (1) over criteria (2). In this case, as the addition of the new entitity’s attributeset does not greatly affect a group’s internal balance, much more importance is given to keeping the total number of entities among all groups balanced.
$$ 
chosenIndex = chosenIndexCalc(chosenIndex,diffPercent,0.025)
$$

Finally, the chosen group is checked to see whether it is full or not. If it is full, the index of the chosen group becomes the index of any other group with the least amount of entities, if available.
$$ 
chosenIndex = updateIndexIfGroupFull(chosenIndex)
$$

The choice process ends at this point: the group is chosen.