library(reshape2)
## Loading the data for the loading pahse
loaders <- as.data.frame(read.csv("proposed_benchmarks2.txt",header=F))
## Extracting the loading and total rows as columns
loaders <- dcast(loaders, V1 + V2 ~ V3)
## Inferring which is the time required to serialize the index
loaders$indexing <- loaders$total - loaders$loading
## Dropping the column for the total
loaders <- loaders[,-which(names(loaders) %in% c("total"))]
## Summing up the values required for both operands
loaders <- aggregate(cbind(loading, indexing) ~ V1, data = loaders, sum, na.rm = TRUE)
loaders <- loaders[-8,]

## Loading the data for joining
jointime <- as.data.frame(read.csv("eq_join_result2.txt",header=F))
loaders$indexing <- loaders$indexing + jointime