import random
from time import process_time as clock
import matplotlib.pyplot as plt
import sys

sys.setrecursionlimit(10000)


#TODO : Ajouter une courbe d'echange et de comparaisons pour chaque tri
#TODO : Changer le pas pour les puissances de p
#TODO : comparer à nlog(n) et n^2
#TODO : determiner la constante entre la courbe du tri bulles et le n^2

d = {"triBulles" : 0 , "triInsertion":1 , "triSelection" : 2}

#tableau d'echange
change = [[] for i in range(4)]
#tableau de comparaison
compare = [[] for i in range(4)]

#Genérateur des tableaux :


# randomTab prend des entiers n, a et b et renvoie un tableau
# aléatoire de taille n contenant des entiers compris entre
# les bornes a et b.
def randomTab(n, a, b):
    T = [0] * n
    for i in range(len(T)):
        x = random.randint(a, b)
        T[i] = x
    return T

# randomPerm prend en paramètre un entier n et renvoie une
# permutation aléatoire de longueur n
def randomPerm(n):
    T = [0] * n

    for p in range(n):
        T[p] = p + 1

        for i in range(len(T)):
            x = random.randint(i, n - 1)
            if (x != i):
                tmp = T[i]
                T[i] = T[x]
                T[x] = tmp

    return T

# creat_list_switch prend des entiers n, k et un booleen rev et
# effectue k échanges entre des positions aléatoires sur la
# liste des entiers de 1 à n si rev vaut False ou sur la
# liste des entiers n à 1 si rev vaut True.
def creat_list_switch(n, k, rev):
    T = [n - i for i in range(n)] if rev else [i + 1 for i in range(n)]
    for p in range(k):
        x = random.randint(1, n - 1)
        w = random.randint(1, n - 1)

        tmp = T[x]
        T[x] = T[w]
        T[w] = tmp

    return T




#TRI:


def triBulles(L):
    n = len(L)
    compar = 0
    cng = 0
    for i in range(n):
        for j in range(0, n-i-1):
            if L[j] > L[j+1] :
                L[j], L[j+1] = L[j+1], L[j]
                cng += 1
            compar +=1
    change[0].append(cng)
    compare[0].append(compar)

def triInsertion(L):
    n=len(L)
    compar = 0
    cng = 0
    for i in range(1,n):
        m,k = L[i],i
        while k>0 and L[k-1]>m:
            L[k],k = L[k-1],k-1
            compar += 2
            cng += 1
        L[k]=m
    change[1].append(cng)
    compare[1].append(compar)

def triSelection(T):
    n=len(T)
    compar = 0
    cng = 0
    for i in range(len(T)):
        min = i
        for j in range(i + 1, len(T)):
            if T[min] > T[j]:
                min = j
            compar += 1
        T[i], T[min] = T[min], T[i]
        cng += 1
    change[2].append(cng)
    compare[2].append(compar)
    return T

def triRapide(T,comparaison = 0):
    if T == []:
        return []
    else:
        pivot = T[0]
    t1 = []
    t2 = []
    compar = 0
    for x in T[1:]:
        if x < pivot:
            t1.append(x)
        else:
            t2.append(x)
        compar += 1
    return triRapide(t1) + [pivot] + triRapide(t2)

def triPython(T):
    T.sort()

#------------------------------------------------------------------
def mesure(algo, T):
    debut = clock()
    algo(T)
    return clock() - debut


def mesureMoyenne(algo, tableaux):
    return sum([mesure(algo, t[:]) for t in tableaux]) / len(tableaux)


couleurs = ['b', 'g', 'r', 'm', 'c', 'k', 'y', '#ff7f00', '.5', '#00ff7f', '#7f00ff', '#ff007f', '#7fff00', '#007fff']
marqueurs = ['o', '^', 's', '*', '+', 'd', 'x', '<', 'h', '>', '1', 'p', '2', 'H', '3', 'D', '4', 'v']


def courbes(algos, tableaux, styleLigne='-'):
    x = [t[0] for t in tableaux]
    for i, algo in enumerate(algos):
        print('Mesures en cours pour %s...' % algo.__name__)
        y = [mesureMoyenne(algo, t[1]) for t in tableaux]
        plt.plot(x, y, color=couleurs[i % len(couleurs)], marker=marqueurs[i % len(marqueurs)], linestyle=styleLigne,
                 label=algo.__name__)

#compare
def courbescc(algos,tableaux, styleLigne='-'):
    x = [t[0] for t in tableaux]
    for i, algo in enumerate(algos):
        if(algo.__name__ != "triRapide" and algo.__name__ != "triPython"):
            print('Mesures des comparaisons pour %s...' % algo.__name__)
            y = compare[d[algo.__name__]]
            moyenne = [sum(y[i:i+5])//5 for i in range(0,65,5)]
            plt.plot(x, moyenne, color=couleurs[i % len(couleurs)], marker=marqueurs[i % len(marqueurs)], linestyle=styleLigne,
                     label=(algo.__name__+" comapraisons"))
            y = change[d[algo.__name__]]
            moyenne = [sum(y[i:i+5])//5 for i in range(0,65,5)]
            plt.plot(x, moyenne, color=couleurs[(i) % len(couleurs)], marker=marqueurs[(i) % len(marqueurs)], linestyle=styleLigne,
                     label=(algo.__name__+" echange"))

#affiche le graph
def affiche(titre):
    plt.xlabel('taille du tableau')
    plt.ylabel('temps d\'execution')
    plt.legend(loc='upper left')
    plt.title(titre)
    plt.show()

def affichecc(titre):
    plt.xlabel('taille du tableau')
    plt.ylabel('Nombre d\'echange et de comparaisons')
    plt.legend(loc='upper left')
    plt.title(titre)
    plt.show()

#comparer les algos
def compareAlgos(algos):
    global change,compare
    taille = 1000  # taille maximale des tableaux à trier
    pas = 100  # pas entre les tailles des tableaux à trier
    ech = 5  # taille de l'échantillon pris pour faire la moyenne
    print()
    print("Comparaison à l'aide de randomPerm ")
    tableaux = [[i, [randomPerm(i) for j in range(ech)]] for i in [pow(2,k) for k in range(13)]]
    courbes(algos, tableaux, styleLigne='-')
    affiche("Comparaison à l'aide de randomPerm "+str(ech))
    plt.savefig('randomperm.png')
    plt.clf()
    courbescc(algos,tableaux)
    affichecc("Nombres de comparaisons et d'echange")
    plt.savefig("ccperm.png")
    plt.clf()
    print()
    #reinistialisation du tableau
    change = [[] for i in range(4)]
    compare = [[] for i in range(4)]
    print("Comparaison à l'aide de randomTab")
    tableaux = [[i, [randomTab(i, 0, i) for j in range(ech)]] for i in [pow(2,k) for k in range(13)]]
    courbes(algos, tableaux, styleLigne='-')
    affiche("Comparaison à l'aide de randomTab "+str(ech))
    plt.savefig('randomtab.png')
    plt.clf()
    courbescc(algos,tableaux)
    affichecc("Nombres de comparaisons et d'echange")
    plt.savefig("ccrandom.png")
    plt.clf()
    print()


#print(randomTab(5,0,5));
#print(randomPerm(8));
#print(creat_list(6,1,True));
#print(triInsertionEchange([2,5,4,3,0]))
algos = [triPython,triRapide,triBulles,triInsertion,triSelection]
compareAlgos(algos)
