# FeedbackSettings

## Leverantör

Sundsvalls kommun

## Beskrivning
FeedbackSettings är en tjänst som hanterar aviseringsinställningar för privatpersoner och organisationer.


## Tekniska detaljer

### Starta tjänsten

|Miljövariabel|Beskrivning|
|---|---|
|**Databasinställningar**||
|`SPRING_DATASOURCE_URL`|JDBC-URL för anslutning till databas|
|`SPRING_DATASOURCE_USERNAME`|Användarnamn för anslutning till databas|
|`SPRING_DATASOURCE_PASSWORD`|Lösenord för anslutning till databas|


### Paketera och starta tjänsten
Applikationen kan paketeras genom:

```
./mvnw package
```
Kommandot skapar filen `api-feedbacksettings-<version>.jar` i katalogen `target`. Tjänsten kan nu köras genom kommandot `java -jar target/api-feedbacksettings-<version>.jar`.

### Bygga och starta med Docker
Exekvera följande kommando för att bygga en Docker-image:

```
docker build -f src/main/docker/Dockerfile -t api.sundsvall.se/ms-feedbacksettings:latest .
```

Exekvera följande kommando för att starta samma Docker-image i en container:

```
docker run -i --rm -p 8080:9090 api.sundsvall.se/ms-feedbacksettings
```

#### Kör applikationen lokalt
Exekvera följande kommando för att bygga och starta en container i sandbox mode:  

```
docker-compose -f src/main/docker/docker-compose-sandbox.yaml build && docker-compose -f src/main/docker/docker-compose-sandbox.yaml up
```


## 
Copyright (c) 2021 Sundsvalls kommun