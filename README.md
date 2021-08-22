# FaQeLDB

_The FaQe Ledger Database_

FaQeLDB is an in-memory database that aims to be API-compatible with Amazon Quantum Ledger Database (QLDB). To this end, it supports [PartiQL](https://partiql.org/) queries and serializes data using the [Amazon Ion](https://amzn.github.io/ion-docs/) format.

## Usage

### Run

#### Start Server

```shell
$ ./gradlew run
```

#### Run Queries

Using the [QLDB Shell](https://github.com/awslabs/amazon-qldb-shell):

```shell
$ qldb -s http://localhost:8000 -l test
qldbshell > create table foo
qldbshell > insert into foo << {'x': 1}, {'x': 2} >>
qldbshell > select * from foo
```

### Test

```shell
$ ./gradlew test
```
