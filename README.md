# VBCLI DOCUMENTATION
Virtual Box CLI documentation

This CLI must be run by:

`java -jar VBCLI.jar`

## METHODS

You can execute specific operations by adding flags to the execution

- Virtual Box Test

`java -jar VBCLI.jar -t`

- Create a Virtual Machine

`java -jar VBCLI.jar -c <Machine name> <Machine OS type> <RAM in MB> <Storage in GB> <ISO file path>`

- Start a Virtual Machine

`java -jar VBCLI.jar -s <Machine name>`

- Shut down a Virtual Machine

`java -jar VBCLI.jar -sd <Machine name>`

- Delete a Virtual Machine

`java -jar VBCLI.jar -d <Machine name>`