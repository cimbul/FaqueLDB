# FaQeLDB

_The FaQe Ledger Database_

FaQeLDB is an in-memory database that aims to be API-compatible with Amazon Quantum Ledger Database (QLDB). To this end, it supports [PartiQL](https://partiql.org/) queries and serializes data using the [Amazon Ion](https://amzn.github.io/ion-docs/) format.

## Support

FaQeLDB currently supports:
 * **`SELECT` Queries**:
   * Everything supported by the [PartiQL reference implementation](https://github.com/partiql/partiql-lang-kotlin), including conditionals, joins, and navigating nested data structures
   * The [`BY` clause](https://docs.aws.amazon.com/qldb/latest/developerguide/working.metadata.by-clause.html) to retrieve document IDs
   * The [system catalog](https://docs.aws.amazon.com/qldb/latest/developerguide/working.catalog.html) (`information_schema`)
 * [**Functions**](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-functions.html):
   * Aggregations (`COUNT`, `MAX`, etc.)
   * Casts (`CAST`, `TO_STRING`, etc.)
   * Null handling (`COALESCE`, etc.)
   * Date/time (`UTCNOW`, etc.)
   * String utilities (`TRIM`, `UPPER`, etc.)
 * **DDL**:
   * [`CREATE TABLE`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.create-table.html) without tags
   * [`DROP TABLE`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.drop-table.html)
 * **DML**:
   * [`INSERT`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.insert.html) for single and multiple values

These features of QLDB are not currently supported, but are **on the roadmap**:
 * **DDL**:
   * [`CREATE INDEX`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.create-index.html) support is in progress, but it won't affect query performance
   * [`DROP INDEX`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.drop-index.html) support is in progress
   * [`UNDROP TABLE`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.undrop-table.html) is not supported by the PartiQL reference parser yet
 * **DML**:
   * [`DELETE`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.delete.html)
   * [`UPDATE`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.update.html)
   * [`FROM`...`INSERT`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.from.html)
   * [`FROM`...`REMOVE`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.from.html)
   * [`FROM`...`SET`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-reference.from.html)
 * **Functions**:
   * [`TXID`](https://docs.aws.amazon.com/qldb/latest/developerguide/ql-functions.txid.html)
   * [`HISTORY`](https://docs.aws.amazon.com/qldb/latest/developerguide/working.history.html)
 * True transaction support, including rollbacks
 * Multiple ledgers

These features of QLDB are **not supported** and are low priority:
 * Hashing & verification
 * The [control plane](https://docs.aws.amazon.com/qldb/latest/developerguide/API_Operations_Amazon_QLDB.html) and all its features, including ledger management, exports, streams, and tags

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
