# MSPR_Bloc_4_Application_Mobile

Ce repository contient le code source de l'application mobile.

### Composition du groupe

- Adel Ould Ouelhadj
- Boudjemaa AKHAM
- Guillaume GAY
- Jean-Daniel SPADAZZI
- Sébastien GLORIES

### Pratiques de développement (gitflow) :

- La branche main est la branche de production.
- La branche preprod est la branche de pré-production.
- Les branches de développement sont les branches nomées par les noms des développeurs. Chaque
  développeur a sa branche
  de développement pour développer les fonctionnalités qui lui sont assignées.
- Les branches nomées fix sont les branches de correction de bugs. Elles sont créées à partir de la
  branche de
  développement du développeur qui a corrigé le bug.
- Les branches nomées test sont les branches de test. Elles sont créées à partir de la branche de
  développement du
  développeur qui a développé la fonctionnalité à tester.

### Prérequis :

- SDK Android (Android Studio) 
- JDK 

### Utilisation de l'application :

1- Cloner le repository : (Demander l'accès au repository à l'un des membres du groupe))

```bash
git clone https://github.com/BoudjemaaAKHAM/MSPR_Bloc_4_Mobile.git
```

2- Ouvrir le projet avec Android Studio

3- Lancer l'application sur un émulateur ou un appareil physique

### Documentation :

Voici quelques renseignement sur les éléments clés de l'application mobile

#### Menu:

BtnScan.setOnClickListener()
Initialise le scan pour le QR code

updateViews : cette méthode met à jour les vues de l'activité en fonction de l'utilisateur qui 
s'est connecté. Si l'utilisateur est un vendeur, elle active les boutons pour ajouter un 
produit et actualiser la liste et définit l'heure actuelle comme dernière heure d'actualisation. 

Si l'utilisateur n'est pas connecté, il désactive les boutons et définit la dernière heure de 
rafraîchissement sur null.

LogOut : cette méthode déconnecte l'utilisateur et met à jour les vues en conséquence. 
Il affiche également une boîte de dialogue d'alerte pour informer l'utilisateur qu'il a 
été déconnecté.

L'écouteur de clic sur le bouton du produit ouvre l'activité Produit, 
qui affiche une liste de produits grâce à la méthode
openProductActivity : cette méthode est appelée lorsque l'utilisateur souhaite ouvrir 
ProductActivity. Il place la liste des produits dans les extras de l'intention et y ajoute 
également le jeton.


#### Obtention des information auprés de l'API:

Encapsuler dans le thread startApiThread(String token) et déclencher par le scan ou le refresh.

La askAPI() méthode envoie une requête GET à une URL spécifiée avec un jeton de support dans
l'en-tête et lit la réponse dans un StringBuilder. Si la communication réussit,
il enregistre le nom d'utilisateur, l'état, le jeton et l'heure de la communication dans
les préférences partagées et met à jour les vues. Si la communication échoue,
il affiche une boîte de dialogue d'alerte avec un message d'erreur.

Il appelle ensuite la convertResponseToProducts()méthode avec les données de réponse, qui renvoie
une liste d'objets. La Convert()méthode prend la liste des objets et utilise la bibliothèque
Google Gson pour la convertir en un tableau d' Product objets, qui est ensuite utilisé pour
remplir un tableau à deux dimensions de données produit. Les produits sont eux aussi enregistré
en préférences

L'écouteur de clic sur le bouton d'actualisation met à jour la liste de produits en
démarrant un thread API.


#### Preference:

saveData : cette méthode enregistre les informations de connexion de l'utilisateur
(nom d'utilisateur, état de connexion, jeton et heure de la dernière actualisation) dans les
préférences partagées. Si l'utilisateur est un vendeur, il affiche un message toast indiquant
que le vendeur a été connecté. Si l'utilisateur n'est pas connecté, il affiche un message toast
indiquant que le vendeur a été déconnecté.

La loadData()méthode lit les données des préférences partagées et les affecte à certaines
variables d'instance.


#### Scan:

BtnScan.setOnClickListener()
Initialise le scan pour le QR code

onActivityResult : Cette méthode est appelée lorsque l'activité lancée pour scanner un QR code
renvoie un résultat. Il extrait le résultat de l'intention et démarre un nouveau thread pour
effectuer un appel d'API avec le résultat extrait.


#### Product Activity:

La méthode onCreate() est appelée lorsque l'activité est créée. Cette méthode récupère la 
liste des produits passée par l'activité précédente via la méthode 
getIntent().getStringArrayListExtra(). Ensuite, la méthode crée un ArrayAdapter pour 
afficher les éléments de la liste dans un ListView. Lorsque l'utilisateur clique sur 
un élément de la liste, la méthode onItemClick() est appelée, qui appelle une autre 
méthode Launch_ARactivity() pour démarrer une nouvelle activité.

La méthode Launch_ARactivity() crée une nouvelle intention pour démarrer une autre activité 
appelée HelloArActivity. L'intention transporte également une chaîne de caractères qui représente 
le nom du produit sélectionné.

Enfin, le bouton BtnHome est créé et associé à un écouteur qui appelle la méthode finish() pour 
fermer l'activité lorsque l'utilisateur clique dessus.


#### Activité AR:

1/ vérification de compatibilité -- prérequis ARCre

2/ accord utilisateur + déclaration
d'autorisation de manifeste Android pour la fonctionnalité CAMERA :
<uses-permission android:name="android.permission.CAMERA" />

3/ Session d'ARCore. La Session utilise la caméra du périphérique pour détecter les surfaces 
planes et les points d'intérêt dans l'environnement

4/ Instance de la classe SurfaceView. La SurfaceView est ajoutée à l'interface utilisateur de 
l'activité dans le fichier layout.

5/ Moteur de rendu "renderer" ARCore Le renderer ARCore utilise des shaders OpenGL pour 
dessiner des objets 3D sur la surface détectée.

6/ L'application crée un modèle 3D d'un cube, qui sera affiché sur la surface plane détectée. 
Le modèle est chargé à partir d'un fichier .obj.

7/ L'application utilise la classe Frame d'ARCore pour récupérer les informations de la caméra 
en temps réel, telles que la position, l'orientation et la résolution de l'image.

8/ L'application utilise la méthode hitTest de la classe Frame pour détecter si la caméra 
est actuellement pointée vers une surface plane dans l'environnement. Si une surface plane est 
détectée, l'application place le cube 3D sur cette surface en utilisant les coordonnées retournées 
par hitTest.

9/ Enfin, le renderer ARCore dessine le modèle du pointer sur la surface détectée, en lui combinant 
un fichier png pour lui associer une couleur. Le choix du fichier est stipuler par le numéro du 
produit ainsi ouvert dans l'activité.


