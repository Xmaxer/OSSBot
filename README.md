# OSSBot
Project created by xmax, runs on 'Tribot' client and uses its API.

## Commands

#### Announcement

1. `add`
	1. Message
		1. Interval in numerical WHMS order
2. `remove`
	1. Valid announcement ID

Examples usage:

Adding:
```
add 'An announcement that happens once every 30 minutes' 30m
```

Removing:
```
remove 1
```
Will remove the announcement with the ID of 1.

ID's are generated incrementially.

