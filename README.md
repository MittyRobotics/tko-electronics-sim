![TKO Circuit Simulator](https://github.com/MittyRobotics/tko-electronics-sim/blob/master/core/assets/img/logo/circuitsim.png)
---

FRC circuit simulator for electronics diagramming and rookie training.<br>
Created by **TKO Programming** ([Rohan Bansal](https://github.com/Rohan-Bansal) and [Andy Li](https://github.com/AndyLi23)).

![diagram](https://www.chiefdelphi.com/uploads/default/original/3X/4/b/4b3d36734577676d8211377c06b7b3f657fd1967.png)

## Features

- Circuit diagramming with FRC parts* (see subsection below)
- Creating, moving, editing devices and cables
- Saving/importing circuits as files
- Automated debugging for all errors
- Complete LED simulation
- 2D CAD-inspired GUI
- Keybinds, help menu, options menu, and other QOL features
- Native, can be run cross-platform
- Packages for easy installation

## Future Updates

- Curved cables
- Accurate scaling of parts on a blocks-per-inch grid for E-Plate diagramming
- Actual simulation of gauges, pistons, motors
- Recursive debugging of all connected components
- Timed competitive wiring games
- Color themes
- Commented code

## Installation

#### Official Packaging

Head over to the [releases](https://github.com/MittyRobotics/tko-electronics-sim/releases) page and download the binary for your platform. Directions for installation are also included there.

#### Building from Source

1. Clone the repository using git
2. Run `gradlew build` and then `gradlew run` in a console in the top directory

If you use Linux/Mac OS, run `./gradlew build` and then `./gradlew run` in a console in the top directory. If it says permission denied, do `chmod +x gradlew`.


## Device/Component List

#### Hardware
- Battery<br>
- Breaker<br>
- Compressor<br>
- Double WAGO<br>
- Falcon<br>
- Manifold (with double solenoids attached)<br>
- 775 Motor<br>
- NEO<br>
- Pneumatics Control Module (PCM)<br>
- Power Distribution Panel (PDP)<br>
- Voltage Regulator Module (VRM)<br>
- Piston<br>
- Pressure Switch<br>
- Radio<br>
- RoboRIO<br>
- Regulator + Gauge<br>
- Relief Valve Complex<br>
- Spark<br>
- Talon<br>
- Tank<br>
- T-Connector<br>

#### Cables

- Regular cables (4, 12, 18, 22 AWG)
- Crimped cables (4, 12, 18, 22 AWG)
- Ethernet
- Pneumatics tubing

---
