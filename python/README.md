# python objectstore examples

 * boto3 [src](https://github.com/boto/boto3) [doc](https://boto3.amazonaws.com/v1/documentation/api/latest/index.html)
 * [rda-object-storage](https://github.com/NCAR/rda-object-storage)

## Development

Consider Jupyter notebooks or virtualenv.
A simple example of the latter:

    (install python3)
    virtualenv-3 venv
    . venv/bin/activate
    pip install boto3
    python myscripty.py

Please `pip freeze` and git-ignore the `venv`.
(There is a `.gitignore` file in this directory
which may suffice for your project subdir.)
See:
 * [Python.gitignore](https://github.com/github/gitignore/blob/master/Python.gitignore)
 * [stackoverflow](https://stackoverflow.com/questions/6590688/is-it-bad-to-have-my-virtualenv-directory-inside-my-git-repository)

