import networkx as nx

if __name__ == '__main__':
    file = open('similarity_calculation-pce-superconcept.txt', 'r')
    Lines = file.readlines()

    superConceptCandidate = []
    graphSim = nx.read_gpickle("snomed-20230430_dag_is-a")

    for line in Lines:
        superConceptCandidate.append(line)

    step = float('inf')
    candidates = []
    for s1 in superConceptCandidate:
        for s2 in superConceptCandidate:
            s1 = s1.replace("\n", "")
            s2 = s2.replace("\n", "")
            if s1 != s2:
                an1 = nx.descendants(graphSim, s1)
                an2 = nx.descendants(graphSim, s2)
                z = [an1, an2]
                z1 = set.intersection(*map(set, z))

                for commonNode in z1:
                    x = nx.shortest_path_length(graphSim, s1, commonNode) + nx.shortest_path_length(graphSim, s2,
                                                                                                    commonNode)
                    if step > x:
                        step = x
                        candidates = [commonNode]
                    elif step == x:
                        step = x
                        candidates.append(commonNode)

    result = list(set(candidates))
    print(str(result))
