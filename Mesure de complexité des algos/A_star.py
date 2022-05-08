from collections import deque

class Graph:
   
    def __init__(self, adjacency_list):
        self.adjacency_list = adjacency_list

    def get_neighbors(self, v):
        return self.adjacency_list[v]

    #On suppose que tous les poinds sont egaux à 1 
    def h(self, n):
        H = {
            'A': 1,
            'B': 1,
            'C': 1,
            'D': 1
        }

        return H[n]

    def a_star_algorithm(self, start_node, stop_node):
        # open_list est la liste des  nodes visités , maiss ses voiisons ne sont pas inspectés
        # closed_list est la liste des  nodes visités
        # and who's neighbors have been inspected
        open_list = set([start_node])
        closed_list = set([])

        # g contient les distances de start_node aux autres 

        # la valeur par défaut est +inf
        g = {}

        g[start_node] = 0

        # parents  contient la liste dadjacence des nodes 
        parents = {}
        parents[start_node] = start_node

        while len(open_list) > 0:
            n = None

            #  chercher le noeud avec un valeur minimal +evaluation
            for v in open_list:
                if n == None or g[v] + self.h(v) < g[n] + self.h(n):
                    n = v

            if n == None:
                print('Path does not exist!')
                return None

            # if the current node is the stop_node Si le noeud courant est  le stop_node , on reconstruit le  chemin de ce noeud vers strat_node
            if n == stop_node:
                reconst_path = []

                while parents[n] != n:
                    reconst_path.append(n)
                    n = parents[n]

                reconst_path.append(start_node)

                reconst_path.reverse()

                print('Path found: {}'.format(reconst_path))
                return reconst_path

            # Pour tous les voisins du noeud courant 
            for (m, weight) in self.get_neighbors(n):
               
                # S'il est pas dans les 2 listes (open et closed) , on l'ajoute à open_list and on note n son parent 
                if m not in open_list and m not in closed_list:
                    open_list.add(m)
                    parents[m] = n
                    g[m] = g[n] + weight

                
                #Sinon , on vèrifie si il est plus rapide de visiter n apres m et on maj le parent data et g data ,
                # Si le noeud est dans clode_list , on le place dans open_ist
                else:
                    if g[m] > g[n] + weight:
                        g[m] = g[n] + weight
                        parents[m] = n

                        if m in closed_list:
                            closed_list.remove(m)
                            open_list.add(m)

            #supprimer n d'open_list , et on l'ajoute a closed_list
            open_list.remove(n)
            closed_list.add(n)

        print('Path does not exist!')
        return None

#Test
adjacency_list = {
    'A': [('B', 1), ('C', 3), ('D', 7)],
    'B': [('D', 5)],
    'C': [('D', 12)]
    }
graph1 = Graph(adjacency_list)
graph1.a_star_algorithm('A', 'D')