Tetris FX
=========
A tetris clone written in JavaFx.

The number of columns and rows model does not correspond to the original Tetris.

#### Requirements

* Java 11 JRE or higher
* JavaFx SDK from Openjfx.io (https://gluonhq.com/products/javafx/)

#### Build Tool

* Maven 3

#### Build application with maven

```console
mvn clean install
```

#### Binary file

For the latest binary file take a look at the release section


#### Run Application

since java 11

```console
java --module-path "path\to\javafx-sdk-XX.X.X\lib" --add-modules javafx.controls,javafx.fxml  -jar tetrisFx-X.X-SNAPSHOT-uber.jar
```

![Screenshot](screenshot.png)

***

Rest Service
======
You can also control the game or access the model over the rest api under localhost:3000/api

| Request type   |      Path       | Body/Param |Description |
| ------------- | ---------------- | ---------- | ---------- |
| GET           |  `/field`        | None       | Get current field model |
| GET           |  `/gamestate`    | None       | Access the state of the game e.g started or not |
| GET           |  `/speed`        | None       | Get the current game speed |
| GET           |  `/level`        | None       | Get the current level |
| GET           |  `/finishedrows` | None       | Get processed rows    |
| POST          |  `/turn`         | see (1)    | control the tetris blocks |
| GET           |  `/bricklevel`   | None       | Get the information vector about how tall the wall was build |
| GET           |  `/blockdisplacement` | None  | Get the displacement information vector about how much empty space does the block need below |
---

(1)
```javascript
{
    cmd : 1
}
```

Following commands are available

| cmd |  Description |
| --- | ------------ |
| 1   |  Move left   |
| 2   |  Move right  |
| 3   |  Move down   |
| 4   |  Rotate left |
| 5   |  Rotate right|
| 6   |  Start game  |
| 7   |  Pause game  |
| 8   |  Stop game   |
---

Handling field & block data with SockJs
======

Create your SockJS eventbus on the client side with the following path 
* `yourTetrisFxAppIp:3000/api/eventbus`

#### Addresses

Field data model

* `event.websocket.fielddata`

Current moving block

* `event.websocket.block`

Future work
======
...

License
=======

Tetris and its logo is a trademark of The Tetris Company, LLC

[MIT](http://en.wikipedia.org/wiki/MIT_License) license © Lam
