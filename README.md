# TKO 1351 Circuit Simulator
<!--- ![TKO 1351 Circuit Simulator](core/assets/img/circuitsim.png) --->

An FRC circuit simulator used by our electronics department for rookie training and EPlate diagraming. Current features include a 2D interface similar to CAD which allows the user to create, move, and edit devices and cables. Circuits can also be saved as files and imported.

Created by [Andy Li](https://github.com/AndyLi23) and [Rohan Bansal](https://github.com/Rohan-Bansal).

### Features

- 2D CAD-inspired interface
- Creating, moving, editing devices and cables
- Saving/importing circuits as files
- Automated checking for errors (pneumatics excluded)
- Accurate circuit diagramming using real FRC parts: 
  - batteries, breakerers, compressors, falcon motors, manifolds, 775 motors, NEOs, pistons, PCMs, PDPs, pressure switches, radios, RoboRIOs, WAGO connectors, solenoids, sparks, talons, air tanks, VRMs, cables (Ethernet, tubing, all gauges used in FRC), EPlates
- Native, can be run anywhere

### Currently WIP

- Checking errors for pneumatics
- LED simulation
- Default cable colors
- Accurate scaling of parts on a blocks-per-inch grid

### Installation

#### Releases

Head over to the [releases](https://github.com/MittyRobotics/tko-electronics-sim/releases) page and download the binary for your platform. Directions for installation are also included there.

#### Gradle

1. Clone the repository using git
2. Run `gradlew build` and then `gradlew run` in a console in the top directory

If you use Linux/Mac OS, run `./gradlew build` and then `./gradlew run` in a console in the top directory. If it says permission denied, do `chmod +x gradlew`.
