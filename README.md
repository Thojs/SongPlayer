<img src="./src/main/resources/assets/songplayer/icon.png" alt="songplayer icon" width="200"/>

# SongPlayer
SongPlayer is a Fabric mod for Minecraft that plays songs with noteblocks.
The current version is for Minecraft 1.20.4.

## How it works
SongPlayer places noteblocks with nbt and instrument data already in them, so the noteblocks do not need to be individually tuned.

The client will automatically detect what noteblocks are needed and place them automatically before each song is played, which makes playing songs quite easy. The only drawback is that you need to be able to switch between creative and survival mode, which my client will attempt to do automatically.

When playing a song, freecam is enabled. You will be able to move around freely, but in reality you are only moving your camera while your player stays at the center of the noteblocks. This is because noteblocks can only be played if you're within reach distance of them, so you have to stand at the center of the noteblocks to play them, but it's still nice to be able to move around while your song is playing.


## How to install
To install the mod download it from the release section and place it in your `mods` folder.<br>
This mod also requires fabric api to function properly. Download it [here](https://www.curseforge.com/minecraft/mc-mods/fabric-api)

## How to use

### Adding songs
To add songs to SongPlayer you can place any of the supported formats inside the `.minecraft/SongPlayer/songs` folder.

You are able to organize songs easily by placing them into subdirectories. Tab completion will make it easy to navigate. Symlinked directories are supported too.

#### Supported formats
- Any valid midi file.
- NBS files (all versions)
- SP (SongPlayers format, used in item feature)

### Playing songs
To play songs use the `/sp play <filename|url>` command.<br>
When executing the command the mod will try to set your gamemode to creative, place the required noteblocks for the song, try to switch you to survival, then start playing.


## Commands
Users are able to control SongPlayer by using the brigadier `/songplayer` or `/sp` commands.

There are some sub-commands that will be discussed here:

### play \<filename or url>
Plays a particular midi from the `.minecraft/SongPlayer/songs` folder, or, if an url is specified, parses the song at that url and tries to play it.

If there is a song already playing, the new song will be added to the queue.

### stop
Stops playing or building and clears the queue.

### skip
Skips the current song and goes to the next one.

### goto \<mm:ss>
Goes to a specific time in the current playing song.

### loop
Toggles the looping mode on the current song.

### status

Gets the status of the current song that is playing.

### queue

Shows all the songs in the queue.


[//]: # (Playlist function is not really important, I don't use it on a regular basis.)

[//]: # (### playlist play \<playlist>)

[//]: # (### playlist create \<playlist>)

[//]: # (### playlist list \[\<playlist>])

[//]: # (### playlist delete \<playlist> \<song>)

[//]: # (### playlist addSong \<playlist> \<song>)

[//]: # (### playlist removeSong \<playlist> \<song>)

[//]: # (### playlist renameSong \<playlist> \<old name> \<new name>)

[//]: # (### playlist loop)

[//]: # (### playlist shuffle)

[//]: # ()
[//]: # (Create, edit, delete, or play playlists. You can also toggle looping or shuffling.)

### songs (subdirectory)

If no arguments are given, lists songs in the `songs` folder. Otherwise, lists songs in the specified subdirectory.

### setCommands use \<vanilla|essentials>
Switch to using Essentials or vanilla gamemode commands.

### setCommands creative \<command>
Sets the command that will be used to switch to creative mode.

### setCommands survival \<command>
Sets the command that will be used to switch to survival mode.

### toggleFakePlayer

Toggles whether a fake player will show up to represent your true position while playing a song. When playing a song, since it automatically enables freecam, your true position will be different from your apparent position. The fake player will show where you actually are. By default, this is disabled.

### setStageType \<DEFAULT | WIDE | SPHERICAL>

Sets the type of noteblock stage to build. Thanks Sk8kman and Lizard16 for the spherical stage design!
- Default: A square shaped stage with a maximum of 300 noteblocks
- Wide: A cylindrical stage with a maximum of 360 noteblocks
- Spherical: A densely packed spherical stage that can contain all 400 possible noteblocks

### toggleMovement \<swing | rotate>

Toggles whether you swing your arm when hitting a noteblock and rotate to look at the noteblocks you are hitting.

### announcement \<enable | disable | getMessage>
### announcement setMessage

Set an announcement message that is sent when you start playing a song.
With setMessage, write `[name]` where the song name should go.

Example: `$announcement setMessage &6Now playing: &3[name]`

### songItem create \<song or url>
### songItem setSongName \<name>

Encodes song data into an item. When you right-click on such an item, SongPlayer will automatically detect that it is a song item and ask if you want to play it. These items, once created, can be used by anyone that is using the necessary version of SongPlayer.

It will automatically generate custom item names and lore, but these can be modified or deleted without affecting the song data, so feel free to edit the items as you wish. SongPlayer only looks at the `SongItemData` tag.

### testSong
A command used for testing during development.
It plays all 400 possible noteblock sounds in order (if possible).

## Acknowledgements
**Ayunami2000**: Came up with the concept of directly placing noteblocks with nbt data instead of manually tuning them.

**Sk8kman**: Several of Songplayer 3.0's changes were inspired by their fork of SongPlayer. Most notably was their alternate stage designs, but it also motivated me to implement playlists and togglable movements.

**Lizard16**: Cited by Sk8kman as the person who made the spherical stage design.

