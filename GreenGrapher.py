
import numpy as np 
import matplotlib.pyplot as plt 
  
X = range(-10, 11, 1)
for number in X:
    number /= 10

Ygirls = [10,20,20,40,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17]
Zboys = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,20,30,25,30]
  
X_axis = np.arange(len(X))
  
plt.bar(X_axis - 0.2, Ygirls, 0.4, label = 'Voting')
plt.bar(X_axis + 0.2, Zboys, 0.4, label = 'Not Voting')
  
plt.xticks(X_axis, X)
plt.xlabel("Uncertainty")
plt.ylabel("Number of Agents")
plt.title("Evaluation of the Network Population")
plt.legend()
plt.show()