1. Prima di avviare il backend, è necessario preparare il database:
- Apri pgAdmin (o il terminale psql).
- Crea un nuovo database chiamato esattamente: catalogo-film.
- Non serve creare tabelle manualmente, se ne occuperà Hibernate al primo avvio.

2. Configurazione Backend (Spring Boot)

Creare in src/main/resources/ il file application.properties in questo identico modo:
application.properties:
spring.application.name=catalogo-film
spring.datasource.url=jdbc:postgresql://localhost:5432/catalogo_film
spring.datasource.username= *IL TUO USERNAME*
spring.datasource.password= *LA TUA PASSWORD*
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

tmdb.api.key=9579a0d27a808bfbf8073387eefbddad
tmdb.api.url=https://api.themoviedb.org/3
tmdb.api.image.url=https://image.tmdb.org/t/p/w500

jwt.secret=miaChiaveSegretaSuperSicurissima2024CatalogoFilmProjectKey256bit!!
jwt.expiration=86400000

google.maps.api.key=AIzaSyAGWTr9oVHvICUVMgWrmdTbDPDy9CYceBs
groq.api.key=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


3. Al primo avvio del backend, il sistema popolerà automaticamente il database.
Utente Admin creato di default al primo avvio:
Email: admin@admin.com
Password: admin123
Usa queste credenziali per accedere subito al pannello di controllo e testare le funzionalità di gestione.

Una volta che il backend è attivo, non ti resta che lanciare Angular e visualizzare il sito web su http://localhost:4200
