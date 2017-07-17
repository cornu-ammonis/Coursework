def reflexive(set, rel):
 #Fill in code here

def symmetric(set, rel):
  #Fill in code here

def transitive(set, rel):
  #Fill in code here

 

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