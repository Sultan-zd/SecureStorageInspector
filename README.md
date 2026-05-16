# 🛡️ Secure Inspector - Audit de Sécurité Android

**Secure Inspector** est un outil d'audit de sécurité avancé pour Android. Il permet d'analyser à la fois la propre sécurité de l'application (Auto-Audit) et de scanner les autres applications installées sur l'appareil pour détecter des vulnérabilités critiques.

---

## 🌟 Points Forts
- **Conformité OWASP** : Basé sur les standards de sécurité mobile les plus rigoureux.
- **Analyse en Temps Réel** : Moteur de scan optimisé pour une réponse ultra-rapide.
- **Reporting Professionnel** : Exportation de rapports détaillés pour les équipes de sécurité.
- **Design Cyber-Security** : Interface moderne et intuitive sous Material 3.

---

## 🚀 Fonctionnalités principales

### 1. Auto-Audit (Garde du Corps)
L'application analyse ses propres dossiers pour s'assurer qu'aucune donnée sensible n'est exposée :
- Scan des **SharedPreferences** non chiffrées.
- Détection de bases de données **SQLite/Room** sans SQLCipher.
- Recherche de **Secrets Hardcodés** (JWT, API Keys, Emails) dans les fichiers internes et cache.

### 2. Scanner Système (Audit des tiers)
Identifie les failles chez les autres applications installées :
- Détection du mode **Debuggable** (Apps vulnérables au reverse engineering).
- Identification du flag **AllowBackup** (Risque de vol de données via USB/ADB).
- Analyse des **Permissions Risquées** (Accès SMS, Micro, Caméra non justifié).

### 3. Dashboard & Reporting
- Visualisation claire des vulnérabilités par niveau de sévérité.
- Recommandations techniques précises pour chaque faille trouvée.
- Partage du rapport d'audit complet en un clic.

---

## 🛠️ Stack Technique
- **Langage** : Java
- **UI** : Material Design 3, ViewBinding, CoordinatorLayout.
- **Architecture** : Modulaire et orientée objet pour une intégration facile en tant que SDK.
- **Sécurité** : Jetpack Security Crypto.

---

## 📥 Installation & Test
1. Clonez le dépôt.
2. Ouvrez dans **Android Studio**.
3. Lancez sur un émulateur ou un téléphone physique.
4. Cliquez sur **"Scan Application"** pour voir le moteur en action.

---
*Ce projet a été réalisé pour démontrer une expertise en sécurité Android et en développement d'outils de diagnostic professionnels.*
