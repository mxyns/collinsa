# Collinsa
Moteur physique simulant des collisions entre formes géométriques primitives.

## Compiling and running
From `src` folder :  
   1. compile code : `javac -sourcepath . fr/insalyon/mxyns/collinsa/Collinsa.java`  
   2. run preset (here preset_2) : `java fr/insalyon/mxyns/collinsa/Collinsa -s preset_2`

## Available commands
   - Running
      - `-s [preset_name]` to run a preset, check [EPreset](https://github.com/mxyns/collinsa/blob/master/src/fr/insalyon/mxyns/collinsa/presets/Preset.java) for a list of available presets
   
   - Engine Settings : Tickrates and World size
      - `--worldSize (Vec2f)` size of the world on the x and y coordinates
      - `--width (float)` world's width in meters (overwrites worldSize)
      - `--height (float)` world's height in meters (overwrites worldSize)
      - `--chunkCount (Vec2f)` number of chunks on the x and y coordinates
      - `--realtime [false|true]` to define if the simulation must run in realtime or not (i.e. 1 sec IRL <=> 1 sec in simulation). requires higher `fpsp` rate
      - `--dt (int)` interval of time per timestep (if realtime = false)
      - `--fpsp (int)` change the refresh rates of number of simulation timestep (per sec)
      - `--fpsr (int)` change the refresh rates of number of images generated (per sec)
      - `--fpsd (int)` change the refresh rates of number of display refresh/repaint (per sec)

   - Render Settings : Aesthetics & Debug
      - `--scale (float)` to set the rendering scale in px/m
      - `--forceScale (float)` to set the force rendering scale in px/N
      - `--showAABB [false|true]` to enable the display of the entities' axis aligned bounding boxes
      - `--showAxes [false|true]` to display the x and y axes
      - `--showChunks [false|true]` to display the chunks' bounds
      - `--showWorldBounds [false|true]` to display the world's boundaries
      - `--showForces [false|true]` to display forces
      -- `--wireframe [false|true]` to turn wireframe display (shapes not filled) on or off
      - `--aabbColor (Color)` to change their color (e.g. : `--aabbColor BLUE`)
      - `--chunkColor (Color)` to change their color (works like aabbColor)
      - `--worldBoundsColor (Color)` to change their color (works like aabbColor)
      - `--bgColor (Color)` to change the background's color (works like aabbColor)
      - `--useDebugColor [false|true]` to display use collision-related colors (AABB collision / confirmed collision / etc.)
   
   - Formatting : 
      - Color : field name from java.awt.Color or `rgb(red,green,blue)`
      - Vec2f : `Vec2f(x,y)` or `vec2f(x,y)`
      - int : `1`, `2`, `3`, etc.
      - float : `1f`, `2.13f`, etc.
      - double : `1d`, `1.0`, `2.13d`, etc.
      - boolean : `true` or `false`
       
## Upcoming tasks, fixes and issues
    
   The planned fixes and functionalities can be found on [my Trello board](https://trello.com/b/O4Y18YMN/collinsa)  
   Please report any problem or suggest any feature you'd like implemented with Github's [issues](https://github.com/mxyns/collinsa/issues) section 