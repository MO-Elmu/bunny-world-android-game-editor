# bunny-world-android-game-editor
The goal is to implement an editor for a simple graphical adventure game. The game is called "Bunny World" after its most famous puzzle, but in reality, there's not much bunny in it. The project has two aspects: playing the game, which is relatively easy, and the game editor, which is a complete, graphical, document oriented, OOP/GUI project. This is the final project in CS108 at Stanford.
### Bunny World Rules
Bunny World is not actually specific to bunnies in any way, it's really a generic graphical world populated with pictures and sounds. At a basic level, Bunny World operates like a low-budget version of the classic computer game Myst. The basic elements of the game are... 
![image](https://user-images.githubusercontent.com/20994167/37741798-1711029e-2d20-11e8-8b6b-03a9fe7f0226.png)

##### 1) Pages
A page is a rectangular area with a white background that can contain a number of "shape" objects. Shapes or parts of shapes that fall outside the current page size are simply not drawn. The overall game (document) is made up of many pages.
##### 2) Shapes
Each shape belongs to a particular page, or the possessions area.
Each shape has a bounding rectangle that can be moved and resized. In the simplest case, a shape draws itself simply as a light gray rectangle.
##### 3) Shape Script
Every shape has a block of "script" text which programs how the shape behaves during the game. The script is structured as a set of "clauses" where each clause is a sequence of words separated from each other by whitespace, and the whole clause is ended by a semicolon (;). There are five script primitives that perform actions in the game. Multiple actions may be combined in a sequence, in which case they execute from left to right                                                                                                                
###### goto \<page-name\>    Switch to show the page of the given name.                                                                      
###### play \<sound-name\>   Play the sound of the given name.                                                                              
###### hide \<shape-name\>   Make the given shape invisible and un-clickable. The shape may or may not be on the currently displayed page.  
###### show \<shape-name\>   Make the given shape visible and active. The shape may or may not be on the currently displayed page.          
##### Script Triggers                                                                                                                   
###### on click \<actions\> Defines actions when the shape is clicked.                                                                   
###### on enter \<actions\> If the page this shape is currently in has just been "switched to" in the game, then perform the given actions. 
###### on drop \<shape-name\> <actions> Defines actions when the shape of the given name is dropped onto this shape.
##### 4) Possessions or Inventory Area
Just below the current the page there is a visually separate "possessions" area. As the player moves from page to page, the possessions area stays constant, and the shapes in it just sit there. During play, the possessions area enables the player to carry objects from one page to another. 
##### 5)Resources
The game document will use image and sound resources. Preload your Bunny World editor with images and sounds. The user adds the resources to the document by selecting them from your catalog of images and sounds.
