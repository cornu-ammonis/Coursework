"""
Your first graded programming assignment will be to write three functions which determine if a given relation on a 
given finite set is reflexive, symmetric, or transitive.  

- First, the user will list all the elements in the set, separating them by spaces.  My code will generate one list 
object that stores the set.  Example, 

Enter the set: 0 1 2
will create a set with the three elements 0, 1 and 2.

- Then, the user will list all of the pairs in the relation, again separating each term by a space.  My code will pair 
up terms and generate a list of relations.  Example,

Enter the relations: 0 0 1 2 2 2
will create the relations {0,0}, {1,2}, and {2,2}.

My code doesn't do any error-checking on the input so we should assume all input will be of the correct form.  So if you
 enter an odd number of terms in the relations line, or if you enter an item in a relation that isn't in the set, or if 
 you repeat a relation, the code will not catch these errors.
"""

# will contain a tuple representing each relation {x, y} as (x, y)
# this permits checking for the existence of a relation in constant time rather than 
# searching through rel each time, meaning that each of the functions (reflexive, symmetric, transitive) may
# scale linearly instead of quadratically  -- O(n) instead of O(n*n) !!
global relationsSet
relationsSet = set()

def buildRelationsSet(rel):
	global relationsSet

	for r in rel:
		relationsSet.add( (r[0], r[1]) )

def reflexive(set, rel):
	global relationsSet
	if len(relationsSet) is 0:
		buildRelationsSet(rel)

	for member in set:
		if (member, member) not in relationsSet:
			return False

	return True


def symmetric(set, rel):
	global relationsSet
	if len(relationsSet) is 0:
		buildRelationsSet(rel)


	return None
  #Fill in code here

def transitive(set, rel):
	return None
  #Fill in code here

 
#CHANGE NOTHING BELOW THIS LINE
#INPUT
set = [x for x in input("Enter the set:").split()]
prerel = [x for x in input("Enter the relations:").split()]
rel = []
for i in range(len(prerel)//2):
  rel.append([prerel[2*i], prerel[2*i+1]])
refl = reflexive(set, rel)
symm = symmetric(set, rel)
tran = transitive(set, rel)

#OUTPUT
print("Reflexive:", refl)
print("Symmetric:", symm)
print("Transitive", tran)