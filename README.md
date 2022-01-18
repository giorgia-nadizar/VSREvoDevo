# VSREvoDevo
This repository hosts the code used in the paper "On the Schedule for Morphological Development of Evolved Modular Soft Robots".
The scope of the paper concerned the investigation of the effects of different developmental schedules on evolved Voxel-Based Soft Robots (VSRs), experimenting with various representations for development functions and robotic controllers.

## Content
The content of this repository is organized into two main packages: 
- ```2dhmsr```, taken from [2D highly modular soft robots](https://github.com/ericmedvet/2dhmsr), where all the elements required to perform the simulation of VSRs are included; and
- ```evolution```, which contains all the components used to perform the evolutionary optimization, i.e., the *evolution*, of VSRs. This also includes a dependency to [JGEA](https://github.com/ericmedvet/jgea), a general evolutionary algorithm (EA) framework written in Java, used for actually performing the optimization part. The jar for JGEA is already included in the ```lib``` folder.

## Suggested usage
### Visualizing a VSR
To visualize a developing VSR you need to run the ```DevelopmentExample``` class, which is included in the ```2dhmsr``` package.
This will start a simulation and will display a video of a comb shaped VSR moving downhill and developing, i.e., gaining additional voxels, at regular time intervals.
Note that in this case we are using a non-optimized VSR, hence most of its successful movement is caused by the inclination of the terrain.

### Performing an optimization
Evolutionary optimizations are performed by running the ```Starter``` class contained in ```evolution.devolocomotion```.
This will start the evolution of a development function for VSRs with the default parameters (listed at the beginning of the method ```run()```).
One can freely play with the parameters, which are mostly self-explanatory, either changing the source code or by using the corresponding key words from command line.
The most important ones are the ```devoFunction``` parameter, which describes the type of representation, and the ```evolver``` parameter, related to the EA used to perform the optimization.
It is fundamental that these two parameters refer to the same genotype, otherwise an ```Exception``` is thrown.
