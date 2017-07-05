Music Recommendation
===================

----------
**Build instructions**
-------------
	1. mvn clean install
	2. mvn clean install docker:build


1- Simple Build
2- Create a image name as claudioed/music-recommendation


**Docker instructions**
-------------
CONTAINER PORT = 8010

1. Create a docker network

		docker network create music-recommendation-develop

2. Prepare the redis container

		docker run -d --name redis --net music-recommendation-develop  -p 6379:6379 -d redis

3. Run the music recommendation container

		docker run -d --name music-recommendation --net music-recommendation-develop -p 8010:8010 -e SPOTIFY_CLIENT_ID=<your client ID> -e SPOTIFY_CLIENT_SECRET=<your client secret> -e OPENWEATHER_API_KEY=<your open weather api key> -e REDIS_HOST=redis claudioed/music-recommendation

4. Check your containers


**Environment Variables**
-------------
  
 - SPOTIFY_CLIENT_ID= ** Your Spotify Client Id**
 - SPOTIFY_CLIENT_SECRET= ** Your Spotify Client Secret**
 - OPENWEATHER_API_KEY= ** Your Open Weather API KEY**
 - REDIS_HOST=  ** Your Redis Host**