# objectstore-examples

Example code for the NCAR
[Object storage test system](https://www2.cisl.ucar.edu/resources/storage-and-file-systems/object-storage-test-system).

Repo access will be freely given to any UCAR/NCAR employees.
(I hestitated to try to add everybody for fear of spamming
with github emails.) You may also fork and pull-request.

## Development

Subdirs are organized by `language/topic`.
Please do not work on the `master` branch.
Create and commit on your own branch until it is stable.
Then merge to master.

Most compatible libraries will find your AWS keys in `~/.aws/credentials`
or in environment variables `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`.
Also remember to change your endpoint URI to NCAR's system:
`https://stratus.ucar.edu`
