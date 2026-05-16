# 📘 Documentation Technique - Secure Inspector

Ce document explique en détail le fonctionnement interne de l'application, la logique du code et le rôle de chaque composant.

---

## 1. Architecture Globale
L'application suit une architecture **modulaire** où la logique métier (le scan) est totalement séparée de l'interface utilisateur. Cela permet de transformer facilement le projet en une bibliothèque (SDK) réutilisable.

---

## 2. Description des Classes

### 📑 `Finding.java` (Le Modèle)
C'est la structure de base qui représente une vulnérabilité.
- **Attributs** : Titre, description, sévérité (CRITICAL, WARNING, INFO), catégorie (MANIFEST, DATABASE, etc.) et recommandation.
- **Rôle** : Standardiser chaque découverte pour qu'elle puisse être affichée et exportée de manière cohérente.

### 🧠 `StorageInspector.java` (Le Moteur)
C'est le cerveau de l'application. Il contient toute l'intelligence de détection.
- **`performFullAudit()`** : La méthode principale qui orchestre les différents types de scans.
- **Logiciel de Détection (Regex)** : Utilise des expressions régulières complexes pour identifier des motifs spécifiques (ex: `eyJ` pour les JWT, motifs pour les API Keys).
- **`scanSystemWideApps()`** : Utilise le `PackageManager` d'Android pour interroger les réglages de sécurité des autres applications.
- **Filtrage** : Le moteur ignore les fichiers binaires (images, .so) pour rester rapide et efficace.

### 📱 `MainActivity.java` (Le Contrôleur)
Gère l'interface et le cycle de vie de l'application.
- **ViewBinding** : Utilisé pour manipuler les éléments de l'interface de façon sécurisée (sans `findViewById`).
- **Handler & Looper** : Permettent de simuler un temps de traitement pour rendre l'expérience utilisateur plus réaliste et fluide.
- **Intent Share** : Gère l'exportation du rapport d'audit vers d'autres applications.

### 🎨 `FindingsAdapter.java` (L'Affichage)
Fait le lien entre les données brutes et l'interface visuelle.
- Transforme chaque objet `Finding` en une carte (Card) stylisée.
- Applique des couleurs et des icônes dynamiquement selon la sévérité du risque détecté.

---

## 3. Logique de Détection des Failles

### A. Analyse du Manifeste
Le code vérifie les drapeaux (flags) système :
- **`FLAG_DEBUGGABLE`** : Si vrai, l'app est vulnérable car on peut y attacher un débogueur.
- **`FLAG_ALLOW_BACKUP`** : Si vrai, les données privées peuvent être copiées via une simple commande USB.

### B. "Sensitive Data Classifier" (IA/Regex)
Au lieu d'une IA lourde, nous utilisons une "IA de règles" basée sur des patterns :
- **Tokens JWT** : Détecte les jetons de session actifs.
- **Secrets** : Cherche les mots-clés comme "password", "api_key", "secret" suivis de valeurs.
- **PII** : Identifie les emails pour protéger la vie privée des utilisateurs.

### C. Audit du Stockage
- **SharedPreferences** : Vérifie si le dossier `shared_prefs` contient des fichiers XML. Si oui, ils ne sont pas chiffrés.
- **Databases** : Vérifie la liste des bases de données. Si elles ne sont pas gérées par un système comme SQLCipher, elles sont considérées comme vulnérables.

---

## 4. Design & UX (User Experience)
- **Material 3** : Utilisation des derniers standards de Google pour les couleurs et les composants.
- **Dashboard** : Utilise un dégradé "Midnight Blue" pour donner un aspect technologique et sérieux.
- **Feedback Visuel** : Barres de progression et Toasts informent l'utilisateur de l'état du scan.

---

## 5. Pourquoi ce code est "Professionnel" ?
1. **Gestion d'erreurs** : Utilisation de blocs try-catch pour éviter les crashs lors de la lecture de fichiers.
2. **Performances** : Le scan est asynchrone (simulation) et filtré pour ne pas consommer trop de batterie ou de mémoire.
3. **Extensibilité** : On peut ajouter de nouvelles règles de détection en ajoutant simplement une nouvelle Regex dans `StorageInspector`.
