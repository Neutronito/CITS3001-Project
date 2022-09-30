from ast import arg
import numpy as np 
import matplotlib.pyplot as plt 
import sys
  


# process cmd line arguments
# Syntax is: Each round is split by a _ while inside the two numbers are split by a ,

redCount = []
blueCount = []

argumentsList = sys.argv[1].split("_")

for argument in argumentsList:
    values = argument.split(",")

    print("First value is " + values[0])
    print("Second value is " + values[1])
    redCount.append(int(values[0]))
    blueCount.append(int(values[1]))



print(redCount)
print(blueCount)
  
X = list(range(0, len(redCount)))
  
X_axis = np.arange(len(X))
  
plt.bar(X_axis - 0.2, blueCount, 0.4, label = 'Blue Team')
plt.bar(X_axis + 0.2, redCount, 0.4, label = 'Red Team')
  
plt.xticks(X_axis, X)
plt.xlabel("Groups")
plt.ylabel("Number of Students")
plt.title("Number of Students in each group")
plt.legend()
plt.show()