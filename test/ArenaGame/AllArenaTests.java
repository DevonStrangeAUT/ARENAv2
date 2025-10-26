/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ArenaGame;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Aggregates all ARENA tests into a single suite
 * Compatible with NetBeans "Run File" (JUnit 4 runner)
 * 
 * @author Fynn
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    ArenaGame.database.BattleLogDAOTest.class,
    ArenaGame.database.DatabaseManagerTest.class,
    ArenaGame.ItemTest.class,
    ArenaGame.InventoryTest.class,
    ArenaGame.PlayerGladiatorTest.class
})
public class AllArenaTests { }
