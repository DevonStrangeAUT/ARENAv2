Arenav2 is a turn-based gladiator combat game built in Java.
It offers two modes of play: 
- CLI Mode: Classic text-based combat (terminal interface).
- GUI Mode: Swing-based graphical interface with menus, buttons, and live combat logs.

  Players create or select a gladiator, battle opponents and earn points stored in the local Derby database.

  Project Structure

  src/
 ├─ ArenaGame/
 │   ├─ Arena.java              ← CLI entry point
 │   ├─ BattleManager.java      ← Handles text-based battles
 │   ├─ PlayerGladiator.java    ← Player combat logic
 │   ├─ Gladiator.java          ← Enemy combat logic
 │   ├─ database/
 │   │    ├─ PlayerDAO.java
 │   │    ├─ GladiatorDAO.java
 │   │    └─ BattleLogDAO.java
 │   └─ ui/
 │        ├─ MainMenuFrame.java ← GUI entry point
 │        └─ BattleFrame.java   ← GUI battle system
 └─ ...

Running the game. 

Option 1: GUI Version: Run -> ArenaGame.ui.MainMenuFrame

Option 1: CLI Version: Run -> ArenaGame.Arena
