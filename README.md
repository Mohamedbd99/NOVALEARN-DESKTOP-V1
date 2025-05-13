# 🌟 NovaLearn - Version Desktop

NovaLearn Desktop est une application éducative développée en Java, dédiée aux élèves atteints de troubles d’apprentissage. Cette version permet une utilisation hors-ligne, orientée vers les enseignants, élèves, médecins et parents pour suivre, contribuer et interagir dans l’écosystème pédagogique proposé.


## 🚀 Objectifs du projet

NovaLearn est une application Desktop vise à offrir une solution éducative inclusive et accessible, permettant :


Aux enseignants de créer, publier et suivre des cours et exercices.

Aux élèves d’accéder à un espace personnalisé avec des quiz, ressources et progression.

Aux parents de consulter les résultats, soumettre des réclamations et accéder à des ressources de coaching.

Aux médecins de partager des conseils médicaux, publier des blogs et suivre les progrès des élèves.


## 👨‍💻 Technologies utilisées
 
Langage : Java 17+

IDE : IntelliJ IDEA / Eclipse

Framework GUI : JavaFX (Scène graphique, composants dynamiques)

Base de données : MySQL (via JDBC)



🧩 Fonctionnalités principales
🧑‍🏫 Espace Enseignant
Création de cours, exercices et évaluations.

Suivi des élèves et génération de rapports.

Intégration d’IA (modulaire) pour suggestions pédagogiques.

🧑‍🎓 Espace Élève
Visualisation des cours assignés.

Participation à des quiz interactifs.


🧑‍⚕️ Espace Médecin
Publication de blogs médicaux et vidéos de coaching.


Interaction avec les enseignants et les parents.

👨‍👩‍👧 Espace Parent
Accès aux performances des enfants.

Consultation de contenu de coaching.

Soumission de réclamations.

📄 Gestion de contenu
Génération de PDF (résultats, attestations).

Interface multi-rôle avec authentification sécurisée.


###📦 Installation & Exécution
###✅ Prérequis
Java JDK 17+

Maven 3.x

MySQL / MariaDB

JavaFX SDK

IDE Java (IntelliJ ou Eclipse)

wkhtmltopdf (si couplé à une génération HTML → PDF)

###⚙️ Étapes
Cloner le dépôt :

git clone https://github.com/votre-utilisateur/novalearn-desktop.git
cd novalearn-desktop

cd novalearn-desktop
Configurer la base de données :

Créer la BDD novalearn_desktop dans MySQL.

Renseigner les informations dans hibernate.cfg.xml.

Compiler le projet avec Maven :

bash
Copier le code
mvn clean install
Exécuter l’application :

Depuis l’IDE, lancer Main.java

Ou en ligne de commande :

bash
Copier le code
java -jar target/novalearn-desktop.jar


👤 Rôles et droits d'accès
Rôle	           Droits
Admin	           Gestion complète des comptes et modules
Enseignant	     Cours, quiz, suivi pédagogique
Élève	           Accès au contenu, quiz, historique personnel
Parent	         Suivi des enfants, réclamations
Médecin	          Ressources médicales, suivi, conseils


🗂️ Structure du projet
bash
Copier le code
novalearn-desktop/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/        # Contrôleurs JavaFX
│   │   │   ├── model/             # Entités (Hibernate)
│   │   │   ├── dao/               # Accès BDD
│   │   │   ├── service/           # Logique métier
│   │   │   └── Main.java          # Classe principale
│   │   └── resources/
│   │       ├── views/             # FXML ou fichiers UI
│   │       └── images/            # Assets
├── pom.xml                       # Fichier de configuration Maven
├── README.md
└── target/                       # Fichiers compilés


🧑‍🤝‍🧑 Équipe projet

👤 Yassine Gharsallah – Module utilisateurs / connexion

🧠 Mohamed Ben Dammer – Quiz et IA

📚 Cyrine Berrabah – Gestion des cours

🩺 Alioum Walaibe – Blog 

📬 Rami  Bouguerra – Réclamations 


## 📄 Licence

Ce projet est protégé. Toute copie, diffusion ou modification sans accord est interdite.
