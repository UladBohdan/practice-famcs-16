Console App to manipulate your messages stored in JSON-format.

![Screenshot](http://res.cloudinary.com/dhgy4yket/image/upload/v1457363777/practice-famcs-16/CCP.png)

# List of available commands

**load [file]** - load messages from file. Previous data will be lost

**add [author] [message]** - add new message (id&time are given automatically)

**show [-f] [time to start] [time to end]** - show messages in chronological order (parameters are optional)

**remove [id]** - remove message by id

**save [file]** - save messages to a file (file is optional)

**search [-author/-keyword/-regex] [author/keyword/regex]** - search by a parameter

**clear** - clears all the messages

General format for datetime: 2011-12-03T10:15:30

# Dependencies
Gson-2.3.1 (.jar file is included to repository)
