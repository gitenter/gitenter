# Ssheep

The SqlAlchemy executions for

(1) Generating `.ssh/authorized_keys` content to fill in  `AuthorizedKeysCommand` setting up in `sshd_config`.

```
python3 get_authorized_keys_content.py
```

## Run Tests

Any test runner can be used (as we are using `unittest.TestCase`) but in `Pipfile` we specify `pytest`.

```
sudo pip install pipenv
pipenv install --dev
pipenv run pytest
```
