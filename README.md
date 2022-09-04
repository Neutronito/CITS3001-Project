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

## Trial 3
Note that the above change was implemented, so our uncertainty decrease threshold is 0.15 and our uncertainty increase threshold is 0.05.
We started with 9 voting yes and 6 voting no, in a fairly certain environment.
Uncertainty interval was -1.0, 0.4.
We executed 1000 turns. 

### Trial 3 - 1
Changed to 7 voting and 8 not voting
Average uncertainty went from -0.2992 to 0.2094

### Trial 3 - 2
Changed to 10 voting and 5 not voting
Average uncertainty went from -0.4085 to -0.0928

### Trial 3 - 3
Changed to 7 voting and 8 not voting
Average uncertainty went from -0.3658 to 0.0420

### Trial 3 - 4
Changed to 8 voting and 7 not voting
Average uncertainty went from -0.3700 to 0.2943

### Trial 3 - 5
Changed to 8 voting and 7 not voting
Average uncertainty went from -0.3607 to 0.1203

### Results
It is slighly better, but I can still often see a lot of similar numbers, especially around 0.45 and 0.6. 0.45 = 0.6 - 0.15 which makes sense to me. As far as I can see, the passive increase is too predictable, but also, setting all agents to a flat value when flipping opinions is the real problem I think. Before changing that though, I will try the same test with a green sample size of 100. I believe the small sample size of 15 is also causing too much predictability over time.

## Trial 4
Note, as mentioned above, the sample size has now been changed to 100.
We started with 60 voting yes and 40 voting no, in a fairly certain environment.
Uncertainty interval was -1.0, 0.4.
We executed 1000 turns.

### Trial 4 - 1
Changed to 45 voting and 55 not voting
Average uncertainty went from -0.2781 to 0.5483

### Trial 4 - 2
Changed to 53 voting and 47 not voting
Average uncertainty went from -0.3380 to 0.5425

### Trial 4 - 3
Changed to 48 voting and 52 not voting
Average uncertainty went from -0.3094 to 0.5746

### Trial 4 - 4
Changed to 53 voting and 47 not voting
Average uncertainty went from -0.3094 to 0.4962

### Results
As we can see, the average still approaches 0.6 While I can see a much larger distribution in the uncertainties (i.e. they are not all hovering identical values anymore) it seems that all the agent's uncertainties tend to approach a very high value time. Only ~5 agents or less held a - uncertainty by the end of 1000 turns. I am not sure why this is being caused, but for now I will get rid of the flat value on flip and make it more random, and see how this impacts the network.
It should also be noted that the number of agents voting always drops and the number not voting always increases. I am not sure why this is the case, in a network where 60% wishes to vote, and the certainty is strong, I would expect it to either remain at 60% or increase. I believe the problem is that the uncertain nodes tend to dominate, which means that over time all the uncertain agents tend to pull down the certain agents more than the certain agents pull down the uncertain agents. This means that the population tends to become really uncertain and stay there. This is not ideal, but I will deal with it later. 

## Trial 5
Note, as mentioned above, we have now implemented a random decrease in uncertainty when flipping opinion
We started with 60 voting yes and 40 voting no, in a fairly certain environment.
Uncertainty interval was -1.0, 0.4.
We executed 1000 turns.

### Trial 5 - 1
Changed to 51 voting and 49 not voting
Average uncertainty went from -0.3139 to 0.3446

### Trial 5 - 2
Changed to 49 voting and 51 not voting
Average uncertainty went from -0.2916 to 0.4867

### Trial 5 - 3
Changed to 57 voting and 43 not voting
Average uncertainty went from -0.2709 to 0.3687

### Trial 5 - 4
Changed to 45 voting and 55 not voting
Average uncertainty went from -0.3055 to 0.4005

### Aside
It seems quite clear from the results and as mentioned above, the network tends to become very uncertain over time. However, I am still intrigued as to why the vote proportion always decreases. I am unsure if the high uncertainty over time means that the dominating opinion will lose over time, or if I have created some bias towards not voting. I will now test the proportion inverted, so 60 not voting and 40 voting. I would expect to see that the 60 not voting decreases.

### Trial 5 - 5
Changed to 50 voting and 50 not voting
Average uncertainty went from -0.2826 to 0.3988

### Trial 5 - 6
Changed to 50 voting and 50 not voting
Average uncertainty went from -0.2857 to 0.4076

### Trial 5 - 7
Changed to 44 voting and 56 not voting
Average uncertainty went from -0.3361 to 0.3327

### Trial 5 - 8
Changed to 51 voting and 49 not voting
Average uncertainty went from -0.3139 to 0.3446

### Results
Thankfully, when inverting the voting opinion, we see the same drop. It seems the dominant opinion loses out, dropping down. In fact, I will quickly test at 10,000 turns if the opinion levels out 50/50. This appears to indeed be the case. I am in fact not sure at all why this happens. Either way, I believe this is incorrect. I would expect that a population with relatively high certainty and a dominating opinion would see that opinion continue to dominate until the entire population is in agreement. I will focus stopping the uncertainty from rapidly increasing every time, by buffing the "pull up" value of two like minded agents interacting. I hope this will lead to the behaviour that I am expecting. 
I have also just realised that the other potential issue is a strong node is not increasing the uncertainty of an opposing node enough. I will disregard this for now and focus on changing one thing at a time. We can come back to this topic later. 

## Trial 6
Note, as mentioned above, I have now implemented a scale factor being multiplied to the increase when two agreeing nodes interact. Because I am buffing the pullup, the value being used is 1.2
We started with 60 voting yes and 40 voting no, in a fairly certain environment.
Uncertainty interval was -1.0, 0.4.
We executed 1000 turns.

### Trial 6 - 1
Changed to 3 voting and 97 not voting
Average uncertainty went from -0.2685 to -0.7464

### Trial 6 - 2
Changed to 3 voting and 97 not voting
Average uncertainty went from -0.2512 to -0.7409

### Trial 6 - 3
Changed to 0 voting and 100 not voting
Average uncertainty went from -0.2523 to -0.7342

### Trial 6 - 4
Changed to 100 voting and 0 not voting
Average uncertainty went from -0.2748 to -0.9133

### Trial 6 - 5
Changed to 99 voting and 1 not voting
Average uncertainty went from -0.2844 to -0.7540

### Aside
Things have now gotten very interesting. This idea seemed to work, but it seems that while an opinion will dominate, it swings either way, regardless of which opinion was initially dominant. For further analysis, I will single models with many intermediate snapshots, say every 100 turns, to see what is going on. Also, I believe the "swing" is based on the newtork arrangement as well, so despite trying to run away from it as much as I could, it appears I will now have to draw up the model and see how it looks. 