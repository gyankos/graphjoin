library(igraph)

## Loading the graph
gg <- read.graph("/media/giacomo/Biggus/com-friendster.ungraph.txt", format = "edgelist", n = 65608366, directed=F)
## Getting the nodes' degree