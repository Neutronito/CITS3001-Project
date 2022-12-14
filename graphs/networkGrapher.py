import pandas as pd
import networkx as nx
from matplotlib import pyplot as plt
import sys

plt.figure(figsize=(10,5))
plt.rcParams["figure.autolayout"] = True

# GREEN NETWORK -------------------------------------------------
plt.subplot(1, 2, 1)

argumentsList   = sys.argv[1].split("|")
fromNodesList   = argumentsList[0].split(",")
toNodesList     = argumentsList[1].split(",")

teamsList       = sys.argv[2].split("|")
greenAgents     = teamsList[0].split(",")
greenTeams      = [x for x in teamsList[1]]

# Build dataframe with connections
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

nx.draw(G, with_labels=True, node_color=colors['team'].cat.codes, node_size=200, alpha=1, linewidths=5)
plt.title("Green Agents Network", fontsize=14)
# GREEN NETWORK -------------------------------------------------

# GREY NETWORK --------------------------------------------------
plt.subplot(1, 2, 2)

greyTeams   = sys.argv[3]
greyAgents  = [x for x in range(len(greyTeams))]

G = nx.Graph()
G.add_nodes_from(greyAgents)
colors = ["red" if x == "0" else "blue" for x in greyTeams]

nx.draw(G, with_labels=True, node_color=colors, node_size=200, alpha=1, linewidths=10)
plt.title("Grey Agents Network", fontsize=14)
# GREY NETWORK --------------------------------------------------

plt.show()