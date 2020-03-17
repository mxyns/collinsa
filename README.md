# Collinsa
Moteur physique simulant des collisions entre formes géométriques primitives.

## Compiling and running
From `src` folder :  
   1. compile code : `javac -sourcepath . fr/insalyon/mxyns/collinsa/Collinsa.java`  
   2. run preset (here preset_2) : `java fr/insalyon/mxyns/collinsa/Collinsa -s preset_2`

## Available commands
   - `-s [preset_name]` to run a preset, check [EPreset](https://github.com/mxyns/collinsa/blob/master/src/fr/insalyon/mxyns/collinsa/presets/Preset.java) for a list of available presets
   - `--showAABB [false|true]` to enable the display of the entities' axis aligned bounding boxes
   - `--aabbColor (String from java.awt.Color fields' names)` to change their color (e.g. : `--aabbColor BLUE`)
   - `--showAxes [false|true]` to display the x and y axes
   - `--showChunks [false|true]` to display the chunks' bounds
   - `--chunkColor (String from java.awt.Color fields' names)` to change their color (works like aabbColor)
   - `--showWorldBounds [false|true]` to display the world's boundaries
   - `--scale (float)` to set the rendering scale in px/m 
   - `--realtime [false|true]` to define if the simulation must run in realtime or not (i.e. 1 sec IRL <=> 1 sec in simulation). requires higher `fpsp` rate
   - `--dt (int)` interval of time per timestep (if realtime = false)
   - `--fpsp (int)` change the refresh rates of number of simulation timestep (per sec)
   - `--fpsr (int)` change the refresh rates of number of images generated (per sec)
   - `--fpsd (int)` change the refresh rates of number of display refresh/repaint (per sec)
