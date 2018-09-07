Tetris FX
=========
A tetris clone written in JavaFx.

The number of columns and rows model does not correspond to the original Tetris.

#### Requirements

* Java 8 JDK or higher
* Maven 3

#### Build application with maven

```console
mvn clean install
```

#### Run Application

```console
java -jar tetris-1.0-SNAPSHOT-uber.jar
```

![Screenshot](screenshot.png)

***

Rest Service
======
You can also control the game over the rest api under localhost:3000/api

| Request type   |      Path       | Body/Param |Description |
| ------------- | ---------------- | ---------- | ---------- |
| GET           |  `/field`        | None       | Get current field model |
| GET           |  `/gamestate`    | None       | Access the state of the game e.g started or not |
| GET           |  `/speed`        | None       | Get the current game speed |
| GET           |  `/level`        | None       | Get the current level |
| GET           |  `/finishedrows` | None       | Get processed rows    |
| POST          |  `/turn`         | see (1)    | control the tetris blocks |

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

Handling data field with SockJs
======
Create your SockJS eventbus on the client side with following path 
* `yourTetrisFxAppIp:3000/api/eventbus`

Addresses

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
