<p align="center">
  <a href="https://opensource.org/licenses/MIT">
    <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="MIT License" />
  </a>
</p>

# HermesAPI

**HermesAPI** is a lightweight HTTP API for Fabric Minecraft servers, developed by RedRedL.  
It provides useful endpoints to interact with the server in real-time.

## Features

- Retrieve online player count
- Get list of online player names
- Simple HTTP interface using Netty
- Designed for Minecraft 1.21.1 with Fabric API

## Installation

1. Make sure you have [Fabric Loader](https://fabricmc.net/use/) installed.
2. Download the latest HermesAPI mod `.jar` file.
3. Place the `.jar` into your `mods` folder.
4. Launch your Minecraft server or client with Fabric.

## Usage

Once the server is running, HermesAPI will start an HTTP server on port `8080` by default.  
You can access these endpoints:

- `/players/count` — Returns the number of online players.
- `/players/names` — Returns a list of online player names.

Example request:

```bash
curl http://localhost:8080/players/count
