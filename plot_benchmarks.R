library(ggplot2)
library(reshape2)
library(tidyverse)


##################################################################################
##################################################################################
##################################################################################
t <- read.csv("/home/giacomo/Scaricati/ideas_graphs/final_version/loading.csv")
names(t)[4] <- "t"
names(t)[3] <- "v"

breaks <- 10^(-10:10)
minor_breaks <- rep(1:9, 21)*(10^rep(-10:10, each=9))

t$Approach <- factor(t$Approach, levels=c("Proposed", "PostgreSQL+SQL", "Virtuoso+SPARQL", "Neo4J+Cypher"))
ggplot(t, aes(x=v, y=t, group=interaction(Approach,Dataset),shape=Dataset,color=Approach)) +
  scale_color_manual(values=c("#e66101", "#fdb863", "#b2abd2", "#5e3c99")) + 
  geom_line()+
  scale_x_log10(breaks = breaks, minor_breaks = minor_breaks) +
  scale_y_log10(breaks = breaks, minor_breaks = minor_breaks) +
  annotation_logticks() +
  geom_point(size=3)


##################################################################################
##################################################################################
##################################################################################
t <- read.csv("/home/giacomo/Scaricati/ideas_graphs/final_version/friendster.csv")
names(t)[4] <- "t"
names(t)[3] <- "v"

breaks <- 10^(-10:10)
minor_breaks <- rep(1:9, 21)*(10^rep(-10:10, each=9))

t$Approach <- factor(t$Approach, levels=c("Proposed", "PostgreSQL+SQL", "Virtuoso+SPARQL", "Neo4J+Cypher"))
t$Algorithm <- factor(t$Algorithm, levels=c("Conjunctive", "Disjunctive"))
ggplot(t, aes(x=v, y=t, group=interaction(Approach,Algorithm),shape=Algorithm,color=Approach)) +
  scale_color_manual(values=c("#e66101", "#fdb863", "#b2abd2", "#5e3c99")) + 
  geom_line()+
  scale_x_log10(breaks = breaks, minor_breaks = minor_breaks) +
  scale_y_log10(breaks = breaks, minor_breaks = minor_breaks) +
  annotation_logticks() +
  geom_point(size=3)



##################################################################################
##################################################################################
##################################################################################
t <- read.csv("/home/giacomo/Scaricati/ideas_graphs/final_version/kroneker.csv")
names(t)[4] <- "t"
names(t)[3] <- "v"

breaks <- 10^(-10:10)
minor_breaks <- rep(1:9, 21)*(10^rep(-10:10, each=9))

t$Approach <- factor(t$Approach, levels=c("Proposed", "PostgreSQL+SQL", "Virtuoso+SPARQL", "Neo4J+Cypher"))
t$Algorithm <- factor(t$Algorithm, levels=c("Conjunctive", "Disjunctive"))
ggplot(t, aes(x=v, y=t, group=interaction(Approach,Algorithm),shape=Algorithm,color=Approach)) +
  scale_color_manual(values=c("#e66101", "#fdb863", "#b2abd2", "#5e3c99")) + 
  geom_line()+
  scale_x_log10(breaks = breaks, minor_breaks = minor_breaks) +
  scale_y_log10(breaks = breaks, minor_breaks = minor_breaks) +
  annotation_logticks() +
  geom_point(size=3)
