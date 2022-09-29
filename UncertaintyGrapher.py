import sys
import matplotlib.pyplot as plt
   
# process cmd line arguments
# Syntax is just each uncertainty value seperated by a comma
argumentsList = sys.argv[1].split(",")
uncertainties = []

for argument in argumentsList:
    uncertainties.append(float(argument))

rounds = list(range(0, len(uncertainties)))

  
plt.plot(rounds, uncertainties, color='red', marker='o')
plt.title('Uncertainty Over Time', fontsize=14)
plt.xlabel('Turn Number', fontsize=14)
plt.ylabel('Average Uncertainty', fontsize=14)
plt.grid(True)
plt.show()