import json
import sys
import pickle

import networkx as nx


def preprocessing_superConcept(superConcept, graph):
    data_graph = nx.MultiDiGraph()
    data_graph.add_node("dummy")
    l_superConcept = []
    edges = graph.edges(superConcept)
    for e in edges:
        label = graph[e[0]][e[1]]["label"]
        labelSplit = str(label).split("+")
        for l in labelSplit:
            if l != "116680003":
                data_graph.add_edge("dummy", l + "=" + e[1])
                l_superConcept.append(l)

    return data_graph, l_superConcept

def checkCase(json_with, l_SuperConcept):
    result = []
    l_attribute = []
    l_value = []
    l_boolean = []
    if len(json_with) == 1:
        result.append("case1")
        for j in json_with:
            json_rg = j.get("roleGroup")
            for element in json_rg:
                l_attribute.append(element.get("attributecode"))
                l_value.append(element.get("valuecode"))
    else:
        i = 0
        for j in json_with:
            json_rg = j.get("roleGroup")
            l_attribute.append([])
            l_value.append([])
            for element in json_rg:
                l_attribute[i].append(element.get("attributecode"))
                l_value[i].append(element.get("valuecode"))
            i = i + 1
        for l in l_attribute:
            c = [x for x in l_SuperConcept if x not in l]
            if len(c) == 0:
                l_boolean.append(True)
            else:
                l_boolean.append(False)
        result.append("case2")
        # if False not in l_boolean:
        #     result.append("case2a")
        # else:
        #     count = l_boolean.count(True)
        #     if count == 1:
        #         result.append("case2b")
        #     else:
        #         result.append("case2a")


    result.append(l_attribute)
    result.append(l_value)
    result.append(l_boolean)
    return result


def load_graph(file):
    with open(file, 'rb') as f:
        return pickle.load(f)


if __name__ == '__main__':
    graph_rel = load_graph("snomed-20230430_dag_relatedness")
    file = open('similarity_calculation-pce-superconcept.txt', 'r')
    Lines = file.readlines()

    resultList = []

    for line in Lines:
        parts = line.split("@")
        pce = parts[0]
        superConcept = parts[1]

        diff_resultWithout = []
        diff_resultWith = []

        graph_superConcept = preprocessing_superConcept(superConcept, graph_rel)[0]
        l_superConcept = preprocessing_superConcept(superConcept, graph_rel)[1]

        json_object = json.loads(pce)

        # ungrouped attributes
        json_without = json_object.get("withoutRoleGroup")
        graph_pce = nx.MultiDiGraph()
        if len(json_without) > 0:
           for e in json_without:
               valueCode = str(e.get("valuecode")).replace(".0", "")
               attributeCode = e.get("attributecode")
               graph_pce.add_edge("dummy", attributeCode + "=" + valueCode)
        diff_resultWithout.append(graph_pce.edges - graph_superConcept.edges)

        # grouped attributes
        json_with = json_object.get("withRoleGroup")
        listAttributeRelation = []
        listValue = []
        listFocusconcept = []
        b = checkCase(json_with, l_superConcept)
        l_attribute = b[1]
        l_value = b[2]
        l_boolean = b[3]

        if b[0] == "case1":
            graph_pce = nx.MultiDiGraph()
            for j in range(0, len(l_attribute)):
                graph_pce.add_edge("dummy", str(l_attribute[j]) + "=" + str(l_value[j]).replace(".0", ""))
            diff_resultWith.append(graph_pce.edges - graph_superConcept.edges)
        elif b[0] == "case2":
            for i in range(0, len(l_attribute)):
                graph_pce = nx.MultiDiGraph()
                l_diff = []
                for j in range(0, len(l_attribute[i])):
                    graph_pce.add_edge("dummy", str(l_attribute[i][j]) + "=" + str(l_value[i][j]).replace(".0", ""))

                x = graph_pce.edges - graph_superConcept.edges
                if len(x) == 0: # RG als ganzes muss passen - Bsp: open & closed fracture
                    l_diff.append(graph_pce.edges - graph_superConcept.edges)
                    diff_resultWith.append(l_diff)
                else:
                    count = l_boolean.count(True)
                    if count == 1:
                        l_diff.append(x)
                        diff_resultWith.append(l_diff)
                    else:
                        l_diff.append(graph_pce.edges)
                        diff_resultWith.append(l_diff)

        # elif b[0] == "case2b":
            # graph_pce = nx.MultiDiGraph()
            # for i in range(0, len(l_attribute)):
            #     for j in range(0, len(l_attribute[i])):
            #         graph_pce.add_edge("dummy", str(l_attribute[i][j]) + "=" + str(l_value[i][j]))
            # diff = graph_pce.edges - graph_superConcept.edges
            # diff_result.append(diff)

        result = []
        resultWithout = []
        resultWith = []

        if b[0] == "case1":
            resultWith.append([])
            for rg in diff_resultWith:
                for e in rg:
                    resultWith[0].append(str(e[1]))

        elif b[0] == "case2":
            i = 0
            for rg in diff_resultWith:
                for r in rg:
                    resultWith.append([])
                    for e in r:
                        resultWith[i].append(e[1])
                    i = i + 1

        for e in diff_resultWithout:
            for e1 in e:
                resultWithout.append(e1[1])

        result = [resultWithout, resultWith]

        print(result)