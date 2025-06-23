<h1 align="center">Hermes</h1>

<p align="center">
  <a href="https://opensource.org/licenses/MIT">
    <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="MIT License"/>
  </a>
    <img src='https://img.shields.io/badge/all_contributors-2-blue.svg?style=flat-square' />
  </a>
</p>

<p align="center">
    <a href="https://github.com/RedRedL/Hermes/labels/bug">Report Bug</a>
    &middot;
    <a href="https://github.com/RedRedL/Hermes/labels/enhancement">Request Feature</a>
  </p>
</div>

**HermesAPI** is a lightweight HTTP API mod for Fabric Minecraft servers that provides real-time server interaction through a simple HTTP interface.

## 🚀 Features

- Server side only
- Retrieve online player count
- Get list of online player names
- Real-time player join/leave events via SSE (Server-Sent Events)
- Simple HTTP interface using Netty
- Designed for Minecraft 1.21.1 with Fabric API

### 🛠️ Built With

* [![Java][Java-shield]][Java-url]
* [![Netty][Netty-shield]][Netty-url]
* [![Fabric][Fabric-shield]][Fabric-url]

<!-- Badge URLs -->
[Java-shield]: https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white
[Java-url]: https://www.java.com
[Netty-shield]: https://img.shields.io/badge/Netty-4.1-red?logo=netty&logoColor=white
[Netty-url]: https://netty.io
[Fabric-shield]: https://img.shields.io/badge/Fabric_API-1.21.1-1976d2?logo=fabric
[Fabric-url]: https://fabricmc.net


## 📦 Installation

1. Ensure [Fabric Loader](https://fabricmc.net/use/) is installed
2. Download the latest release from [Releases]
3. Place the `.jar` in your `mods` folder
4. Start your Fabric server

## 💻 Usage

The API runs on port `8080` by default:

| Endpoint                  | Description                             | Example Response            |
|---------------------------|-----------------------------------------|-----------------------------|
| `GET /players/count`      | Online player count                     | `{"count": 5}`              |
| `GET /players/names`      | Online player names                     | `{"players": ["redredl"]}`  |
| `GET /players/connections` | SSE stream of join/leave events         | `data: A player has joined!` |

### 🔄 Real-Time Player Events (SSE)

To listen for real-time events like player joins or leaves, connect to the following endpoint:

```bash
curl http://localhost:8080/players/connections
