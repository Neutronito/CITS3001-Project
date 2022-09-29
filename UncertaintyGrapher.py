import sys
import matplotlib.pyplot as plt
   
# process cmd line arguments
# Syntax is just each uncertainty value seperated by a comma
# The red uncertainties come first, and then the blue, seperated by a |
argumentsList = sys.argv[1].split("|")

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
plt.title('Uncertainty Over Time', fontsize=14)
plt.xlabel('Turn Number', fontsize=14)
plt.ylabel('Average Uncertainty', fontsize=14)
plt.grid(True)
plt.show()