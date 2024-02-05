<p align="center">
      <img src="https://i.ibb.co/Rv9PZWB/2024-01-16-01-01-10.jpg" width="1000">
</p>

<p align="center">
   <img src="https://img.shields.io/badge/Android%20Studio-brightgreen" alt="Android Studio">
   <img src="https://img.shields.io/badge/Version-v1.0.0-blue" alt="Version 1.0.0">
</p>

## About
"ArtHunter" - 🌍 An extraordinary adventure blending the real and virtual worlds in an exciting format. Search for artifacts and explore your city, participate in amazing events, and level up your character! 🏹

## Installation

To run the game, ensure that you have Android Studio version 2023.1.1 and MongoDB 2.1.1 installed. Please note that the game doesn't run on a local database due to emulator constraints

1. Install project 
```bash
   git clone https://github.com/Bronid/Arthunter-Project.git
```

2. In [local.propeties](https://github.com/Bronid/Arthunter-Project/blob/master/local.properties) set your GOOGLE_MAPS_API_KEY for Google Maps API

3. Open your port 27017 for MongoDB and change CONNECTION_STRING [here](https://github.com/Bronid/Arthunter-Project/blob/master/app/src/main/java/com/google/codelabs/buildyourfirstmap/database/MongoDBManager.kt)

4. Run this project via Android Studio

5. Done =)

## Rules of the Game

The rules of the game are quite simple. The actions take place in a post-apocalyptic world, where your main character must survive on the streets of your city!

There are several zones in the game:
- **Green Zones**: Safe areas where you can find items and traders.
- **Yellow Zones**: Dangerous areas where you may encounter mutants.
- **Red Zones**: Extremely hazardous zones with legendary monsters and the possibility to find artifacts, but the mortality rate is significantly higher.

If your character is not in any of these zones, they are in the so-called "Neutral" zone. Events can still occur here, but monsters are not as powerful, and there are fewer items to loot.

Upon entering the application, you must create your account and then create your character.

### Characteristics:
- **Strength**: Determines additional damage against monsters.
- **Perception**: Controls the zoom on the map. Higher perception allows the player to see more of their surroundings.
- **Constitution**: Increases health.

Your character has an inventory, and you can interact with items found during your raids.

Character death results in the loss of all items. Additionally, to embark on another raid, you must wait for the character to recover.

All events affecting the character are described at the top center of the map, with each event occurring every tick, equivalent to 10 seconds.

## Developers
- [Radusya](https://github.com/Radusya)
- [Bronid](https://github.com/Bronid)

## Screenshots
<p align="left">
<img src="https://i.ibb.co/RD2QBSB/login-page.png" alt="login_page" width="200" height="400">
<img src="https://i.ibb.co/jfwhpqj/main-page.png" alt="main_page" width="200" height="400">
</p>


