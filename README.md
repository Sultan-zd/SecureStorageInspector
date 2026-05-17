# 🛡️ Secure Storage Suite & Inspector

**Secure Storage Suite** est une solution Android "all-in-one" alliant **Exploration de Stockage** et **Audit de Cybersécurité**. Elle permet aux développeurs et aux utilisateurs avancés de gérer leurs données locales tout en s'assurant qu'aucune information sensible (tokens, mots de passe) ne soit exposée.

---

## 🌟 Double Mission : Gestion & Sécurité

### 📂 Module 1 : Storage Explorer (Gestion)
Une interface immersive pour visualiser l'état de santé du stockage de l'application :
- **SharedPrefs Manager** : Comptabilisation des fichiers de préférences et détection des entrées sensibles.
- **Database Visualizer** : Identification des bases de données Room/SQLite et vérification de leur état de chiffrement.
- **Internal File Browser** : Exploration du dossier `files/` pour gérer les documents JSON, XML et TXT.
- **Cache Analytics** : Calculateur de taille de cache en temps réel pour une optimisation de l'espace disque.

### 🛡️ Module 2 : Security Auditor (Protection)
Un moteur d'analyse profond basé sur les standards OWASP Mobile :
- **Sensitive Data Classifier** : Reconnaissance automatique (via Regex avancées) de jetons **JWT**, clés d'API, identifiants et données de santé.
- **System-Wide Scan** : Analyse des autres applications du téléphone pour détecter les failles critiques (applications en mode `debuggable`, permissions excessives).
- **Rapport d'Audit Professionnel** : Exportation d'un rapport technique complet pour partage avec les équipes de développement.

---

## 🚀 Caractéristiques Professionnelles
- **UI Matérielle** : Conçu avec **Material Design 3**, incluant un dashboard dynamique et un mode sombre adaptatif.
- **Performance** : Moteur de scan optimisé avec retour visuel immédiat (Scan Duration Analytics).
- **Sécurité Jetpack** : Intègre les recommandations pour `EncryptedSharedPreferences` et `SQLCipher`.

## 🛠️ Stack Technique
- **Langage** : Java (Architecture orientée objet)
- **UI Framework** : Material 3, ViewBinding, CoordinatorLayout.
- **Moteur d'Audit** : Analyse statique par Pattern Matching (Regex).
- **Compatibilité** : Android 7.0+ (API 24).

---
*Ce projet démontre une expertise complète en architecture logicielle Android et en cybersécurité offensive/défensive.*
