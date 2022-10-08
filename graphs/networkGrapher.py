import pandas as pd
import networkx as nx
from matplotlib import pyplot as plt
import sys

argumentsList   = sys.argv[1].split("|")
fromNodesList   = argumentsList[0].split(",")
toNodesList     = argumentsList[1].split(",")

teamsList       = sys.argv[2].split("|")
greenAgents     = [x for x in teamsList[0]]
greenTeams      = [x for x in teamsList[1]]

plt.figure(figsize=(10,5))
plt.rcParams["figure.autolayout"] = True

# Build datagrame with connections
df = pd.DataFrame({'from': fromNodesList, 'to': toNodesList})

# Assign nodes to blue or red team
color = pd.DataFrame({'ID': greenAgents, 'team': greenTeams})

# Build graph and arrange in order
G = nx.from_pandas_edgelist(df, 'from', 'to')
G.nodes()

colors = color.set_index('ID')
colors = colors.reindex(G.nodes())
colors['team']=pd.Categorical(colors['team'])
colors['team'].cat.codes

# ['red' if x == "0" else 'blue' for x in greenTeams]

nx.draw(G, with_labels=True, node_color=colors['team'].cat.codes, node_size=200, alpha=1, linewidths=20)

plt.show()