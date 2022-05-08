# Man ISSUES SOLVERS 

*Ceci est un manuel pour savoir comment tester le programme*

## Pour compiler :
```
./gradlew build
```

## Pour exécuter une option :
```
./gradlew run --args='[gitFolderToExecuteOn] option'
```

	par exemple :
	
```
./gradlew run --args='.. --justSaveConfigFile=true'
```

car le chemin relatif démarre dans ./cli/


## Les options disponibles  :
		--man=true
		--justSaveConfigFile=true
		--loadConfigFile=(nom du fichier à load)
		
		Les plugins :
		--addPlugin=countCommits
		--addPlugin=countMergesAccepted
	    --addPlugin=countLines
	    --addPlugin=percentageLine 
	    --addPlugin=DatePerAuthor
		--addPlugin=percentageCommits
		--addPlugin=countBranches
		--addPlugin=allCommitsPerDate

		Les options d'appel de git log (modifient les commits analysés par les plugin) :
		--logOption=--no-merges
		--logOption=--since:[date]
		--logOption=--after:[date]
		--logOption=--until:[date]
		--logOption=--before:[date]
		(Les formats possibles pour la date: DD/MM/YY, MM/DD/YY, YY/MM/DD, YY/DD/MM et YYYY au lieu de YY)
		--logOption=--branche:[nom de la branche]

		Autres options :
		--addOption=htmlToNav
		--addOption=strict (change la façon de compter les lignes)
		--addOption=outputToFile:[nomDeFichier]

pour avoir tous les plugins avec leurs graphs   : 
		--fullPack=true 

pour avoir l'evolution des commits par rapport au temps : 
		--evolutionInfo=true			       

les options à venir :
		--addPlugin=PercentageCommits
