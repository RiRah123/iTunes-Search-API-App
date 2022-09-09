# iTunes-Search-API-App
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Shell Script](https://img.shields.io/badge/shell_script-%23121011.svg?style=for-the-badge&logo=gnu-bash&logoColor=white)
![JSON](https://img.shields.io/badge/JSON-black?style=for-the-badge&logo=JSON%20web%20tokens)

## Project Summary
The `iTunes Search API App` is a GUI Application built in JavaFX, which displays a gallery of 20 artwork images based on the search query results of the iTunes Sarch API. When the application starts up, an initial set of images is displayed, corresponding to a provided default query. However, if the user would like a set of images relevant to a praticular query, then they may enter that query onto the provided text field and click the `Update Images` Button. Additionally, a play and pause mode is integrated into the app. By default, play mode is turned on, where a random image on screen will be replaced with another random image from the query result. If the user would turn off play mode, then they press the 'Play/Pause' button on the top right-hand corner.
<p align="center">
  <img src="https://camo.githubusercontent.com/cc67163e39e5cdcacc5fdbc9831dee842e2e6ed4fd86e1a2dba0a51335541427/68747470733a2f2f692e696d6775722e636f6d2f655568304e62462e706e67">
</p>

## How to Compile?
First, ensure that you Apache Maven downloaded onto your system. Following this, you can use the shell script command below to compile the app:
```
$ ./iTunes-Search-API-App.script.sh
```
If the shell script is not working, you can also use the following maven commands below:
```
$ mvn clean
$ mvn compile
```

## How to Run?
You can use the shell script command from earlier to run the app as well:
```
$ ./iTunes-Search-API-App.script.sh
```
If the shell script is not working, you can also use the following maven command below:
```
$ mvn run
```
