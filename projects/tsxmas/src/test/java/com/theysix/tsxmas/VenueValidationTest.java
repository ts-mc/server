package com.theysix.tsxmas;

import org.bukkit.World;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.lang.reflect.Proxy;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class VenueValidationTest {
    @TempDir Path dir;
    private static World world;
    @BeforeAll static void worldFacade() {
        world=(World)Proxy.newProxyInstance(World.class.getClassLoader(),new Class[]{World.class},(proxy,method,args)->switch(method.getName()) {
            case "getName" -> "tsxmas_christmas_2026";
            case "toString" -> "TestChristmasWorld";
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy==args[0];
            default -> null;
        });
    }
    @BeforeEach void setup() throws Exception {
        for(String n:List.of("config","database","events","messages","rewards","locations"))try(var in=getClass().getResourceAsStream("/"+n+".yml")){Files.copy(in,dir.resolve(n+".yml"));}
    }
    Settings venue(String... points) throws Exception {
        Settings settings=new Settings(dir);var y=settings.locations();
        y.set("boundary.configured",true);y.set("boundary.min-x",-100);y.set("boundary.max-x",100);y.set("boundary.min-z",-100);y.set("boundary.max-z",100);
        for(String point:points) {y.set("points."+point+".configured",true);y.set("points."+point+".x",0);y.set("points."+point+".y",70);y.set("points."+point+".z",0);}
        return settings;
    }
    @Test void calendarAndCommonChecksIgnoreUnconfiguredBuildAndOtherActivities() throws Exception {
        Settings s=venue("hub");assertTrue(s.validateWorld(null,name->world).isEmpty());assertTrue(s.validateWorld("calendar",name->world).isEmpty());
        assertTrue(s.validateWorld("hunt",name->world).stream().anyMatch(e->e.contains("forest-present")));
    }
    @Test void huntWorksWithAllBuildLocationsUnset() throws Exception {
        Settings s=venue("hub","forest-present","candy-cane","snowman");assertTrue(s.validateWorld("hunt",name->world).isEmpty());
        assertTrue(s.validateWorld("build",name->world).stream().anyMatch(e->e.contains("build")));
    }
    @Test void invalidPlotOnlyBlocksBuildContest() throws Exception {
        Settings s=venue("hub","build");var y=s.locations();y.set("plots.plot-one.configured",true);y.set("plots.plot-one.min-x",101);y.set("plots.plot-one.max-x",110);
        assertTrue(s.validateWorld("calendar",name->world).isEmpty());
        assertTrue(s.validateWorld("build",name->world).stream().anyMatch(e->e.contains("Plot outside boundary")));
    }
    @Test void buildStillRequiresARealPlot() throws Exception {
        Settings s=venue("hub","build");assertTrue(s.validateWorld("build",name->world).stream().anyMatch(e->e.contains("at least one configured plot")));
    }
}
