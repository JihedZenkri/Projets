from collections import defaultdict
import heapq

#Definition of a weighted graph object with adj list
class Graph:
    def __init__(self,n):
        self.vertices = n
        self.edges = defaultdict(list)
        self.distances = {}

    def addEdge(self, u, v, w):
        self.edges[u].append((v,w))


#Dijkstra algorithm for shortest path
def dijkstra(graph,start):
    distance = [float("infinity")]*graph.vertices
    distance[start] = 0
    priorityqueue = [(distance[start],start)]
    while(len(priorityqueue)>0):
        curr_cost,curr_node = heapq.heappop(priorityqueue)
        if(curr_cost>distance[curr_node]):
            continue
        for neighbor,w in graph.edges[curr_node]:
            new_cost = curr_cost+w
            if(new_cost<distance[neighbor]):
                distance[neighbor] = new_cost
                heapq.heappush(priorityqueue,(new_cost,neighbor))
    return distance

#Bellman-Ford algorithm for shortest path
def bellman_ford(graph,start):
    distance = [float("infinity")]*graph.vertices
    distance[start] = 0
    for _ in range(graph.vertices-1):
        for u in range(graph.vertices):
            if(u==start):
                continue
            for v,w in graph.edges[u]:
                if(distance[u]!=float("infinity") and distance[u]+w<distance[v]):
                    distance[v] = distance[u]+w
    return distance

#TODO : Delaunay Triangulation