![TKO Circuit Simulator](https://github.com/MittyRobotics/tko-electronics-sim/blob/master/core/assets/img/logo/circuitsim.png)
---

FRC circuit simulator for electronics diagramming and rookie training.<br>
Created by **TKO Programming** ([Rohan Bansal](https://github.com/Rohan-Bansal) and [Andy Li](https://github.com/AndyLi23)) as a response to the COVID-19 pandemic.

![diagram](https://www.chiefdelphi.com/uploads/default/original/3X/4/b/4b3d36734577676d8211377c06b7b3f657fd1967.png)

## Features

- Circuit diagramming with FRC parts (see subsection below for list)
- Creating, moving, connecting devices with cables
- Saving/importing circuits as shareable files
- Automated debugging for wiring errors
- Simple LED simulation
- 2D CAD-inspired GUI
- Keybinds, help menu, options menu, and other QOL features
- Packages for easy installation (support for Windows, MacOS, Linux)

## Future Updates

- Enable resizing and fix bugs
- Future hardware (limelight, RSL, sensors, servos, etc.) 
- Welcome screen with basic instructions and more intuitive interface
- Twisted cables and groups of three cables for RoboRIO
- Accurate scaling of parts on a blocks-per-inch grid for E-Plate diagramming
- Actual simulation of gauges, pistons, motors
- Color themes
- Commented code

## Installation

#### Official Packaging

Head over to the [releases](https://github.com/MittyRobotics/tko-electronics-sim/releases) page and download the binary for your platform. Directions for installation can also be found there.

#### Building from Source

**We constantly commit partially finished new features or bug fixes, so we suggest that you use the latest stable release instead. Proceed at your own risk.**

1. Clone the repository using git
2. Run `gradlew build` and then `gradlew run` in a console in the top directory

If you use Linux/Mac OS, run `./gradlew build` and then `./gradlew run` in a console in the top directory. <br>
If it says permission denied, do `chmod +x gradlew`.

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
