import json
import math
import networkx as nx
import pickle
import os.path

import Measure
import Worker
from Worker import *

root = '138875005'


def calculateSimilarity(ontology, measure_name, concept1_json, superConcept):
    # remove old pce
    if "pce" in ontology.get("sim"):
        ontology.get("sim").remove_node("pce")
        ontology.get("rel").remove_node("pce")

    ontology_graph_sim = preprocessing_pceSim(data_graph=ontology.get("sim"), pce_json=concept1_json)  # PCE -> FC
    ontology_graph_rel = preprocessing_pceSim(data_graph=ontology.get("rel"), pce_json=concept1_json)  # PCE -> FC

    ontology = {
        "rel": ontology_graph_rel,
        "sim": ontology_graph_sim,
    }

    if measure_name == "WuPalmer":
        result = Measure.calculate_relatedness_WuPalmer(ontology=ontology, concept1="pce", concept2=superConcept)
    elif measure_name == "ChoiKim":
        result = Measure.calculate_relatedness_ChoiKim(ontology=ontology, concept1="pce", concept2=superConcept)
    elif measure_name == "LeacockChodorow":
        result = Measure.calculate_relatedness_LeacockChodorow(ontology=ontology, concept1="pce", concept2=superConcept)
    elif measure_name == "BatetSanchezValls":
        result = Measure.calculate_relatedness_BatetSanchezValls(ontology=ontology, concept1="pce", concept2=superConcept)
    elif measure_name == "Resnik":
        result = Measure.calculate_relatedness_Resnik(ontology=ontology, concept1="pce", concept2=superConcept)
    elif measure_name == "Lin":
        result = Measure.calculate_relatedness_Lin(ontology=ontology, concept1="pce", concept2=superConcept)
    elif measure_name == "JiangConrathDissimilarity":
        result = Measure.calculate_relatedness_JiangConrathDissimilarity(ontology=ontology, concept1="pce",
                                                                         concept2=superConcept)
    else:
        result = "Wrong measure name!"

    return result


def preprocessing_pce(data_graph, pce_json):
    data_graph.add_node("pce")
    json_object = json.loads(pce_json)

    json_focusconcept = json_object.get("focusconcept")
    json_without = json_object.get("withoutRoleGroup")
    json_with = json_object.get("withRoleGroup")
    listAttributeRelation = []
    listValue = []
    listFocusconcept = []
    for j in json_focusconcept:
        listFocusconcept.append(j.get("code"))
    for j in json_without:
        listValue.append(j.get("valuecode"))
        listAttributeRelation.append(j.get("attributecode"))
    for j in json_without:
        listValue.append(j.get("valuecode"))
        listAttributeRelation.append(j.get("attributecode"))
    for j in json_with:
        json_rg = j.get("roleGroup")
        for element in json_rg:
            listValue.append(element.get("valuecode"))
            listAttributeRelation.append(element.get("attributecode"))
    # attribute relation
    for i in range(0, len(listValue)):
        if data_graph.has_edge("pce", str(listValue[i])):
            data_graph.add_edge("pce", str(listValue[i]),
                                label=data_graph["pce"][str(listValue[i])]["label"] + "+" + str(
                                    listAttributeRelation[i]))
        else:
            data_graph.add_edge("pce", str(listValue[i]), label=str(listAttributeRelation[i]))

    # is-a relation
    for fc in listFocusconcept:
        data_graph.add_edge("pce", str(fc), label="116680003")
    return data_graph


def preprocessing_pceSim(data_graph, pce_json):
    data_graph.add_node("pce")
    json_object = json.loads(pce_json)

    json_focusconcept = json_object.get("focusconcept")
    json_without = json_object.get("withoutRoleGroup")
    json_with = json_object.get("withRoleGroup")
    listAttributeRelation = []
    listValue = []
    listFocusconcept = []
    for j in json_focusconcept:
        listFocusconcept.append(j.get("code"))
    for j in json_without:
        listValue.append(j.get("valuecode"))
        listAttributeRelation.append(j.get("attributecode"))
    for j in json_without:
        listValue.append(j.get("valuecode"))
        listAttributeRelation.append(j.get("attributecode"))
    for j in json_with:
        json_rg = j.get("roleGroup")
        for element in json_rg:
            listValue.append(element.get("valuecode"))
            listAttributeRelation.append(element.get("attributecode"))
    # is-a relation
    for fc in listFocusconcept:
        data_graph.add_edge("pce", str(fc), label="116680003")
    return data_graph

def load_graph(file):
    with open(file, 'rb') as f:
        return pickle.load(f)


if __name__ == '__main__':

    file = open('similarity_calculation-pce-superconcept.txt', 'r')
#     Lines = file.readline()

    resultList = []

    # use only is-a relation to get lca of the hierarchy
    graph_sim = load_graph("snomed-20220930_dag_is-a")         # FC -> super concept
    graph_rel = load_graph("snomed-20220930_dag_relatedness")  # FC -> super concept

    ontology = {
        "rel": graph_rel,
        "sim": graph_sim,
    }

#     for line in file:
#         print(line)


    for line in file:
        parts = line.split("@")
        pce = parts[0]
        superConceptCandidate = parts[1]
        measureName = parts[2].replace("\n", "")
        # measureName = "WuPalmer"

        result = calculateSimilarity(ontology= ontology, measure_name=measureName, concept1_json=pce, superConcept=superConceptCandidate)

        resultList.append(superConceptCandidate + "@" + str(result))

    print(str(resultList))

    # measureName = "WuPalmer"
    # measureName = "ChoiKim"
    # measureName = "LeacockChodorow"
    # measureName = "BatetSanchezValls"
    # measureName = "Resnik"
    # measureName = "Lin"
    # measureName = "JiangConrathDissimilarity"
