# OSSBot
Project created by xmax, runs on 'Tribot' client and uses its API.

This bot runs in-game and can only be used so.
To execute commands, simple type the command into the CC chat beginning with **!!**

Each command has it's parameters briefly explained. Each command works off 'levels' in the hierarchy
So for example a command with the name of `command` has the folllowing parameters:

1. level1
	1. level2
	2. level2
		1. level3
2. level1

The command would be executed like this:

```
!!command level1 level2 level3
```

The **!!** characters are omitted in all the examples, as it's assumed you type them.


## Commands

#### `Announcement`

###### Parameters

1. `add`
	1. Message
		1. Interval
2. `remove`
	1. Valid announcement ID

Examples usage:

Adding:
```
announcement add 'An announcement that happens once every 30 minutes' 30m
```

Removing:
```
announcement remove 1
```
Will remove the announcement with the ID of 1.

ID's are generated incrementially.

#### `8ball`

###### Parameters

NONE

Input must end with a question mark.

Example usage:

```
!!8ball Are you really a bot?
```

#### `CML`

###### Parameters

1. Valid skill
	1. Interval
		2. [Player name]
2. `update`

Example usage:

```
cml strength 5d12h xmax
```
Will get the XP gains in the last 5 days and 12 hours for the player 'xmax'

```
cml update
```
Will update the player who called the command

#### `calc`

###### Parameters

1. A valid expression

Example usage:

```
calc 2*(60/3)+10
```
Will result in 50

#### `Caps`

###### Parameters

NONE

This command will captilise the entire string entered. Since all-caps are restricted normally in-game, the bot bypasses
them by using a special character in-between each character

Example usage:

```
caps I want to make it look like I am shouting
```
Will return: "I WANT TO MAKE IT LOOK LIKE I AM SHOUTING"

#### `Cluesolver`

###### Parameters

1. Cipher
	1. Something to decipher
2. Cryptic
	1. A cryptic clue to crack
3. Anagram
	1. An anagram to solve

Example usage:

```
cluesolver cipher 'hckta iqfhcvjgt'
```
Will return: Fairy Godfather

#### `Comp`

###### Parameters

1. `endofcomp`
	1. New CML competition ID
2. `timeleft`
3. `dpfund`

Example usage:

```
comp timeleft
```
Will return the time left till the next competition

```
comp dpfund
```
Will return the drop party fund (Uncollected competition money)

#### `Config`

###### Parameters

1. Valid command name
	1. `remove`
		1. `exception`
			1. Player name
		2. `restriction`
			1. Player name
		3. `abbreviation`
			1. An abbreviation for the command
	2. `add`
		1. `exception`
			1. Player name
		2. `restriction`
			1. Player name
		3. `abbreviation`
			1. An abbreviation for the command
	3. `disable`
		1. `true` or `false`
	4. `change`
		1. `minimumrank`
			1. `0` all the way to `7` (the rank number)
			
Example usage:

```
config calc add abbreviation c
```
Will allow uses to use `c` as the command name. I.e: `c` will become the same as `calc`

```
config 'comp endofcomp' add restriction xmax
```
Will change the `endofcomp` parameter restriction so that ONLY the user 'xmax' can use it.
This will not affect the whole command. Just the parameter and the parameters children.

```
config cml disable true
```
Will disable the `cml` command entirely

```
config cluesolver change minimumrank 0
```
Will change the minimum rank that's allowed to use the command to 0 which is the rank of 'unranked'

#### `Data`

###### Parameters

1. `favworld`
	1. [Player name]
2. `favchar`
	1. Same as above...
3. `chars`
4. `time`
5. `joindate`
6. `comprank`
7. `lastseen`
8. `lastworld`
9. `rank`
10. `days`
11. `compmoney`

Example usage:

```
data chars xmax
```
Will get the number of characters the user 'xmax' has typed in chat.

#### `Examine`

###### Parameters

1. Player name
	2. [New examine text]

Example usage:

```
examine xmax 'Some guy who progams'
```

Will set my examine to the text.

```
examine xmax
```
Will return the set examine.

#### `Fact`

###### Parameters

1. [`trivia`]
2. [`math`]
3. [`year`]
4. [`date`]

Example usage:

```
fact
```
Will return a random category fact

```
fact trivia
```
Will return a trivia fact.

This command uses the [number api] (http://numbersapi.com/)

#### `Flipcoin`

###### Parameters

NONE

Example usage:

```
flipcoin
```
Will return either heads or tails.

#### `help`

###### Parameters

1. Valid command name

Exmaple usage:

```
help comp
```
Will print all the parameters of the `comp` command

#### `Joke`

###### Parameters

NONE

Exmaple usage:

```
joke
```
Will return a random user submitted joke.
Warning: Some of these may be offensive

#### `Levelup`

###### Parameters

1. A valid level
	1. A valid skill
2. `add`
	1. A new custom message for this command

Example usage:

```
levelup 89 strength
```
Might return something like: 'Congratulation on 89 strength!'
Messages are random.

```
levelup add 'congratulations on {level} {skill}!'
```
Will add the new message to the list of possible printable messages. Where {level}, {skill} and {name} will be replaced
by the number, skill and player name respectively.

#### `Offsite`

###### Parameters

NONE

Example usage:

```
offsite
```
Will simply output the off-site web address

#### `poll`

###### Parameters

1. `comp`
	1. `vote`
		1. The valid vote
	2. `create`
		1. The three new skills separated by spaces

Example usage:

```
poll comp vote strength
```
Assuming strength is one of the 3 options in the poll, it will submit your vote towards it.

```
poll comp
```
Will return the current results of the poll

```
poll comp create 'skill1 skill2 skill3'
```
Will replace the current poll with the new poll that has the options: skill1, skill2 and skill3


#### `Price`

###### Parameters

1. [Amount]
	2. A valid item name
	
Example usage:

```
price 50 potato
```
Will return the price of 50 potatoes according to both the GE and OSB guide prices.

```
price potato
```
Will return the price of an indivual potato

#### `QFC`

###### Parameters

NONE

Example usage:

```
qfc
```
Will simply return the Quick-Find-Code to the forum thread of the CC

#### `Reverse`

###### Parameters

NONE

Example usage:

```
reverse invert this sentence
```
Will return: 'ecnetnes siht trevni'

#### `Screenie`

###### Parameters

NONE

Example usage:

```
screenie
```
The bot will take a screenshot of the chatbox and return the Imgur album link where the screenshot was uploaded.

#### `Time`

###### Parameters

1. [timezone]

Example usage

```
time
```
Will return the current date and time in UTC

```
time est
```
Will return the current date and time in ETC

#### `Top`

###### Parameters

1. `comp`
2. `charstyped`
3. `timespent`
4. `commandused`

Example usage:

```
top comp
```
Will return the top 3 players in the current weekly competition

```
top charstyped
```
Will return the top 3 players who typed the most characters in chat

```
top timespent
```
Will return the top 3 players who spent the most time online in the CC

```
top commandused
```
Will return the top 3 most used commands