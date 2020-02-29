# Quarkus Test Application project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the Application in Dev Mode

Start the REST services:
```
cd services
mvn quarkus:dev
```
Package and run the Angular User Interface:
```
cd ui
./package.sh
mvn quarkus:dev
```

## Creating a Native Executable

You can create a native executable using: `mvn package -Pnative`.

Or you can use Docker to build the native executable using: `mvn package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your binary: `./target/quarkus-testapp-project-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .
