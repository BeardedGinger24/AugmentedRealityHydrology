## Augmented Reality for Hydrology
The purpose of this project is to:
1) Provide an Augmented Reality Android App that displays Hydrology Data from JPL's Watertrek database.
2) Extract generally useful and reusable components from the app, and place them in a framework.

### Modules
The android project consists of 3 modules:
1) 'arlib' - The Augmented Reality Framework
2) 'demo' - A Demo App (to demonstrate the framework's capabilities in a simple manner).
3) 'watertrekapp' - The Main App (which actually displays live data from JPL's Watertrek database using the framework).

### What is Augmented Reality (AR)
Augmented Reality is the ability to seemingly place computer generated objects in the real world. This is done by superimposing computer graphics on top of the user's view of the world. This can be done, for example, by drawing 3D graphics on top of a camera preview.

### The two approaches to Augmented Reality
There are basically two approaches to Augmented Reality:

1) Use Computer Vision Techniques to tell how the device is moving, detect real world object in the real world that can be seen by the user, and line up computer graphics with real world objects
2) Use the device's sensors to obtain the device's position and orientation. Use this information with the rendered object's known position to correctly place the computer graphics on top of the camera preview.

An AR Framework can use one or a combination of these approaches. There are advantages and drawbacks for each. One drawback of using sensors to detect device position in the world is that GPS readings are typically not very precise (can be off by average of 20 meters in our experience). This can make rendered objects jump when the GPS readings update and not line up well. Computer Vision techniques are good at detecting exactly where on the camera preview an object is located, and thus allowing us to line up computer graphics with the real world objects. But the algorithms are complicated, potentially slow, and can only detect certain types of real world objects (like QR codes or corners of the room). They also will not do well when the view becomes obstructed. For example, Computer Vision is useless in telling us the correct position of a nearby building when we are inside another building, or a tree is in the way. In these kinds of cases, it is more useful to use sensors and the known positions of objects to correctly line up computer graphics with the real world.

In this project, we are currently using the second approach, since our purpose is to show the location of distant Hydrological/Geological features that are likely to be obstructed. But, later on, the first approach may be utilized as well in order to provide more accurate positioning and tracking.


### Functionality Provided by the framework
In order to provide AR functionality, the following 3 things must be provided.
1) A surface that allows displaying the device's camera output, with 3D computer graphics drawn on top.
2) Access to the device's sensors (e.g. GPS, Accelerometer, Gyroscope, Magnetic Field, etc)
3) A simple 3D rendering framework.
If you have these three things you can draw 3D graphics on top of the 'real world'/camera preview, and properly line up these graphics with the rest of the world; even as the device is moved and rotated. These functionalities are provided by classes in the 'ui', 'sensor', and 'graphics3d' packages of the project.

In addition, the classes are provided to make certain specific tasks easier:
4) Convert between X, Y, Z coordinates and Latitude, Longitude, Altitude coordinates
5) Check for and Request Permissions on the Android Device for things like Location, Internet Access, External Storage
6) Performing Vector Math Operations
These classes are provided in the 'util' package

### Brief description of Framework classes

### Sample usage of framework

### Screenshots and Links to Videos


