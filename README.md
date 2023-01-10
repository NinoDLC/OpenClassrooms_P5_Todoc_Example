# Démo
![Démo Gif, wait for it !](https://github.com/NinoDLC/OpenClassrooms_P5_Todoc_Example/blob/master/example.gif)

# Sujets abordés / démontrés
 * Architecture MVVM (Model View ViewModel)
 * `LiveData` (en particulier `MediatorLiveData`)
 * Utilisation d'un `Fragment` comme vue (`TaskFragment`)
 * Utilisation d'une `DialogFragment` customisée (`AddTaskDialogFragment`)
 * `RecyclerView` (et son `ListAdapter` / `DiffItemCallback`)
 * Dialogue entre un `Adapter` et son `Fragment` (via l'interface `TaskListener`)
 * Tri de données via input utilisateur dans le `TaskViewModel`
 * Utilisation d'un Dao pour persister les différents `Task` grâce à une base de données (`TaskDao`)
 * Enums (`TaskSortingType`)
 * Singleton (`ViewModelFactory`)
 * Tests unitaires (TU) avec des `LiveData` et `ViewModels` (grâce à `Mockito`)
 * Code Coverage à 97% (`JaCoCo`)
 * Tests d'intégration poussés avec `Espresso` (tests UI et tests de base de données)

# Commandes utiles :
`./gradlew jacocoTestReport` pour générer le rapport de coverage des tests unitaires
`./gradlew connectedAndroidTest` pour lancer les tests d'intégration (avec un émulateur / device USB déjà lancé)
