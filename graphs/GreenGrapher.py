from ast import arg
import numpy as np 
import matplotlib.pyplot as plt 
import sys

# Create the graph for Uncertainty Over Time
# process cmd line arguments
# Syntax is:
# rightmost digit represents voting or not voting, 1 is voting, 0 is not voting
# following digits represent uncertainty * 10, must be integers
# so -0.896475 would be -8

X = range(-10, 11, 1)
for number in X:
    number /= 10

agents_voting = []
agents_voting = [0 for i in range(21)]

agents_not_voting = []
agents_not_voting = [0 for i in range(21)]

argumentsList = sys.argv[1].split(",")

for argument in argumentsList:
    argument = int(argument)

    if argument == 1:
        agents_voting[10] += 1

    elif argument == 0:
        agents_not_voting[10] += 1

    else:
        opinion = abs(argument) % 10
        
        if (argument < 0):
            argument = abs(argument) // 10
            argument *= -1 
        else:
            argument = argument // 10

        # voting
        if (opinion == 1):
            agents_voting[argument + 10] += 1
        #not voting
        else:
            agents_not_voting[argument + 10] += 1

  
X_axis = np.arange(len(X))
  
plt.bar(X_axis - 0.2, agents_voting, 0.4, label = 'Voting')
plt.bar(X_axis + 0.2, agents_not_voting, 0.4, label = 'Not Voting')
  
plt.xticks(X_axis, X)
plt.xlabel("Uncertainty")
plt.ylabel("Number of Agents")
plt.title("Evaluation of the Network Population")
plt.legend()
plt.show()