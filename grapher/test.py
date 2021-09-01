import itertools

file = open("../app/log/tx_log.csv", "r")

groups = []
for line in file:
    line = line.rstrip("\n")
    data = line.split(',')[1:]
    for i in range(len(data)):
        data[i] = data[i].strip(" '[]\"")
        data[i] = int(data[i])
    groups.append(data)
    print(data)

for i in range(len(groups)):    
    groups[i] = list(itertools.combinations(groups[i] ,2))
    print(groups[i])


file.close()