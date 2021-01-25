# VBCLI DOCUMENTATION
Virtual Box CLI documentation

This CLI must be run by:

```sh
java -jar VBCLI.jar
```

## METHODS

You can execute specific operations by adding flags to the execution

- Virtual Box Test

```sh
java -jar VBCLI.jar -t
```

- List of Virtual Machines

```sh
java -jar VBCLI.jar -l
```

- Create a Virtual Machine

```sh
java -jar VBCLI.jar -c <Machine name> <Machine OS type> <RAM in MB> <Storage in GB> <ISO file path>
```

- Start a Virtual Machine

```sh
java -jar VBCLI.jar -s <Machine name>
```

- Shut down a Virtual Machine

```sh
java -jar VBCLI.jar -sd <Machine name>
```

- Delete a Virtual Machine

```sh
java -jar VBCLI.jar -d <Machine name>
```