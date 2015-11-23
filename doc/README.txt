!WARNING!
This is beta quality software! It has many security holes in it and is intended only for testing purposes.

Quick Howto
To start the server, use the bin/run.sh script, or the bin/bitcoop init script. This has been tested under linux and OS X. A windows port is under way. To build bitcoop, take these steps:
1- cd bitccop
2- ant
3- cd native
4- scons
5- mv libBitCoop* ..
6- cd ..
7- edit bcoop.xml
You should have to change pretty much everything in that file.

8- bin/run.sh

Do this on all the machines you want to use.

How this works
- You run the bitcoop server on each machine that participate on your network.
- You can schedule backups to occur at specified times
- You can use the command line client bin/shell.sh to connect to computers to trigger backups or see some status information.
- The first backup always takes more time since all the files have to be copied. Subsequent backups will be much faster.

Troubleshooting
- Contact the author Philippe Marchesseault pmarchesso@sourceforge.net
