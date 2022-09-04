# CITS3001-Project

# Green Network Interaction Testing

## Trial 1
#### Constant Parameters
We started with 9 voting yes and 6 voting no, in a fairly certain environment.
Uncertainty interval was -1.0, 0.4.
We executed 10 turns. 

### Trial 1 - 1
Changed to 11 voting and 4 not voting
Average uncertainty went from -0.2533 to 0.3343

### Trial 1 - 2
Changed to 6 voting and 9 not voting
Average uncertainty went from -0.2438 to 0.4520

### Trial 1 - 3
Changed to 9 voting and 6 not voting
Average uncertainty went from -0.3769 to 0.3724

### Trial 1 - 4
Changed to 10 voting and 5 not voting
Average uncertainty went from -0.2891 to 0.4026

### Trial 1 - 5
Changed to 3 voting and 12 not voting
Average uncertainty went from -0.4785 to 0.4704

### Trial 1 - 6
Changed to 7 voting and 8 not voting
Average uncertainty went from -0.2593 to 0.4368

### Trial 1 - 7
Changed to 8 voting and 7 not voting
Average uncertainty went from -0.2754 to 0.2836

### Trial 1 - 8
Changed to 7 voting and 8 not voting
Average uncertainty went from -0.3584 to 0.4588

### Results
The change in the voting proportion seems fairly random which is a good thing, as this probably means it is related to the way the network is layed out, which is ideal.
We can see that the average uncertainty always seems to increase. I believe this is caused by the fact that every time an agent's opinion flips, I set it to 0.6. This causes their uncertainty to hover around 0.6, and when several agents flip over a relatively short period of time, they all hover around 0.6. This causes the network to stagnate. Since the change in uncertainty in an interaction is based around the difference in uncertainty, when the agents all have around 0.6, the difference is 0, and so their uncertainty never changes. Over an even greater period, I believe that the uncertainties of all the green agents will approach 0.6 and freeze there. 

## Trial 2
#### Constant Parameters
We started with 9 voting yes and 6 voting no, in a fairly certain environment.
Uncertainty interval was -1.0, 0.4.
We executed 1000 turns. 

### Trial 2 - 1
Changed to 7 voting and 8 not voting
Average uncertainty went from -0.3437 to 0.6149

### Trial 2 - 2
Changed to 6 voting and 9 not voting
Average uncertainty went from -0.4202 to 0.6019

### Trial 2 - 3
Changed to 10 voting and 5 not voting
Average uncertainty went from -0.3791 to 0.6046

### Results
It is obvious by now that the above hypothesis was correct. To prevent this stagnation, I will say that if a node has not changed its uncertainty much (say within some deadband) then its uncertainty will decrease, the less its opinion has changed, the greater the decrease. This is somewhat true to real life, over time someones opinion can be considered to increase as they become more certain of themselves over time. 
This improvement may still cause stagnation, as if all nodes are hovering 0.6 then their uncertainties will all decrease more or less the same amount. However, since a normal network has a mixture of differing opinions, we should still see the interactions causing fluctuations in the network. If this proves to not be the case, a random element can be added to cause less predictable behaviour of the graph.

I will not try that yet but something else that can be done is not set every opinion to a flat value when it flips, but rather a random range. 