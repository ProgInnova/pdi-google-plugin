# PDI Google Steps plugin #

Development stage

### Authentication and service account ###
This plugin works with the [OAuth2 service accounts](https://developers.google.com/identity/protocols/OAuth2ServiceAccount) from Google, to create your own service account with a project at [Google APIs Console](https://console.developers.google.com)

### Building ###
This plugin is built with Maven
```shell
$ git clone https://jsanchez293@bitbucket.org/proginnova-s-a/pdi-google-plugin.git
$ cd pdi-google-plugin
$ mvn package
$ cp target/pdi-google-plugin-0.0.1-SNAPSHOT.zip ${PDI_FOLDER}/plugins/steps
$ cd ${PDI_FOLDER}/plugins/steps
$ unzip pdi-google-plugin-0.0.1-SNAPSHOT.zip
```

### Steps available ###

* [Copy files in Google Drive ![][1]](mdoc/drivecopystep.md)


[1]:src/main/resources/drivecopy.svg
