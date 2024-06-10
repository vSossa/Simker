# Simker (**SIM**ple tas**K** manag**ER**)
It's a simple terminal app for task management. 
___
## Description
The operations on the app are intend to be similar to bash commands. Per example:

To see the tasks, the operation would be `ls`. To add delete one, `rm index`, where index is the index of the task you want to delete.

The tasks have a name, an optional description, and a status (open, in progress, or closed).

The tasks can be save in a file and loaded from a file. Simker has a standard csv file where it's going to save your tasks if you don't pass a file path to the `save` operation.
___
## Quick start
```console
$ javac -cp /src src/main/Main.java 
$ java main.Main [tasks.csv]
```

The csv file has the format:
|<status>|<name>|[description]|
|--------|------|-------------|
|0|"teste"|""|

The status can be 0, 1 or 2, meaning `open`, `in progress` and `closed`, respectively. The name is necessary, but the description isn't. If you don't want a description to a particular task, just leave it as `""`.
