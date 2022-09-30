from ast import arg
import numpy as np 
import matplotlib.pyplot as plt
import sys

plt.figure(figsize=(15,5))
# Create the graph for Opinion Over Time
# process cmd line arguments
# Syntax is: Each round is split by a _ while inside the two numbers are split by a ,
plt.subplot(1, 2, 1)

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
plt.xlabel("Simulation Round", fontsize=14)
plt.ylabel("Number of Green Agents", fontsize=14)
plt.title("Opinion Over Time", fontsize=14)
plt.legend()

# Create the graph for Uncertainty Over Time
# process cmd line arguments
# Syntax is just each uncertainty value seperated by a comma
# The red uncertainties come first, and then the blue, seperated by a |
plt.subplot(1, 2, 2)

argumentsList = sys.argv[2].split("|")
red_list = argumentsList[0].split(",")
blue_list = argumentsList[1].split(",")

red_uncertainties = []
blue_uncertainties = []

for argument in red_list:
    red_uncertainties.append(float(argument))

for argument in blue_list:
    blue_uncertainties.append(float(argument))

rounds = list(range(0, len(red_uncertainties)))

plt.plot(rounds, red_uncertainties, color='red', marker='o')
plt.plot(rounds, blue_uncertainties, color='blue', marker='o')
plt.title("Uncertainty Over Time", fontsize=14)
plt.xlabel("Simulation Round", fontsize=14)
plt.ylabel("Average Uncertainty", fontsize=14)
plt.grid(True)
plt.show()