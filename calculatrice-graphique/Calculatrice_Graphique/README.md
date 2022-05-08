# Calculatrice-Graphique

*Il s'agit d'une calculatrice graphique qui permet à la fois de tracer des fonctions entrées par l'utilisateur et d'effectuer des calculs.*

## Presentation

###  1.  Ce qu’on a voulu faire :

- Nous voulions faire une calculatrice graphique permettant d’afficher sans problème n’importe quelle fonction dans les bornes que l’on voulait. 
- Nous voulions faire une calculatrice esthétique où nous pouvions zoomer sur la courbe, dézoomer ,supprimer les courbes et faire un historique des fonctions tracées. 
- Nous voulions aussi faire un code simple avec le moins de classes possibles et exécutable avec Gradle pour éviter toute erreur.

 
###  2.  Ce que nous avons réussi à faire

- Nous avons réussis à réaliser ce projet avec le moins de classes possibles.
- Nous avions réussi à inclure Gradle dans le code pour exécuter le programme le plus simplement et le plus rapidement possible.
- Nous avons réussi à implémenter toutes les fonctionnalités que nous voulions, c’est à dire tracer toutes les fonctions même les fonctions discontinues, zoomer/dezoomer, supprimer, faire un historique ainsi qu'un slider permettant de zoomer comme nous le voulions. 

###  3.  Ce que nous n’avons pas réussi à faire 

- Réussir à dézoomer avec le slider mais le bouton Dezoom fonction.
- Modifier le pas selon le zoom.

###  4.  Les problèmes que nous avons rencontré durant ce projet 

- Charles Chickhani n’a pas pu accéder à Gitlab avec son pc sous Linux et n’a donc pas pu faire de commit. Il est donc passé par ses camarades pour faire des modifications. 
- Nous avons eu des bugs lorsque nous voulions supprimer les fonctions. La fonction se supprimait mais quand nous voulions retracer une fonction, l’intégrale de la fonction était tracée. Ce problème a été résolu. 
- Nous avons eu des problèmes pour le traçage de fonctions discontinues comme tan(x) ou 1/x par exemple. Ce probléme a été résolu.
- Nous avons quelques soucis avec l’addition et la soustraction de fonction negative. ce problème a été résolu.
- Bug d’affichage de notre police "Recoletta.ttf" avec Gradle. Affichange d'un message d'erreur dans le terminal

###  5. Notre organisation 

 Nous avons réussi à nous organiser de manière équitable et à communiquer en permanence. Afin de communiquer, nous utilisions Discord et principalement la messagerie Signal. Chacun a travaillé équitablement sur le projet, a aidé son camarade dans sa tâche. Le groupe avançait ensemble en étant en permanence en contact pour vérifier que personne bloque, ou qu’une personne allait trop vite. 
 Nael s’est occupé de la partie graphique. De plus, il s'est occupé de régler les bornes afin de tracer les fonctions selon les bons axes tout en gardant l'allure souhaitée au niveau du graphe.
Cela a permis d'etablir l'evaluation des graphes avec differentes fonctions et opérations.
 Jasen s’est occupe de la fonction draw() qui permet de dessiner la fonction entrée par l'utilisateur aussi bien les fonction discontinues. Il a aussi compilé le projet avec Gradle afin de rendre l'execution simplifiée et compatible sur tout type de machine.
 En effet, le dessin des graphes est realisé grâce au parser codé par Jihed et Charles.
De là, en codant ce parser, ils ont décomposé le problème selon le type de fonction demandé et l'opérateur séparant les termes de l'équation.
De là, Jihed a pu rajouter un zoom, symbolisé par un slider et Charles a rajouté un historique des fonctions tapées par l'utilisateur symbolisé par une petite fenêtre.




## Usage

### Pour lancer la calculatrice

clone https://gaufre.informatique.univ-paris-diderot.fr/zenkri/calculatrice-graphique-.git
    ```
1.  Rentrer sur le dossier du projet
    ```
    cd Calculatrice_Graphique
    ```

2.  Lancer Gradle et ses dépendances
    ```
    ./gradlew build
    ```

### Démarrer la calculatrice graphique
    ./gradlew run


###  Apparence de la calculatrice

![calll1](https://user-images.githubusercontent.com/39710677/116599703-b8d9ce80-a928-11eb-8137-76fd6548efab.jpg)

### 1. Saisie d'une fonction

![calll2](https://user-images.githubusercontent.com/39710677/116599817-dc047e00-a928-11eb-90c0-9464b2a91b4f.jpg)

### 2. Option de Zoom/Dezoom

![call3](https://user-images.githubusercontent.com/39710677/116599823-de66d800-a928-11eb-81ca-e54780601463.jpg)

### 3. Historique des fonctions saisies

![calll4](https://user-images.githubusercontent.com/39710677/116599831-e0309b80-a928-11eb-831c-1868287138d7.jpg)
