1. Identifiants
	ZENKRI, Jihed, @zenkri, 21964937
	JEDDI, Skander, @jeddi, 21957008

2. Fonctionnalités
	Fonctionnalités implémentées entièrement:
		- reprint
		- eval
		- simp
	Fonctionnalités implémentées partiellement:
		- vars (nous n'avons pas pu implémenter correctement le cas d'une conditionnelle If avec l'union des deux branches)
		- sign (nous n'avons pas eu le temps d'implémenter la recherche d'un point fixe pour les signes dans les boucles While)

3. Compilation et exécution
	Compilation:
				make
	Exécution:
   				./run --reprint fichier.p (réaffichage du programme Polish contenu dans le fichier .p) 
      			./run --eval fichier.p (évaluation du programme Polish contenu dans le fichier .p) 
         		./run --simpl fichier.p (simplification du programme Polish contenu dans le fichier .p) 
            	./run --vars fichier.p (affichage de toutes les variables du programme Polish contenu dans le fichier .p et les variables risquant d'être écrites avant d'être déclarées) 
               	./run --sign fichier.p (affichage des signes potentiels de toutes les variables du programme Polish contenu dans le fichier .p) 

4. Découpage modulaire
	Les modules utilisés sont au nombre de 9, 1 par partie du projet à réaliser + 1 module utilitaire, 1 module pour les différents types introduits et le module de base:
		1. polish.ml: module contenant le main, qui se charge de l'exécution du sous-module approprié par rapport aux arguments de la ligne de commande.
		2. types.ml: ensemble de types introduits et utilisés dans le maniement des programmes Polish.
		3. common.ml: ensemble de fonctions utilitaires communes à plusieurs autres modules.
		4. read.ml: module responsable de la lecture d'un programme Polish - lecture du fichier physique et conversion des lignes en expressions du langage Polish.
		5. print.ml: module responsable de l'affichage d'un programme Polish - conversion et affichage d'expressions du langage Polish sous forme de chaînes de caractères.
		7. eval.ml: module responsable de l'évaluation d'un programme Polish - évaluation des expressions, des opérations et des conditions.
		8. simpl.ml: module responsable de la simplification d'un programme Polish - simplification des expressions arithmétiques et suppression des blocs de code morts.
		9. vars.ml: module responsable de l'affichage des variables contenues dans un programme Polish - collecte des variables et affichage des variables risquant d'être non-initialisées à leur première lecture.

5. Organisation du travail
	L'organisation du travail s'est passée sans problèmes. Nous avons réparti les tâches entre nous, 2 modules pour l'un et 2 pour l'autre, et les modules plus complexes ont été réalisé par nous deux en même temps.
	La première partie du rendu ne nous a pas posé de problèmes, il nous a fallu un temps de démarrage pour réaliser les fonctions de read mais une fois celles-ci faites, le reste était plus simple.
	La deuxième partie a été un peu plus complexe, nous nous y sommes pris un peu en retard par rapport à la difficulté des tâches à réaliser, aux alentours des vacances de fin d'année.
	Nous avons gardé tout au long du projet la même manière de fonctionner que pour le premier rendu, répartition des tâches plus simples équitablement entre nous et travail en commun sur les tâches plus complexes.

6. Misc
	Aucune question, remarque ou suggestion au moment de la rédaction du rapport.