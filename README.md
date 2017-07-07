Music Recommendation
===================

----------
**Build instructions**
-------------
	1. mvn clean install
	2. mvn clean install docker:build


1- Build simples <br/>
2- Criação da imagem docker claudioed/music-recommendation


**Docker instructions**
-------------
CONTAINER PORT = 8010

1. Criação da rede docker

		docker network create music-recommendation-develop

2. Criação do container Redis

		docker run -d --name redis --net music-recommendation-develop  -p 6379:6379 -d redis

3. Criação do container da aplicação

		docker run -d --name music-recommendation --net music-recommendation-develop -p 8010:8010 -e SPOTIFY_CLIENT_ID=<your client ID> -e SPOTIFY_CLIENT_SECRET=<your client secret> -e OPENWEATHER_API_KEY=<your open weather api key> -e REDIS_HOST=redis claudioed/music-recommendation

4. Verifique se os containers estão rodando

        docker ps


**Environment Variables**
-------------
  
 - SPOTIFY_CLIENT_ID= ** Your Spotify Client Id**
 - SPOTIFY_CLIENT_SECRET= ** Your Spotify Client Secret**
 - OPENWEATHER_API_KEY= ** Your Open Weather API KEY**
 - REDIS_HOST=  ** Your Redis Host**


**Decisões de Arquitetura**
-------------

Foi utilizado a plataforma **Spring Boot (Versão 1.5.3)** para desenvolvimento da aplicação, como  web server foi utilizado o undertow. A plataforma foi escolhida por ser uma plataforma amplamente usada no mercado e também por ser padrão na empresa. Nesta versão não está disponível uma implementação **Reativa** que neste caso seria uma escolha ideal, por tratar-se de uma aplicação pode ter um grande volume de acessos.
Para aumentar o *throughput* da aplicação foi incluída uma camada de cache, este cache também tem intuito de servir como *fallback* caso os serviços externos **Spotify** e **OpenWeatherMaps** fiquem indisponíveis por algum tempo. 
Para garantir resiliência e tolerância a falha foi utilizado o framework **Hystrix**, este framework implementa o padrão Circuit Breaker muito utilizado na construção de aplicações que necessitam fazer integrações com *APIs externas* que é exatamente o caso estudado.
A implementação foi feita utilizando a biblioteca **javaRX** que ajuda a garantir que as chamadas sejam realizadas de forma *assíncrona* o que garante elasticidade ao serviço, algumas bibliotecas utilizadas no projeto não possuem implementação *assíncrona*,  nestes casos a biblioteca **javaRX** possui alguns *wrappers* ajudar na implementação. A utização da biblioteca também ajuda a garantir uma melhor legibilidade do código uma vez que umas das principais características do projeto são as chamadas encadeadas.
