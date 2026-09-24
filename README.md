<div align="center">

# Online Bookstore System

### Microservices Project

**Spring Boot · Spring Cloud · Eureka · Gateway · OpenFeign · Resilience4j**

</div>

---

# 1- Quels sont les services dans ce projet ?

Ce projet est un Online Bookstore développé avec Spring Boot et Spring Cloud, divisé en petits services indépendants.

|  # | Service Name       | Ce qu'il fait                                                                                                                          |   Port |
| -: | ------------------ | -------------------------------------------------------------------------------------------------------------------------------------- | -----: |
|  1 | `config-server`    | Contient tous les fichiers de configuration pour tous les autres services, au même endroit                                             | `8888` |
|  2 | `eureka-server`    | Un annuaire où chaque service s'enregistre pour que les autres puissent le trouver                                                     | `8761` |
|  3 | `api-gateway`      | La porte d'entrée unique pour toutes les requêtes. Les clients parlent uniquement à celui-ci, qui transmet les requêtes au bon service | `8080` |
|  4 | `product-service`  | Gère les livres : titre, auteur, prix, quantité en stock                                                                               | `8081` |
|  5 | `order-service`    | Gère la création des commandes, vérifie le stock avec product-service, réduit le stock après une commande                              | `8082` |
|  6 | `customer-service` | Gère des profils clients simples (nom, e-mail) , sans connexion ni sécurité                                                            | `8083` |

---

# 2- Outils et technologies utilisés

| Technologie                       | Utilisation                                                                                                 |
| --------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| **Java (17 ou 21)**               |                                                                                                             |
| **Spring Boot**                   | framework pour créer chaque service                                                                         |
| **Spring Cloud Config Server**    | configuration centralisée pour tous les services                                                            |
| **Netflix Eureka**                | service discovery (les services se trouvent automatiquement)                                                |
| **Spring Cloud Gateway**          | route les requêtes entrantes vers le bon service                                                            |
| **Spring Data JPA + H2 Database** | base de données en mémoire simple pour chaque service                                                       |
| **OpenFeign**                     | permet à Order Service d'appeler Product Service et Customer Service via HTTP facilement                    |
| **Resilience4j**                  | circuit breaker, donc si un service tombe en panne, les autres ne plantent pas , ils répondent correctement |
| **Maven**                         | utilisé pour compiler chaque projet                                                                         |

---

# 3- Comment les services communiquent entre eux

### Explication simple

Un client (comme Insomnia, Postman ou une application web) envoie une requête à l'API Gateway (port 8080).

Le Gateway consulte Eureka pour savoir où se trouve actuellement le bon service (son adresse peut changer, Eureka connaît toujours la dernière).

Le Gateway transmet la requête à ce service (Product, Order ou Customer).

Si Order Service a besoin d'informations de Product Service ou Customer Service, il interroge aussi Eureka et les appelle directement en utilisant Feign.

Chaque service, lorsqu'il démarre, va d'abord chercher ses paramètres sur le Config Server (informations de base de données, numéro de port, etc.) au lieu de les garder en dur dans le code.

### Ordre de démarrage

L'ordre de démarrage a donc toujours son importance :

```text
config-server      → doit démarrer en premier

eureka-server      → "l'annuaire" démarre ensuite

api-gateway        → s'enregistre dans Eureka

product-service    → s'enregistre dans Eureka

order-service      → s'enregistre dans Eureka

customer-service   → s'enregistre dans Eureka
```

---

# 4- Comment exécuter ce projet

Démarrez-les dans l'ordre indiqué ci-dessus, un par un, en attendant que chacun soit complètement lancé avant de démarrer le suivant.

Une fois que tout est en cours d'exécution, vous pouvez le tester à l'aide d'un outil comme Insomnia ou Postman, en envoyant des requêtes au Gateway sur le port 8080.

---

## Exemple : créer un client

```http
POST http://localhost:8080/api/customers
Content-Type: application/json
```

```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```

---

## Exemple : créer un produit

```http
POST http://localhost:8080/api/products
Content-Type: application/json
```

```json
{
  "title": "Clean Code",
  "author": "Robert Martin",
  "price": 29.99,
  "stockQuantity": 10
}
```

---

## Exemple : passer une commande

```http
POST http://localhost:8080/api/orders
Content-Type: application/json
```

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

---

# 5- Endpoints principaux

## Product Service (`/api/products`)

| Method   | Path                 | Description                |
| -------- | -------------------- | -------------------------- |
| `POST`   | `/api/products`      | Ajouter un nouveau produit |
| `GET`    | `/api/products`      | Lister tous les produits   |
| `GET`    | `/api/products/{id}` | Obtenir un produit         |
| `PUT`    | `/api/products/{id}` | Mettre à jour un produit   |
| `DELETE` | `/api/products/{id}` | Supprimer un produit       |

---

## Order Service (`/api/orders`)

| Method | Path                                | Description                              |
| ------ | ----------------------------------- | ---------------------------------------- |
| `POST` | `/api/orders`                       | Passer une nouvelle commande             |
| `GET`  | `/api/orders`                       | Lister toutes les commandes              |
| `GET`  | `/api/orders/{id}`                  | Obtenir une commande                     |
| `GET`  | `/api/orders/customer/{customerId}` | Obtenir toutes les commandes d'un client |

---

## Customer Service (`/api/customers`)

| Method   | Path                  | Description               |
| -------- | --------------------- | ------------------------- |
| `POST`   | `/api/customers`      | Ajouter un nouveau client |
| `GET`    | `/api/customers`      | Lister tous les clients   |
| `GET`    | `/api/customers/{id}` | Obtenir un client         |
| `PUT`    | `/api/customers/{id}` | Mettre à jour un client   |
| `DELETE` | `/api/customers/{id}` | Supprimer un client       |

---

# Bon à savoir

### Éléments utiles lors de la création

> Si Order Service essaie de passer une commande pour un client ou un produit qui n'existe pas, il renvoie une erreur propre au lieu de planter (`404 Not Found`).

> Si Order Service essaie de commander plus d'articles que le stock disponible, il renvoie `409 Conflict`.

> Si Product Service ou Customer Service est en panne, Order Service ne plante pas , grâce au circuit breaker (Resilience4j), il répond avec `503 Service Unavailable` au lieu d'une erreur désordonnée.

---

<div align="center">

**Online Bookstore System · Spring Boot · Spring Cloud**

</div>
