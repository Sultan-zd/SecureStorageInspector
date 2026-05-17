# 📘 Documentation Technique - Secure Storage Suite

Ce document détaille l'implémentation technique de la suite de sécurité et de l'explorateur de stockage.

---

## 1. Moteur de Statistiques (`StorageInspector.java`)
Le moteur a été étendu pour fournir des données analytiques précises sur le stockage :
- **Calcul du Cache** : Utilisation d'une fonction récursive pour calculer la taille totale du dossier `cacheDir` et conversion dynamique (B, KB, MB).
- **Compteur de Fichiers** : Méthodes `getPrefsCount()`, `getDbCount()` et `getFilesCount()` qui interrogent directement le système de fichiers Android (`/data/data/...`).

## 2. Intelligence de Détection (Audit Engine)
L'application utilise une analyse statique et dynamique :
- **Analyse du Manifeste** : Vérification des flags système (`FLAG_DEBUGGABLE`, `FLAG_ALLOW_BACKUP`) via le `PackageManager`.
- **Analyse de Contenu (Pattern Matching)** : Utilisation de `Pattern` et `Matcher` pour identifier les structures de données sensibles comme les jetons JSON Web Token (JWT).

## 3. Interface Utilisateur de Haute Précision
- **Dashboard Grid** : Utilisation d'un `GridLayout` réactif avec des `MaterialCardView` pour l'explorateur.
- **Visual Feedback** : Intégration d'un `LinearProgressIndicator` pour simuler le temps de traitement lors d'un audit profond.
- **ViewBinding** : Implémentation systématique pour garantir la sécurité du code UI et éviter les fuites de mémoire.

## 4. Flux de Données
1. **Initialisation** : Au démarrage, l'app calcule les statistiques de stockage.
2. **Action Scan** : Le bouton déclenche le scan profond (Interne + Système).
3. **Mise à jour** : Les résultats sont injectés dans le `FindingsAdapter` et le dashboard de sécurité est mis à jour.
4. **Reporting** : Un `StringBuilder` compile tous les objets `Finding` pour créer le rapport final.

---
## 💡 Recommandations de Sécurité Intégrées
Le projet ne se contente pas de trouver des erreurs, il intègre une base de connaissances qui suggère l'utilisation de :
- **EncryptedSharedPreferences** pour les clés.
- **SQLCipher** pour les bases de données.
- **Android Keystore** pour les secrets cryptographiques.
